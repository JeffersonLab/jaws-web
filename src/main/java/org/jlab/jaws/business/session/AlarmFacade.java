package org.jlab.jaws.business.session;

import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.AlarmProducer;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.AlarmSyncDiff;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class AlarmFacade extends AbstractFacade<AlarmEntity> {
  private static final Logger logger = Logger.getLogger(AlarmFacade.class.getName());

  @EJB LocationFacade locationFacade;

  @EJB ActionFacade actionFacade;

  @EJB SyncRuleFacade syncRuleFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public AlarmFacade() {
    super(AlarmEntity.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<AlarmEntity> root,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean synced,
      String pv,
      String alarmName,
      String actionName,
      String componentName) {
    List<Predicate> filters = new ArrayList<>();

    Join<Alarm, Action> actionJoin = root.join("action");
    Join<Action, SystemEntity> systemJoin = actionJoin.join("system");

    if (locationIdArray != null && locationIdArray.length > 0) {
      // Parent locations imply children locations
      Set<Location> materializedLocations = new HashSet<>();
      for (BigInteger locationId : locationIdArray) {
        if (locationId != null) {
          Set<Location> subset = locationFacade.findBranchAsSet(locationId);
          materializedLocations.addAll(subset);
        }
      }

      List<BigInteger> locationIdList = new ArrayList<>();

      for (Location l : materializedLocations) {
        locationIdList.add(l.getLocationId());
      }

      if (!locationIdList.isEmpty()) {
        Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
        Root<Location> subqueryRoot = subquery.from(Location.class);
        Join<Location, Alarm> alarmJoin = subqueryRoot.join("alarmList");
        subquery.select(alarmJoin.get("alarmId"));
        subquery.where(subqueryRoot.get("locationId").in(locationIdList));
        filters.add(cb.in(root.get("alarmId")).value(subquery));
      }
    }

    if (synced != null) {
      if (synced) {
        filters.add(cb.isNotNull(root.get("syncElementId")));
      } else {
        filters.add(cb.isNull(root.get("syncElementId")));
      }
    }

    if (pv != null && !pv.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("pv")), pv.toLowerCase()));
    }

    if (alarmName != null && !alarmName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), alarmName.toLowerCase()));
    }

    if (priorityId != null) {
      filters.add(cb.equal(actionJoin.get("priority"), priorityId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(actionJoin.get("name")), actionName.toLowerCase()));
    }

    if (componentName != null && !componentName.isEmpty()) {
      filters.add(cb.like(cb.lower(systemJoin.get("name")), componentName.toLowerCase()));
    }

    if (teamId != null) {
      filters.add(cb.equal(systemJoin.get("team"), teamId));
    }

    return filters;
  }

  @PermitAll
  public List<AlarmEntity> filterList(
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean synced,
      String pv,
      String alarmName,
      String actionName,
      String componentName,
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AlarmEntity> cq = cb.createQuery(AlarmEntity.class);
    Root<AlarmEntity> root = cq.from(AlarmEntity.class);
    cq.select(root);

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            locationIdArray,
            priorityId,
            teamId,
            synced,
            pv,
            alarmName,
            actionName,
            componentName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public long countList(
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean synced,
      String pv,
      String alarmName,
      String actionName,
      String componentName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<AlarmEntity> root = cq.from(AlarmEntity.class);

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            locationIdArray,
            priorityId,
            teamId,
            synced,
            pv,
            alarmName,
            actionName,
            componentName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @PermitAll
  public AlarmEntity findByName(String name) {
    List<AlarmEntity> list = this.filterList(null, null, null, null, null, name, null, null, 0, 1);

    AlarmEntity entity = null;

    if (list != null && !list.isEmpty()) {
      entity = list.get(0);
    }

    return entity;
  }

  @PermitAll
  public List<AlarmEntity> findByRule(SyncRule rule) {
    List<AlarmEntity> list = null;

    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AlarmEntity> cq = cb.createQuery(AlarmEntity.class);
    Root<AlarmEntity> root = cq.from(AlarmEntity.class);
    cq.select(root);

    cq.where(cb.equal(root.get("syncRule"), rule));

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);
    list = getEntityManager().createQuery(cq).getResultList();

    return list;
  }

  @RolesAllowed("jaws-admin")
  public void addAlarm(
      String name,
      BigInteger actionId,
      BigInteger[] locationIdArray,
      String alias,
      String device,
      String screenCommand,
      String managedBy,
      String maskedBy,
      String pv,
      String elementName,
      BigInteger syncRuleId,
      BigInteger elementId)
      throws UserFriendlyException {
    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (actionId == null) {
      throw new UserFriendlyException("Action is required");
    }

    Action action = actionFacade.find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    List<Location> locationList = new ArrayList<>();

    if (locationIdArray != null && locationIdArray.length > 0) {
      for (BigInteger id : locationIdArray) {
        if (id == null) { // TODO: the convertBigIntegerArray method should be excluding empty/null
          continue;
        }

        Location l = locationFacade.find(id);

        if (l != null) {
          locationList.add(l);
        }
      }
    }

    SyncRule rule = null;

    if (syncRuleId != null) {

      if (elementId == null) {
        throw new UserFriendlyException("Element ID is required if sync rule ID is provided");
      }

      rule = syncRuleFacade.find(syncRuleId);

      if (rule == null) {
        throw new UserFriendlyException("Sync Rule with ID " + syncRuleId + " not found");
      }
    }

    if (syncRuleId == null && elementId != null) {
      throw new UserFriendlyException("Sync rule ID is required if element ID is provided");
    }

    AlarmEntity alarm = new AlarmEntity();

    alarm.setName(name);
    alarm.setAction(action);
    alarm.setLocationList(locationList);
    alarm.setAlias(alias);
    alarm.setDevice(device);
    alarm.setScreenCommand(screenCommand);
    alarm.setManagedBy(managedBy);
    alarm.setMaskedBy(maskedBy);
    alarm.setPv(pv);
    alarm.setSyncElementName(elementName);
    alarm.setSyncRule(rule);
    alarm.setSyncElementId(elementId);

    create(alarm);

    this.kafkaSet(Arrays.asList(alarm));
  }

  @RolesAllowed("jaws-admin")
  public void kafkaSet(List<AlarmEntity> alarmList) {
    if (alarmList != null) {
      try (AlarmProducer producer = new AlarmProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (AlarmEntity alarm : alarmList) {
          String key = alarm.getName();

          Alarm value = new Alarm();

          value.setAction(alarm.getAction().getName());

          Object source = new Source();

          if (alarm.getPv() != null) {
            source = new EPICSSource(alarm.getPv());
          }

          value.setSource(source);
          value.setLocation(alarm.getLocationNameList());
          value.setManagedby(alarm.getManagedBy());
          value.setMaskedby(alarm.getMaskedBy());
          value.setScreencommand(alarm.getScreenCommand());

          producer.send(key, value);
        }
      }
    }
  }

  @RolesAllowed("jaws-admin")
  public void kafkaUnset(List<String> list) {
    if (list != null) {
      try (AlarmProducer producer = new AlarmProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (String name : list) {
          producer.send(name, null);
        }
      }
    }
  }

  @RolesAllowed("jaws-admin")
  public void removeAlarm(BigInteger alarmId) throws UserFriendlyException {
    if (alarmId == null) {
      throw new UserFriendlyException("Alarm ID is required");
    }

    AlarmEntity alarm = find(alarmId);

    if (alarm == null) {
      throw new UserFriendlyException("Alarm not found with ID: " + alarmId);
    }

    remove(alarm);

    kafkaUnset(Arrays.asList(alarm.getName()));
  }

  @RolesAllowed("jaws-admin")
  public void editAlarm(
      Set<String> editableParams,
      BigInteger alarmId,
      String name,
      BigInteger actionId,
      BigInteger[] locationIdArray,
      String alias,
      String device,
      String screenCommand,
      String managedBy,
      String maskedBy,
      String pv,
      String elementName,
      BigInteger syncRuleId,
      BigInteger elementId)
      throws UserFriendlyException {
    if (alarmId == null) {
      throw new UserFriendlyException("Alarm ID is required");
    }

    AlarmEntity alarm = find(alarmId);

    if (alarm == null) {
      throw new UserFriendlyException("Alarm not found with ID: " + alarmId);
    }

    if (editableParams.contains("actionId")) {
      if (actionId == null) {
        throw new UserFriendlyException("Action is required");
      }

      Action action = actionFacade.find(actionId);

      if (action == null) {
        throw new UserFriendlyException("Action not found with ID: " + actionId);
      }

      alarm.setAction(action);
    }

    if (editableParams.contains("name")) {
      alarm.setName(name);
    }

    if (editableParams.contains("alias")) {
      alarm.setAlias(alias);
    }

    if (editableParams.contains("device")) {
      alarm.setDevice(device);
    }

    if (editableParams.contains("screenCommand")) {
      alarm.setScreenCommand(screenCommand);
    }

    if (editableParams.contains("managedBy")) {
      alarm.setManagedBy(managedBy);
    }

    if (editableParams.contains("maskedBy")) {
      alarm.setMaskedBy(maskedBy);
    }

    if (editableParams.contains("pv")) {
      alarm.setPv(pv);
    }

    if (editableParams.contains("locationId[]")) {
      List<Location> locationList = new ArrayList<>();

      if (locationIdArray != null && locationIdArray.length > 0) {
        for (BigInteger id : locationIdArray) {
          if (id
              == null) { // TODO: the convertBigIntegerArray method should be excluding empty/null
            continue;
          }

          Location l = locationFacade.find(id);

          if (l != null) {
            locationList.add(l);
          }
        }
      }

      alarm.setLocationList(locationList);
    }

    if (editableParams.contains("syncRuleId")) {
      SyncRule rule = null;

      if (syncRuleId != null) {

        if (elementId == null) {
          throw new UserFriendlyException("Element ID is required if sync rule ID is provided");
        }

        rule = syncRuleFacade.find(syncRuleId);

        if (rule == null) {
          throw new UserFriendlyException("Sync Rule with ID " + syncRuleId + " not found");
        }
      }

      if (syncRuleId == null && elementId != null) {
        throw new UserFriendlyException("Sync rule ID is required if element ID is provided");
      }

      alarm.setSyncRule(rule);
    }

    if (editableParams.contains("syncElementName")) {
      alarm.setSyncElementName(elementName);
    }

    if (editableParams.contains("syncElementId")) {
      alarm.setSyncElementId(elementId);
    }

    edit(alarm);

    kafkaSet(Arrays.asList(alarm));
  }

  @RolesAllowed("jaws-admin")
  public void addKeyValueList(String alarms) throws UserFriendlyException {
    if (alarms == null || alarms.isBlank()) {
      throw new UserFriendlyException("alarms must not be empty");
    }

    List<String> lines = new ArrayList<>();

    alarms.lines().forEach(lines::add);

    for (String line : lines) {
      if (!line.isBlank()) {
        parseAlarmLine(line);
      }
    }
  }

  private void parseAlarmLine(String line) throws UserFriendlyException {

    System.out.println("line: " + line);

    String[] tokens = line.split("=", 2); // split on FIRST "=" only

    if (tokens.length != 2) { // Make sure there was at least one!
      throw new UserFriendlyException("Invalid alarm line: " + line);
    }

    String name = tokens[0];
    String json = tokens[1];

    System.out.println("token 0: " + tokens[0]);
    System.out.println("token 1: " + tokens[1]);

    JsonReader reader = Json.createReader(new StringReader(json));

    JsonObject object = reader.readObject();

    String actionName = object.getString("alarmclass");

    List<String> locationNames = new ArrayList<>();

    if (!object.isNull("location")) {
      JsonArray locationArray = object.getJsonArray("location");

      for (int i = 0; i < locationArray.size(); i++) {
        String location = locationArray.getString(i);
        locationNames.add(location);
      }
    }

    String device = null;

    if (object.containsKey("device") && !object.isNull("device")) {
      device = object.getString("device");
    }

    String screenCommand = null;

    if (!object.isNull("screencommand")) {
      screenCommand = object.getString("screencommand");
    }

    String managedBy = null;

    if (!object.isNull("managedby")) {
      managedBy = object.getString("managedby");
    }

    String maskedBy = null;

    if (!object.isNull("maskedby")) {
      maskedBy = object.getString("maskedby");
    }

    JsonObject source = object.getJsonObject("source");
    String pv = null;

    if (source.containsKey("org.jlab.jaws.entity.EPICSSource")) {
      JsonObject epicsSource = source.getJsonObject("org.jlab.jaws.entity.EPICSSource");

      if (!epicsSource.isNull("pv")) {
        pv = epicsSource.getString("pv");
      }
    }

    Action action = actionFacade.findByName(actionName);

    if (action == null) {
      throw new UserFriendlyException("Action not found: " + actionName);
    }

    List<Location> locationList = new ArrayList<>();
    List<BigInteger> locationIdList = new ArrayList<>();

    for (int i = 0; i < locationNames.size(); i++) {
      Location location = locationFacade.findByName(locationNames.get(i));

      if (location == null) {
        throw new UserFriendlyException("Location not found: " + locationNames.get(i));
      }

      locationList.add(location);
      locationIdList.add(location.getId());
    }

    addAlarm(
        name,
        action.getActionId(),
        locationIdList.toArray(new BigInteger[0]),
        null,
        device,
        screenCommand,
        managedBy,
        maskedBy,
        pv,
        null,
        null,
        null);
  }

  @PermitAll
  public AlarmSyncDiff diff(
      LinkedHashMap<BigInteger, AlarmEntity> remoteList, List<AlarmEntity> localList) {
    AlarmSyncDiff diff = new AlarmSyncDiff();

    LinkedHashMap<BigInteger, AlarmEntity> addList = new LinkedHashMap<>(remoteList);

    for (AlarmEntity local : localList) {
      if (local.getSyncElementId() == null) {
        diff.removeList.add(local);
      } else {
        AlarmEntity remote = remoteList.get(local.getSyncElementId());

        if (remote == null) {
          diff.removeList.add(local);
        } else {
          addList.remove(local.getSyncElementId());
          if (local.syncEquals(remote)) {
            diff.matchList.add(local);
          } else {
            diff.updateList.add(local);
          }
        }
      }
    }

    diff.addList.addAll(addList.values());

    return diff;
  }

  @PermitAll
  public LinkedHashMap<String, AlarmEntity> findDanglingByName(List<AlarmEntity> addList) {
    LinkedHashMap<String, AlarmEntity> map = new LinkedHashMap<>();

    if (addList != null && !addList.isEmpty()) {
      List<String> names = new ArrayList<>();

      // SQL in statement is limited to 1000 items
      for (AlarmEntity a : addList.subList(0, Math.min(addList.size(), 1000))) {
        names.add(a.getName());
      }

      String jpql = "select a from AlarmEntity a where a.name in :names";

      TypedQuery<AlarmEntity> query = em.createQuery(jpql, AlarmEntity.class);

      query.setParameter("names", names);

      List<AlarmEntity> resultList = query.getResultList();

      if (resultList != null) {
        for (AlarmEntity a : resultList) {
          map.put(a.getName(), a);
        }
      }
    }

    return map;
  }

  @PermitAll
  public LinkedHashMap<String, AlarmEntity> findDanglingByPv(List<AlarmEntity> addList) {
    LinkedHashMap<String, AlarmEntity> map = new LinkedHashMap<>();

    if (addList != null && !addList.isEmpty()) {
      List<String> pvs = new ArrayList<>();

      // SQL in statement is limited to 1000 items
      for (AlarmEntity a : addList.subList(0, Math.min(addList.size(), 1000))) {
        pvs.add(a.getPv());
      }

      String jpql = "select a from AlarmEntity a where a.pv in :pvs";

      TypedQuery<AlarmEntity> query = em.createQuery(jpql, AlarmEntity.class);

      query.setParameter("pvs", pvs);

      List<AlarmEntity> resultList = query.getResultList();

      if (resultList != null) {
        for (AlarmEntity a : resultList) {
          map.put(a.getPv(), a);
        }
      }
    }

    return map;
  }
}
