package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.CEDSyncRule;

/**
 * @author ryans
 */
@Stateless
public class CEDSyncRuleFacade extends AbstractFacade<CEDSyncRule> {
  private static final Logger logger = Logger.getLogger(CEDSyncRuleFacade.class.getName());

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public CEDSyncRuleFacade() {
    super(CEDSyncRule.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb, Root<CEDSyncRule> root, BigInteger syncId, String actionName) {
    List<Predicate> filters = new ArrayList<>();

    if (syncId != null) {
      filters.add(cb.equal(root.get("cedSyncRuleId"), syncId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), actionName.toLowerCase()));
    }

    return filters;
  }

  @PermitAll
  public List<CEDSyncRule> filterList(BigInteger syncId, String actionName, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<CEDSyncRule> cq = cb.createQuery(CEDSyncRule.class);
    Root<CEDSyncRule> root = cq.from(CEDSyncRule.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("cedSyncRuleId");
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
    Root<CEDSyncRule> root = cq.from(CEDSyncRule.class);

    List<Predicate> filters = getFilters(cb, root, syncId, actionName);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
