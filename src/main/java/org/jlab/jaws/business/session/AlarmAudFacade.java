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
import org.jlab.jaws.persistence.entity.aud.AlarmAud;

/**
 * @author ryans
 */
@Stateless
public class AlarmAudFacade extends AbstractFacade<AlarmAud> {
  @EJB ApplicationRevisionInfoFacade revisionFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public AlarmAudFacade() {
    super(AlarmAud.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public List<AlarmAud> filterList(BigInteger alarmId, BigInteger revisionId, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AlarmAud> cq = cb.createQuery(AlarmAud.class);
    Root<AlarmAud> root = cq.from(AlarmAud.class);
    cq.select(root);

    List<Predicate> filters = new ArrayList<>();

    if (alarmId != null) {
      filters.add(cb.equal(root.get("alarmAudPK").get("alarmId"), alarmId));
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

    List<AlarmAud> entityList =
        getEntityManager()
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(max)
            .getResultList();

    if (entityList != null) {
      for (AlarmAud entity : entityList) {
        entity.getRevision().getId(); // Tickle to load
      }
    }

    return entityList;
  }

  @PermitAll
  public Long countFilterList(BigInteger entityId, BigInteger revisionId) {
    String selectFrom = "select count(*) from ALARM_AUD e ";

    List<String> whereList = new ArrayList<>();

    String w;

    if (entityId != null) {
      w = "e.alarm_id = " + entityId;
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
  public void loadStaff(List<AlarmAud> entityList) {
    if (entityList != null) {
      for (AlarmAud entity : entityList) {
        revisionFacade.loadUsers(entity.getRevision());
      }
    }
  }
}
