package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.business.util.OracleUtil;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.kafka.eventsource.EventSourceRecord;

/**
 * @author ryans
 */
@Stateless
public class NotificationFacade extends AbstractFacade<Notification> {
  private static final Logger logger = Logger.getLogger(NotificationFacade.class.getName());

  @EJB LocationFacade locationFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public NotificationFacade() {
    super(Notification.class);
  }

  @RolesAllowed("jaws-admin")
  public void clearCache() {
    em.createQuery("delete from Notification").executeUpdate();
  }

  // Note: Can't restrict to jaws-admin because caller in NotificationFacade RunAs doesn't work
  @PermitAll
  public void oracleSet(String name, EffectiveNotification effectiveNotification, Date since) {
    em.createQuery("delete from Notification n where n.name = :a")
        .setParameter("a", name)
        .executeUpdate();

    Notification notification = new Notification();

    notification.setName(name);

    BinaryState state = BinaryState.fromAlarmState(effectiveNotification.getState());

    notification.setState(state);
    notification.setSince(since);

    OverriddenAlarmType override = overrideFromAlarmState(effectiveNotification.getState());

    notification.setActiveOverride(override);

    AlarmActivationUnion union = effectiveNotification.getActivation();

    String activationType = "NotActive";

    if (union != null) {
      if (union.getUnion() instanceof EPICSActivation) {
        activationType = "EPICS";
        EPICSActivation epics = (EPICSActivation) union.getUnion();
        notification.setActivationSevr(epics.getSevr().name());
        notification.setActivationStat(epics.getStat().name());
      } else if (union.getUnion() instanceof NoteActivation) {
        activationType = "Note";
        NoteActivation note = (NoteActivation) union.getUnion();
        notification.setActivationNote(note.getNote());
      } else if (union.getUnion() instanceof ChannelErrorActivation) {
        activationType = "ChannelError";
        ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
        notification.setActivationError(channel.getError());
      } else if (union.getUnion() instanceof Activation) {
        activationType = "Simple";
      }
    }

    notification.setActivationType(activationType);

    create(notification);
  }

  // Note: Can't restrict to jaws-admin because caller in NotificationFacade RunAs doesn't work
  @PermitAll
  public void oracleMerge(List<EventSourceRecord<String, EffectiveNotification>> records)
      throws SQLException {
    final String sql =
        "MERGE INTO JAWS_OWNER.NOTIFICATION existing "
            + "                USING "
            + "                    (SELECT ?  AS name, "
            + "                ? AS state, "
            + "                ? AS since, "
            + "                ? AS active_override, "
            + "                ? AS activation_type, "
            + "                ? AS activation_note, "
            + "                ? AS activation_sevr, "
            + "                ? AS activation_stat, "
            + "                ? AS activation_error "
            + "                    FROM DUAL) a "
            + "                 ON (a.name = existing.name) "
            + "                WHEN MATCHED THEN UPDATE "
            + "                SET "
            + "                existing.state = a.state, "
            + "                existing.since = a.since, "
            + "                    existing.active_override = a.active_override, "
            + "                    existing.activation_type = a.activation_type, "
            + "                    existing.activation_note = a.activation_note, "
            + "                    existing.activation_sevr = a.activation_sevr, "
            + "                    existing.activation_stat = a.activation_stat, "
            + "                    existing.activation_error = a.activation_error "
            + "                WHEN NOT MATCHED THEN INSERT "
            + "                    (existing.name, existing.state, existing.since, existing.active_override, "
            + "                        existing.activation_type, existing.activation_note, existing.activation_sevr, "
            + "                        existing.activation_stat, existing.activation_error) "
            + "                    VALUES (a.name, a.state, a.since, a.active_override, a.activation_type, "
            + "                        a.activation_note, a.activation_sevr, a.activation_stat, a.activation_error) ";

    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = OracleUtil.getConnection();
      stmt = con.prepareStatement(sql);

      for (EventSourceRecord<String, EffectiveNotification> record : records) {

        BinaryState state = BinaryState.fromAlarmState(record.getValue().getState());
        OverriddenAlarmType override = overrideFromAlarmState(record.getValue().getState());
        AlarmActivationUnion union = record.getValue().getActivation();

        String activationType = "NotActive";
        String note = null;
        String sevr = null;
        String stat = null;
        String error = null;

        if (union != null) {
          if (union.getUnion() instanceof EPICSActivation) {
            activationType = "EPICS";
            EPICSActivation epics = (EPICSActivation) union.getUnion();
            sevr = epics.getSevr().name();
            stat = epics.getStat().name();
          } else if (union.getUnion() instanceof NoteActivation) {
            activationType = "Note";
            NoteActivation noteObj = (NoteActivation) union.getUnion();
            note = noteObj.getNote();
          } else if (union.getUnion() instanceof ChannelErrorActivation) {
            activationType = "ChannelError";
            ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
            error = channel.getError();
          } else if (union.getUnion() instanceof Activation) {
            activationType = "Simple";
          }
        }

        stmt.setString(1, record.getKey());
        stmt.setString(2, state.name());
        stmt.setDate(3, new java.sql.Date(record.getTimestamp()));
        if (override == null) {
          stmt.setNull(4, Types.VARCHAR);
        } else {
          stmt.setString(4, override.name());
        }
        stmt.setString(5, activationType);
        if (note == null) {
          stmt.setNull(6, Types.VARCHAR);
        } else {
          stmt.setString(6, note);
        }
        if (sevr == null) {
          stmt.setNull(7, Types.VARCHAR);
        } else {
          stmt.setString(7, sevr);
        }
        if (stat == null) {
          stmt.setNull(8, Types.VARCHAR);
        } else {
          stmt.setString(8, stat);
        }
        if (error == null) {
          stmt.setNull(9, Types.VARCHAR);
        } else {
          stmt.setString(9, error);
        }

        stmt.addBatch();
      }

      stmt.executeBatch();
    } finally {
      OracleUtil.close(stmt, con);
    }
  }

  public static OverriddenAlarmType overrideFromAlarmState(AlarmState state) {
    OverriddenAlarmType override = null;

    switch (state) {
      case NormalDisabled:
        override = OverriddenAlarmType.Disabled;
        break;
      case NormalFiltered:
        override = OverriddenAlarmType.Filtered;
        break;
      case NormalMasked:
        override = OverriddenAlarmType.Masked;
        break;
      case NormalOnDelayed:
        override = OverriddenAlarmType.OnDelayed;
        break;
      case ActiveOffDelayed:
        override = OverriddenAlarmType.OffDelayed;
        break;
      case NormalContinuousShelved:
      case NormalOneShotShelved:
        override = OverriddenAlarmType.Shelved;
        break;
      case ActiveLatched:
        override = OverriddenAlarmType.Latched;
        break;
    }

    return override;
  }

  static class FilterPredicates {
    public FilterPredicates(List<Predicate> andFilters, List<Predicate> orFilters) {
      this.andFilters = andFilters;
      this.orFilters = orFilters;
    }

    public List<Predicate> andFilters;
    public List<Predicate> orFilters;

    public Predicate getWhere(CriteriaBuilder cb) {
      Predicate where = null;

      if (!andFilters.isEmpty()) {

        Predicate and = cb.and(andFilters.toArray(new Predicate[] {}));
        Predicate or = null;

        if (!orFilters.isEmpty()) {
          or = cb.or(orFilters.toArray(new Predicate[] {}));
          where = cb.or(or, and);
        } else {
          where = and;
        }
      }

      return where;
    }
  }

  private FilterPredicates getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<Notification> root,
      BinaryState state,
      Boolean overridden,
      List<OverriddenAlarmType> overrideTypeList,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      Boolean filterable,
      String alarmName,
      String actionName,
      String componentName,
      boolean alwaysIncludeUnregistered,
      boolean alwaysIncludeUnfilterable,
      Map<String, Join> joins) {
    List<Predicate> filters = new ArrayList<>();
    List<Predicate> orFilters = new ArrayList<>();

    Join<Notification, Alarm> alarmJoin = root.join("alarm", JoinType.LEFT);
    Join<Alarm, Action> actionJoin = alarmJoin.join("action", JoinType.LEFT);
    Join<Action, SystemEntity> systemJoin = actionJoin.join("system", JoinType.LEFT);

    joins.put("alarm", alarmJoin);
    joins.put("action", actionJoin);
    joins.put("component", systemJoin);

    if (state != null) {
      filters.add(cb.equal(root.get("state"), state));

      if ("Active".equals(state.name())) {
        Predicate isActive = cb.equal(root.get("state"), BinaryState.Active);

        if (alwaysIncludeUnregistered) {
          orFilters.add(cb.and(isActive, cb.isNull(actionJoin.get("priority"))));
        }

        if (alwaysIncludeUnfilterable) {
          orFilters.add(cb.and(isActive, cb.equal(actionJoin.get("filterable"), false)));
        }
      }
    }

    if (overridden != null) {
      if (overridden) {
        filters.add(cb.isNotNull(root.get("override")));
      } else {
        filters.add(cb.isNull(root.get("override")));
      }
    }

    if (overrideTypeList != null && !overrideTypeList.isEmpty()) {
      filters.add(root.get("override").in(overrideTypeList));
    }

    if (activationType != null && !activationType.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("activationType")), activationType.toLowerCase()));
    }

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
        Join<Location, Alarm> alarmLocationJoin = subqueryRoot.join("alarmList");
        subquery.select(alarmLocationJoin.get("alarmId"));
        subquery.where(subqueryRoot.get("locationId").in(locationIdList));
        filters.add(cb.in(alarmJoin.get("alarmId")).value(subquery));
      }
    }

    if (alarmName != null && !alarmName.isEmpty()) {
      filters.add(cb.like(cb.lower(alarmJoin.get("name")), alarmName.toLowerCase()));
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

    if (registered != null) {
      if (registered) {
        filters.add(cb.isNotNull(actionJoin.get("priority")));
      } else {
        filters.add(cb.isNull(actionJoin.get("priority")));
      }
    }

    if (filterable != null) {
      filters.add(cb.equal(actionJoin.get("filterable"), filterable));
    }

    return new FilterPredicates(filters, orFilters);
  }

  @PermitAll
  public List<Notification> filterList(
      BinaryState state,
      Boolean overridden,
      List<OverriddenAlarmType> overrideTypeList,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      Boolean filterable,
      String alarmName,
      String actionName,
      String componentName,
      boolean alwaysIncludeUnregistered,
      boolean alwaysIncludeUnfilterable,
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
    Root<Notification> root = cq.from(Notification.class);
    cq.select(root);

    Map<String, Join> joins = new HashMap<>();

    FilterPredicates filters =
        getFilters(
            cb,
            cq,
            root,
            state,
            overridden,
            overrideTypeList,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            filterable,
            alarmName,
            actionName,
            componentName,
            alwaysIncludeUnregistered,
            alwaysIncludeUnfilterable,
            joins);

    Predicate where = filters.getWhere(cb);

    if (where != null) {
      cq.where(where);
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("state");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    Path p1 = joins.get("action").get("priority");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    Path p2 = root.get("name");
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
  public long countList(
      BinaryState state,
      Boolean overridden,
      List<OverriddenAlarmType> overrideTypeList,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      Boolean filterable,
      String alarmName,
      String actionName,
      String componentName,
      boolean alwaysIncludeUnregistered,
      boolean alwaysIncludeUnfilterable) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Notification> root = cq.from(Notification.class);

    Map<String, Join> joins = new HashMap<>();

    FilterPredicates filters =
        getFilters(
            cb,
            cq,
            root,
            state,
            overridden,
            overrideTypeList,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            filterable,
            alarmName,
            actionName,
            componentName,
            alwaysIncludeUnregistered,
            alwaysIncludeUnfilterable,
            joins);

    Predicate where = filters.getWhere(cb);

    if (where != null) {
      cq.where(where);
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
