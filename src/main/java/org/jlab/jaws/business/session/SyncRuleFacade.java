package org.jlab.jaws.business.session;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
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
import org.jlab.jaws.entity.Alarm;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.AlarmEntity;
import org.jlab.jaws.persistence.entity.SyncRule;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class SyncRuleFacade extends AbstractFacade<SyncRule> {
  private static final Logger logger = Logger.getLogger(SyncRuleFacade.class.getName());

  @EJB ActionFacade actionFacade;

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
      CriteriaBuilder cb, Root<SyncRule> root, BigInteger syncId, String actionName) {
    List<Predicate> filters = new ArrayList<>();

    Join<Alarm, Action> actionJoin = root.join("action");

    if (syncId != null) {
      filters.add(cb.equal(root.get("syncRuleId"), syncId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(actionJoin.get("name")), actionName.toLowerCase()));
    }

    return filters;
  }

  @PermitAll
  public List<SyncRule> filterList(BigInteger syncId, String actionName, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SyncRule> cq = cb.createQuery(SyncRule.class);
    Root<SyncRule> root = cq.from(SyncRule.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("syncRuleId");
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
  public long countList(BigInteger syncId, String actionName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SyncRule> root = cq.from(SyncRule.class);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @RolesAllowed("jaws-admin")
  public void addSync(
      BigInteger actionId, String deployment, String query, String screencommand, String pv)
      throws UserFriendlyException {
    if (actionId == null) {
      throw new UserFriendlyException("Action is required");
    }

    Action action = actionFacade.find(actionId);

    if (action == null) {
      throw new UserFriendlyException("Action not found with ID: " + actionId);
    }

    SyncRule rule = new SyncRule();
    rule.setAction(action);

    rule.setDeployment(deployment);
    rule.setQuery(query);
    rule.setScreenCommand(screencommand);
    rule.setPv(pv);

    create(rule);
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
      String deployment,
      String query,
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

    rule.setAction(action);
    rule.setDeployment(deployment);
    rule.setQuery(query);
    rule.setScreenCommand(screencommand);
    rule.setPv(pv);

    edit(rule);
  }

  @RolesAllowed("jaws-admin")
  public List<AlarmEntity> executeRule(SyncRule rule) throws UserFriendlyException {
    List<AlarmEntity> alarmList = null;

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(rule.getQuery())).build();

    HttpResponse<String> response = null;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new UserFriendlyException("Unable to execute query", e);
    }

    if (200 == response.statusCode()) {
      String body = response.body();

      System.out.println(body);

      alarmList = convertResponse(body, rule);
    } else {
      throw new UserFriendlyException("Response code " + response.statusCode());
    }

    return alarmList;
  }

  private List<AlarmEntity> convertResponse(String body, SyncRule rule) {
    List<AlarmEntity> alarmList = new ArrayList<>();

    JsonObject object = Json.createReader(new StringReader(body)).readObject();

    JsonObject inventory = object.getJsonObject("inventory");
    JsonArray elements = inventory.getJsonArray("elements");

    for (JsonValue v : elements) {
      JsonObject o = v.asJsonObject();
      AlarmEntity alarm = new AlarmEntity();
      alarm.setName(o.getString("name") + " " + rule.getAction().getName());
      alarm.setAction(rule.getAction());
      alarm.setScreenCommand(rule.getScreenCommand());
      alarm.setPv(rule.getPv());
      alarmList.add(alarm);
    }

    return alarmList;
  }
}
