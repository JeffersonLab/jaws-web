package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.hibernate.envers.RevisionType;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.AlarmEntity;
import org.jlab.jaws.persistence.entity.ApplicationRevisionInfo;
import org.jlab.jaws.persistence.entity.SyncRule;
import org.jlab.jaws.persistence.model.AuditedEntityChange;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;

/**
 * @author ryans
 */
@Stateless
public class ApplicationRevisionInfoFacade extends AbstractFacade<ApplicationRevisionInfo> {

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public ApplicationRevisionInfoFacade() {
    super(ApplicationRevisionInfo.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  private UserAuthorizationService userService = UserAuthorizationService.getInstance();

  @PermitAll
  public List<ApplicationRevisionInfo> filterList(
      Date modifiedStart, Date modifiedEnd, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<ApplicationRevisionInfo> cq = cb.createQuery(ApplicationRevisionInfo.class);
    Root<ApplicationRevisionInfo> root = cq.from(ApplicationRevisionInfo.class);
    cq.select(root);

    List<Predicate> filters = new ArrayList<>();

    if (modifiedStart != null) {
      filters.add(cb.greaterThanOrEqualTo(root.get("ts"), modifiedStart.getTime()));
    }

    if (modifiedEnd != null) {
      filters.add(cb.lessThan(root.get("ts"), modifiedEnd.getTime()));
    }

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("id");
    Order o0 = cb.desc(p0);
    orders.add(o0);
    cq.orderBy(orders);

    List<ApplicationRevisionInfo> revisionList =
        getEntityManager()
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(max)
            .getResultList();

    if (revisionList != null) {
      for (ApplicationRevisionInfo revision : revisionList) {
        revision.setChangeList(findEntityChangeList(revision.getId()));
      }
    }

    return revisionList;
  }

  @PermitAll
  public Long countFilterList(Date modifiedStart, Date modifiedEnd) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<ApplicationRevisionInfo> root = cq.from(ApplicationRevisionInfo.class);

    List<Predicate> filters = new ArrayList<>();

    if (modifiedStart != null) {
      filters.add(cb.greaterThanOrEqualTo(root.get("ts"), modifiedStart.getTime()));
    }

    if (modifiedEnd != null) {
      filters.add(cb.lessThan(root.get("ts"), modifiedEnd.getTime()));
    }

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @SuppressWarnings("unchecked")
  @PermitAll
  public List<AuditedEntityChange> findEntityChangeList(long revision) {
    Query q =
        em.createNativeQuery(
            "select 'A', alarm_id, name, revtype from jaws_owner.alarm_aud where rev = ?1 "
                + "union select 'B', action_id, name, revtype from jaws_owner.action_aud where rev = ?2 "
                + "union select 'C', sync_rule_id, description, revtype from jaws_owner.sync_rule_aud where rev = ?3");

    q.setParameter(1, revision);
    q.setParameter(2, revision);
    q.setParameter(3, revision);

    List<Object[]> resultList = q.getResultList();

    List<AuditedEntityChange> changeList = new ArrayList<>();

    if (resultList != null) {
      for (Object[] row : resultList) {
        Class entityClass = classFromChar(((Character) row[0]));
        String classLabel = classLabelFromChar(((Character) row[0]));
        BigInteger entityId = BigInteger.valueOf(((Number) row[1]).longValue());
        String entityName = (String) row[2];
        RevisionType type = fromNumber((Number) row[3]);
        changeList.add(
            new AuditedEntityChange(revision, type, entityId, entityName, entityClass, classLabel));
      }
    }

    return changeList;
  }

  @PermitAll
  public String classLabelFromChar(Character c) {

    // We don't want to see Class.simpleName "AlarmEntity", we want to see "Alarm"

    String label = null;

    if (c != null) {
      switch (c) {
        case 'A':
          label = "Alarm";
          break;
        case 'B':
          label = "Action";
          break;
        case 'C':
          label = "Sync Rule";
          break;
        default:
          break;
      }
    }

    return label;
  }

  @PermitAll
  public Class classFromChar(Character c) {
    Class entityClass = null;

    if (c != null) {
      switch (c) {
        case 'A':
          entityClass = AlarmEntity.class;
          break;
        case 'B':
          entityClass = Action.class;
          break;
        case 'C':
          entityClass = SyncRule.class;
          break;
        default:
          break;
      }
    }

    return entityClass;
  }

  @PermitAll
  public RevisionType fromNumber(Number n) {
    RevisionType type = null;

    if (n != null) {
      int intValue = (int) n.longValue();

      switch (intValue) {
        case 0:
          type = RevisionType.ADD;
          break;
        case 1:
          type = RevisionType.MOD;
          break;
        case 2:
          type = RevisionType.DEL;
          break;
      }
    }

    return type;
  }

  @PermitAll
  public void loadUsers(List<ApplicationRevisionInfo> revisionList) {
    if (revisionList != null) {
      for (ApplicationRevisionInfo revision : revisionList) {
        loadUsers(revision);
      }
    }
  }

  @PermitAll
  public void loadUsers(ApplicationRevisionInfo revision) {
    if (revision != null) {
      String username = revision.getUsername();

      if (username != null) {
        User user = userService.getUserFromUsername(username);

        revision.setUser(user);
      }
    }
  }
}
