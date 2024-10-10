package org.jlab.jaws.business.session;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class SyncRuleFacade extends AbstractFacade<SyncRule> {
  private static final Logger logger = Logger.getLogger(SyncRuleFacade.class.getName());

  @EJB ActionFacade actionFacade;
  @EJB LocationFacade locationFacade;
  @EJB SyncServerFacade serverFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public SyncRuleFacade() {
    super(SyncRule.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      Root<SyncRule> root,
      BigInteger syncId,
      String actionName,
      String systemName) {
    List<Predicate> filters = new ArrayList<>();

    Join<AlarmEntity, Action> actionJoin = root.join("action");
    Join<Action, SystemEntity> systemJoin = actionJoin.join("system");

    if (syncId != null) {
      filters.add(cb.equal(root.get("syncRuleId"), syncId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(actionJoin.get("name")), actionName.toLowerCase()));
    }

    if (systemName != null && !systemName.isEmpty()) {
      filters.add(cb.like(cb.lower(systemJoin.get("name")), systemName.toLowerCase()));
    }

    return filters;
  }

  @PermitAll
  public List<SyncRule> filterList(
      BigInteger syncId, String actionName, String systemName, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SyncRule> cq = cb.createQuery(SyncRule.class);
    Root<SyncRule> root = cq.from(SyncRule.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName, systemName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("action").get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    Path p1 = root.get("server").get("syncServerId");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    Path p2 = root.get("syncRuleId");
    Order o2 = cb.asc(p2);
    orders.add(o2);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public long countList(BigInteger syncId, String actionName, String systemName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SyncRule> root = cq.from(SyncRule.class);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName, systemName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @RolesAllowed("jaws-admin")
  public BigInteger addSync(
      BigInteger actionId,
      String syncServerName,
      String description,
      String query,
      String expression,
      String screencommand,
      String pv)
      throws UserFriendlyException {
    if (actionId == null) {
      throw new UserFriendlyException("Action is required");
    }

    Action action = actionFacade.find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    if (syncServerName == null || syncServerName.isEmpty()) {
      throw new UserFriendlyException("Sync server name is required");
    }

    SyncServer server = serverFacade.findByName(syncServerName);

    if (server == null) {
      throw new UserFriendlyException("Sync server not found with name: " + syncServerName);
    }

    if (query == null || query.isBlank()) {
      throw new UserFriendlyException("Query is required");
    }

    SyncRule rule = new SyncRule();
    rule.setAction(action);

    rule.setSyncServer(server);
    rule.setDescription(description);
    rule.setQuery(query);
    rule.setPropertyExpression(expression);
    rule.setScreenCommand(screencommand);
    rule.setPv(pv);

    create(rule);

    return rule.getSyncRuleId();
  }

  @RolesAllowed("jaws-admin")
  public void removeSync(BigInteger id) throws UserFriendlyException {
    if (id == null) {
      throw new UserFriendlyException("Sync Rule ID is required");
    }

    SyncRule rule = find(id);

    if (rule == null) {
      throw new UserFriendlyException("Sync Rule not found with ID: " + id);
    }

    remove(rule);
  }

  @RolesAllowed("jaws-admin")
  public void editSync(
      BigInteger id,
      BigInteger actionId,
      String syncServerName,
      String description,
      String query,
      String expression,
      String screencommand,
      String pv)
      throws UserFriendlyException {
    if (id == null) {
      throw new UserFriendlyException("Sync Rule ID is required");
    }

    SyncRule rule = find(id);

    if (rule == null) {
      throw new UserFriendlyException("Sync Rule not found with ID: " + id);
    }

    if (actionId == null) {
      throw new UserFriendlyException("Action is required");
    }

    Action action = actionFacade.find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    if (syncServerName == null || syncServerName.isEmpty()) {
      throw new UserFriendlyException("Sync server name is required");
    }

    SyncServer server = serverFacade.findByName(syncServerName);

    if (server == null) {
      throw new UserFriendlyException("Sync server not found with name: " + syncServerName);
    }

    rule.setAction(action);
    rule.setSyncServer(server);
    rule.setDescription(description);
    rule.setQuery(query);
    rule.setPropertyExpression(expression);
    rule.setScreenCommand(screencommand);
    rule.setPv(pv);

    edit(rule);
  }

  @RolesAllowed("jaws-admin")
  public LinkedHashMap<BigInteger, AlarmEntity> executeRule(SyncRule rule)
      throws UserFriendlyException {
    LinkedHashMap<BigInteger, AlarmEntity> alarmList = null;

    HttpClient client = HttpClient.newHttpClient();

    String url = rule.getSearchURL();

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    HttpResponse<String> response = null;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new UserFriendlyException("Unable to execute request for url " + url, e);
    }

    if (200 == response.statusCode()) {
      String body = response.body();

      // System.out.println(body);

      alarmList = convertResponse(body, rule);
    } else {
      throw new UserFriendlyException("Response code " + response.statusCode());
    }

    return alarmList;
  }

  private LinkedHashMap<BigInteger, AlarmEntity> convertResponse(String body, SyncRule rule) {
    LinkedHashMap<BigInteger, AlarmEntity> alarmList = new LinkedHashMap<>();

    JsonObject object = Json.createReader(new StringReader(body)).readObject();

    JsonObject inventory = object.getJsonObject("Inventory");
    JsonArray elements = inventory.getJsonArray("elements");

    Map<String, Location> locationMap = loadSegmaskToLocationMap();

    for (JsonValue v : elements) {
      JsonObject o = v.asJsonObject();

      BigInteger elementId = o.getJsonNumber("id").bigIntegerValue();
      String elementName = o.getString("name");

      List<Location> locationList = null;
      String epicsName = "";

      if (o.containsKey("properties")) {
        JsonObject properties = o.getJsonObject("properties");

        if (properties.containsKey("EPICSName") && !properties.isNull("EPICSName")) {
          epicsName = properties.getString("EPICSName");
        }

        if (properties.containsKey("SegMask") && !properties.isNull("SegMask")) {
          String segMask = properties.getString("SegMask");

          locationList = locationsFromSegMask(locationMap, segMask);
        }
      }

      String screenCommand =
          applyExpressionVars(
              rule.getScreenCommand(), elementName, epicsName, rule.getSyncServer().getName());
      String pv =
          applyExpressionVars(rule.getPv(), elementName, epicsName, rule.getSyncServer().getName());

      AlarmEntity alarm = new AlarmEntity();
      alarm.setSyncElementId(elementId);
      alarm.setSyncRule(rule);
      alarm.setName(elementName + " " + rule.getAction().getName());
      alarm.setAction(rule.getAction());
      alarm.setLocationList(locationList);
      alarm.setScreenCommand(screenCommand);
      alarm.setPv(pv);
      alarmList.put(elementId, alarm);
    }

    return alarmList;
  }

  private String applyExpressionVars(
      String input, String elementName, String epicsName, String deployment) {
    String result = "";

    if (input != null) {
      result = input.replaceAll("\\{ElementName}", elementName);

      result = result.replaceAll("\\{EPICSName}", epicsName);

      result = result.replaceAll("\\{Deployment}", deployment);
    }

    return result;
  }

  private Map<String, Location> loadSegmaskToLocationMap() {
    Map<String, Location> map = new HashMap<>();

    List<Location> locationList = locationFacade.findAll();

    for (Location location : locationList) {
      String segmaskCsv = location.getSegmask();

      if (segmaskCsv != null) {
        String[] masks = segmaskCsv.split(",");

        for (String mask : masks) {
          if (mask != null && !mask.isBlank()) {
            map.put(mask.trim(), location);
          }
        }
      }
    }

    return map;
  }

  private List<Location> locationsFromSegMask(Map<String, Location> locationMap, String segMask) {
    // Use Set because some SegMasks map to same Location so we want to avoid duplicates
    Set<Location> locationList = new HashSet<>();

    if (segMask != null && !segMask.isEmpty()) {
      String[] masks = segMask.split("\\+");

      for (String mask : masks) {
        if (mask != null && !mask.isBlank()) {
          Location loc = locationMap.get(mask.trim());

          if (loc != null) {
            locationList.add(loc);
          }
        }
      }
    }

    // Often SegMask includes implicit locations (parent locations in same branch)
    List<Location> explicitOnly = new ArrayList<>();

    Location rootLocation = getRoot(locationList);

    // If There is more than 1 location then root is implied
    if (locationList.size() > 1) {
      locationList.remove(rootLocation);
    }

    for (Location location : locationList) {
      if (isLowestInBranch(location, locationList)) {
        explicitOnly.add(location);
      }
    }

    return explicitOnly;
  }

  private boolean isLowestInBranch(Location location, Set<Location> locationList) {
    boolean lowest = true;

    Set<Location> descendents = getDescendents(location);

    for (Location loc : locationList) {
      if (descendents.contains(loc)) {
        lowest = false;
        break;
      }
    }

    return lowest;
  }

  private Set<Location> getDescendents(Location location) {
    Set<Location> descendents = new HashSet<>(location.getChildList());

    for (Location loc : location.getChildList()) {
      descendents.addAll(getDescendents(loc));
    }

    return descendents;
  }

  private Location getRoot(Set<Location> locationList) {
    Location root = null;
    for (Location location : locationList) {
      if (location.getLocationId().equals(Location.TREE_ROOT) && location.getParent() == null) {
        root = location;
        break;
      }
    }

    return root;
  }
}
