package org.jlab.jaws.business.session;

import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.ActionProducer;
import org.jlab.jaws.entity.AlarmAction;
import org.jlab.jaws.entity.AlarmPriority;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Priority;
import org.jlab.jaws.persistence.entity.SystemEntity;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class ActionFacade extends AbstractFacade<Action> {
  private static final Logger logger = Logger.getLogger(ActionFacade.class.getName());

  @EJB SystemFacade systemFacade;

  @EJB PriorityFacade priorityFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ActionFacade() {
    super(Action.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      Root<Action> root,
      BigInteger priorityId,
      BigInteger teamId,
      String actionName,
      String systemName) {
    List<Predicate> filters = new ArrayList<>();

    if (priorityId != null) {
      filters.add(cb.equal(root.get("priority"), priorityId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), actionName.toLowerCase()));
    }

    Join<Action, SystemEntity> systemJoin = null;

    if (systemName != null && !systemName.isEmpty() || teamId != null) {
      systemJoin = root.join("system");
    }

    if (systemName != null && !systemName.isEmpty()) {
      filters.add(cb.like(cb.lower(systemJoin.get("name")), systemName.toLowerCase()));
    }

    if (teamId != null) {
      filters.add(cb.equal(systemJoin.get("team"), teamId));
    }

    return filters;
  }

  @PermitAll
  public List<Action> filterList(
      BigInteger priorityId,
      BigInteger teamId,
      String actionName,
      String systemName,
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Action> cq = cb.createQuery(Action.class);
    Root<Action> root = cq.from(Action.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, priorityId, teamId, actionName, systemName);

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
      BigInteger priorityId, BigInteger teamId, String actionName, String systemName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Action> root = cq.from(Action.class);

    List<Predicate> filters = getFilters(cb, root, priorityId, teamId, actionName, systemName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @PermitAll
  public Action findByName(String name) {
    List<Action> list = this.filterList(null, null, name, null, 0, 1);

    Action entity = null;

    if (list != null && !list.isEmpty()) {
      entity = list.get(0);
    }

    return entity;
  }

  @RolesAllowed("jaws-admin")
  public void addAction(
      String name,
      BigInteger systemId,
      BigInteger priorityId,
      String correctiveAction,
      String rationale,
      Boolean filterable,
      Boolean latchable,
      BigInteger onDelaySeconds,
      BigInteger offDelaySeconds)
      throws UserFriendlyException {
    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (systemId == null) {
      throw new UserFriendlyException("System is required");
    }

    SystemEntity system = systemFacade.find(systemId);

    if (system == null) {
      throw new UserFriendlyException("System not found with ID: " + systemId);
    }

    if (priorityId == null) {
      throw new UserFriendlyException("Priority is required");
    }

    Priority priority = priorityFacade.find(priorityId);

    if (priority == null) {
      throw new UserFriendlyException("Priority not found with ID: " + priorityId);
    }

    if (correctiveAction == null || correctiveAction.isBlank()) {
      throw new UserFriendlyException("Corrective Action is required");
    }

    if (rationale == null || rationale.isBlank()) {
      throw new UserFriendlyException("Rationale is required");
    }

    if (filterable == null) {
      throw new UserFriendlyException("Filterable is required");
    }

    if (latchable == null) {
      throw new UserFriendlyException("Latchable is required");
    }

    Action action = new Action();

    action.setName(name);
    action.setSystem(system);
    action.setPriority(priority);
    action.setCorrectiveAction(correctiveAction);
    action.setRationale(rationale);
    action.setFilterable(filterable);
    action.setLatchable(latchable);
    action.setOnDelaySeconds(onDelaySeconds);
    action.setOffDelaySeconds(offDelaySeconds);

    create(action);

    kafkaSet(Arrays.asList(action));
  }

  @RolesAllowed("jaws-admin")
  public void removeAction(BigInteger actionId) throws UserFriendlyException {
    if (actionId == null) {
      throw new UserFriendlyException("Action ID is required");
    }

    Action action = find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    remove(action);

    kafkaUnset(Arrays.asList(action.getName()));
  }

  @RolesAllowed("jaws-admin")
  public void editAction(
      BigInteger actionId,
      String name,
      BigInteger systemId,
      BigInteger priorityId,
      String correctiveAction,
      String rationale,
      Boolean filterable,
      Boolean latchable,
      BigInteger onDelaySeconds,
      BigInteger offDelaySeconds)
      throws UserFriendlyException {
    if (actionId == null) {
      throw new UserFriendlyException("Action ID is required");
    }

    Action action = find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (systemId == null) {
      throw new UserFriendlyException("System is required");
    }

    SystemEntity system = systemFacade.find(systemId);

    if (system == null) {
      throw new UserFriendlyException("System not found with ID: " + systemId);
    }

    if (priorityId == null) {
      throw new UserFriendlyException("Priority is required");
    }

    Priority priority = priorityFacade.find(priorityId);

    if (priority == null) {
      throw new UserFriendlyException("Priority not found with ID: " + priorityId);
    }

    if (correctiveAction == null || correctiveAction.isBlank()) {
      throw new UserFriendlyException("Corrective Action is required");
    }

    if (rationale == null || rationale.isBlank()) {
      throw new UserFriendlyException("Rationale is required");
    }

    if (filterable == null) {
      throw new UserFriendlyException("Filterable is required");
    }

    if (latchable == null) {
      throw new UserFriendlyException("Latchable is required");
    }

    action.setName(name);
    action.setSystem(system);
    action.setPriority(priority);
    action.setCorrectiveAction(correctiveAction);
    action.setRationale(rationale);
    action.setFilterable(filterable);
    action.setLatchable(latchable);
    action.setOnDelaySeconds(onDelaySeconds);
    action.setOffDelaySeconds(offDelaySeconds);

    edit(action);

    kafkaSet(Arrays.asList(action));
  }

  @RolesAllowed("jaws-admin")
  public void addActionKeyValueList(String actions) throws UserFriendlyException {
    if (actions == null || actions.isBlank()) {
      throw new UserFriendlyException("actions must not be empty");
    }

    List<String> lines = new ArrayList<>();

    actions.lines().forEach(lines::add);

    for (String line : lines) {
      if (!line.isBlank()) {
        parseActionLine(line);
      }
    }
  }

  private void parseActionLine(String line) throws UserFriendlyException {

    System.out.println("line: " + line);

    String[] tokens = line.split("=", 2); // split on FIRST "=" only

    if (tokens.length != 2) { // Make sure there was at least one!
      throw new UserFriendlyException("Invalid action line: " + line);
    }

    String name = tokens[0];
    String json = tokens[1];

    System.out.println("token 0: " + tokens[0]);
    System.out.println("token 1: " + tokens[1]);

    JsonReader reader = Json.createReader(new StringReader(json));

    JsonObject object = reader.readObject();

    String systemName = object.getString("system");
    String priorityName = object.getString("priority");
    String correctiveAction = null;

    if (!object.isNull("correctiveaction")) {
      correctiveAction = object.getString("correctiveaction");
    }

    String rationale = null;

    if (!object.isNull("rationale")) {
      rationale = object.getString("rationale");
    }

    Boolean filterable = true;

    if (!object.isNull("filterable")) {
      filterable = object.getBoolean("filterable");
    }

    Boolean latchable = false;

    if (!object.isNull("latchable")) {
      latchable = object.getBoolean("latchable");
    }

    BigInteger onDelaySeconds = null;

    if (!object.isNull("ondelayseconds")) {
      onDelaySeconds = BigInteger.valueOf(object.getInt("ondelayseconds"));
    }

    BigInteger offDelaySeconds = null;

    if (!object.isNull("offdelayseconds")) {
      offDelaySeconds = BigInteger.valueOf(object.getInt("offdelayseconds"));
    }

    SystemEntity system = systemFacade.findByName(systemName);
    Priority priority = priorityFacade.findByName(priorityName);

    if (system == null) {
      throw new UserFriendlyException("System not found: " + systemName);
    }

    if (priority == null) {
      throw new UserFriendlyException("Priority not found: " + priorityName);
    }

    addAction(
        name,
        system.getSystemId(),
        priority.getPriorityId(),
        correctiveAction,
        rationale,
        filterable,
        latchable,
        onDelaySeconds,
        offDelaySeconds);
  }

  @RolesAllowed("jaws-admin")
  public void kafkaSet(List<Action> actionList) {
    if (actionList != null) {
      try (ActionProducer producer =
          new ActionProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (Action action : actionList) {
          String key = action.getName();

          AlarmAction value = new AlarmAction();

          value.setRationale(action.getRationale());

          String priorityName = action.getPriority().getName();
          AlarmPriority ap = AlarmPriority.valueOf(priorityName);
          value.setPriority(ap);

          value.setSystem(action.getSystem().getName());
          value.setCorrectiveaction(action.getCorrectiveAction());
          value.setFilterable(action.isFilterable());
          value.setLatchable(action.isLatchable());

          Long onDelaySeconds = null;
          if (action.getOnDelaySeconds() != null) {
            onDelaySeconds = action.getOnDelaySeconds().longValue();
          }
          value.setOndelayseconds(onDelaySeconds);

          Long offDelaySeconds = null;
          if (action.getOffDelaySeconds() != null) {
            offDelaySeconds = action.getOffDelaySeconds().longValue();
          }
          value.setOffdelayseconds(offDelaySeconds);

          producer.send(key, value);
        }
      }
    }
  }

  @RolesAllowed("jaws-admin")
  public void kafkaUnset(List<String> list) {
    if (list != null) {
      try (ActionProducer producer =
          new ActionProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (String name : list) {
          producer.send(name, null);
        }
      }
    }
  }
}
