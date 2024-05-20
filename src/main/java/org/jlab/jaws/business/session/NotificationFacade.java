package org.jlab.jaws.business.session;

import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class NotificationFacade extends AbstractFacade<Notification> {
    private static final Logger logger = Logger.getLogger(NotificationFacade.class.getName());

    @EJB
    LocationFacade locationFacade;

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
    public void set(Alarm alarm, EffectiveNotification effectiveNotification) {
        em.createQuery("delete from Notification n where n.alarm = :a").setParameter("a", alarm).executeUpdate();

        Notification notification = new Notification();

        notification.setAlarm(alarm);
        notification.setState(effectiveNotification.getState());
        AlarmActivationUnion union = effectiveNotification.getActivation();

        String activationType = "NONE";

        if(union != null) {
            if(union.getUnion() instanceof EPICSActivation) {
                activationType = "EPICS";
                EPICSActivation epics = (EPICSActivation) union.getUnion();
                notification.setActivationSevr(epics.getSevr().name());
                notification.setActivationStat(epics.getStat().name());
            } else if (union.getUnion() instanceof NoteActivation) {
                activationType = "Note";
                NoteActivation note = (NoteActivation) union.getUnion();
                notification.setActivationNote(note.getNote());
            } else if(union.getUnion() instanceof ChannelErrorActivation) {
                activationType = "ChannelError";
                ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
                notification.setActivationError(channel.getError());
            }
        }

        notification.setActivationType(activationType);

        AlarmOverrideSet overrides = effectiveNotification.getOverrides();

        DisabledOverride disabled = overrides.getDisabled();
        if(disabled != null) {

        }

        FilteredOverride filtered = overrides.getFiltered();
        if(filtered != null) {

        }

        create(notification);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, CriteriaQuery<? extends Object> cq, Root<Notification> root,
                                       AlarmState state, OverriddenAlarmType override, String activationType,
                                       BigInteger[] locationIdArray,
                                       BigInteger priorityId, BigInteger teamId, String alarmName, String actionName,
                                       String componentName, Map<String, Join> joins) {
        List<Predicate> filters = new ArrayList<>();

        Join<Notification, Alarm> alarmJoin = root.join("alarm");
        Join<Alarm, Action> actionJoin = alarmJoin.join("action");
        Join<Action, Component> componentJoin = actionJoin.join("component");

        joins.put("alarm", alarmJoin);
        joins.put("action", actionJoin);
        joins.put("component", componentJoin);

        if (state != null) {
            filters.add(cb.equal(root.get("state"), state));
        }

        if (override != null) {
            // TOODO: in override table query
        }

        if (activationType != null && !activationType.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("activationType")), activationType.toLowerCase()));
        }

        if(locationIdArray != null && locationIdArray.length > 0) {
            // Parent locations imply children locations
            Set<Location> materializedLocations = new HashSet<>();
            for(BigInteger locationId: locationIdArray) {
                if(locationId != null) {
                    Set<Location> subset = locationFacade.findBranchAsSet(locationId);
                    materializedLocations.addAll(subset);
                }
            }

            List<BigInteger> locationIdList = new ArrayList<>();

            for(Location l: materializedLocations) {
                locationIdList.add(l.getLocationId());
            }

            if(!locationIdList.isEmpty()) {
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
            filters.add(cb.like(cb.lower(componentJoin.get("name")), componentName.toLowerCase()));
        }

        if (teamId != null) {
            filters.add(cb.equal(componentJoin.get("team"), teamId));
        }

        return filters;
    }

    @PermitAll
    public List<Notification> filterList(AlarmState state, OverriddenAlarmType override, String activationType, BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId, String alarmName, String actionName, String componentName, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);
        cq.select(root);

        Map<String, Join> joins = new HashMap<>();

        List<Predicate> filters = getFilters(cb, cq, root, state, override, activationType, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName, joins);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        List<Order> orders = new ArrayList<>();
        Path p0 = joins.get("alarm").get("name");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public long countList(AlarmState state, OverriddenAlarmType override, String activationType, BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId, String alarmName, String actionName, String componentName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Notification> root = cq.from(Notification.class);

        Map<String, Join> joins = new HashMap<>();

        List<Predicate> filters = getFilters(cb, cq, root, state, override, activationType, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName, joins);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }
}
