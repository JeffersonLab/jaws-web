package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.aud.SyncRuleAud;

/**
 * @author ryans
 */
@Stateless
public class SyncRuleAudFacade extends AbstractFacade<SyncRuleAud> {
  @EJB ApplicationRevisionInfoFacade revisionFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public SyncRuleAudFacade() {
    super(SyncRuleAud.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public List<SyncRuleAud> filterList(
      BigInteger syncRuleId, BigInteger revisionId, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SyncRuleAud> cq = cb.createQuery(SyncRuleAud.class);
    Root<SyncRuleAud> root = cq.from(SyncRuleAud.class);
    cq.select(root);

    List<Predicate> filters = new ArrayList<>();

    if (syncRuleId != null) {
      filters.add(cb.equal(root.get("syncRuleAudPK").get("syncRuleId"), syncRuleId));
    }

    if (revisionId != null) {
      filters.add(cb.equal(root.get("revision").get("id"), revisionId));
    }

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("revision").get("id");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);

    List<SyncRuleAud> entityList =
        getEntityManager()
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(max)
            .getResultList();

    if (entityList != null) {
      for (SyncRuleAud entity : entityList) {
        entity.getRevision().getId(); // Tickle to load
      }
    }

    return entityList;
  }

  @PermitAll
  public Long countFilterList(BigInteger entityId, BigInteger revisionId) {
    String selectFrom = "select count(*) from SYNC_RULE_AUD e ";

    List<String> whereList = new ArrayList<>();

    String w;

    if (entityId != null) {
      w = "e.sync_rule_id = " + entityId;
      whereList.add(w);
    }

    if (revisionId != null) {
      w = "e.rev = " + revisionId;
      whereList.add(w);
    }

    String where = "";

    if (!whereList.isEmpty()) {
      where = "where ";
      for (String wh : whereList) {
        where = where + wh + " and ";
      }

      where = where.substring(0, where.length() - 5);
    }

    String sql = selectFrom + " " + where;
    Query q = em.createNativeQuery(sql);

    return ((Number) q.getSingleResult()).longValue();
  }

  @PermitAll
  public void loadStaff(List<SyncRuleAud> entityList) {
    if (entityList != null) {
      for (SyncRuleAud entity : entityList) {
        revisionFacade.loadUsers(entity.getRevision());
      }
    }
  }
}
