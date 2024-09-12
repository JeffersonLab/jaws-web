package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.Action;
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

    if (syncId != null) {
      filters.add(cb.equal(root.get("syncRuleId"), syncId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), actionName.toLowerCase()));
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
}
