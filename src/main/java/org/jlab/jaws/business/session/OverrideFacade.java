package org.jlab.jaws.business.session;

import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.OverrideProducer;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.smoothness.business.exception.UserFriendlyException;

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
public class OverrideFacade extends AbstractFacade<AlarmOverride> {
    private static final Logger logger = Logger.getLogger(OverrideFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @EJB
    LocationFacade locationFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OverrideFacade() {
        super(AlarmOverride.class);
    }

    // Note: Can't restrict to jaws-admin because caller in KafkaOverrideFacade RunAs doesn't work
    @PermitAll
    public void oracleSet(String name, OverriddenAlarmType type, AlarmOverrideUnion value) {
        em.createQuery("delete from AlarmOverride o where o.overridePK = :a").setParameter("a", new OverridePK(name, type)).executeUpdate();

        AlarmOverride override = new AlarmOverride();

        OverridePK pk = new OverridePK();
        pk.setName(name);
        pk.setType(type);

        override.setOverridePK(pk);

        if(value != null) {
            if (value.getUnion() instanceof DisabledOverride) {
                DisabledOverride disabledOverride = (DisabledOverride) value.getUnion();
                override.setComments(disabledOverride.getComments());
            } else if(value.getUnion() instanceof FilteredOverride) {
                FilteredOverride filteredOverride = (FilteredOverride) value.getUnion();
                override.setComments(filteredOverride.getFiltername());
            } else if(value.getUnion() instanceof ShelvedOverride) {
                ShelvedOverride shelvedOverride = (ShelvedOverride) value.getUnion();
                override.setComments(shelvedOverride.getComments());
                override.setOneshot(shelvedOverride.getOneshot());
                override.setExpiration(new Date(shelvedOverride.getExpiration()));
                override.setShelvedReason(shelvedOverride.getReason().name());
            }
        }

        create(override);
    }

    @RolesAllowed("jaws-admin")
    public void kafkaSet(String[] nameArray, OverriddenAlarmType type, AlarmOverrideUnion value) throws UserFriendlyException {
        if(nameArray == null || nameArray.length == 0) {
            throw new UserFriendlyException("Names selection must not be empty");
        }

        if(type == null) {
            throw new UserFriendlyException("Type selection must not be null");
        }

        try(OverrideProducer producer = new OverrideProducer(KafkaConfig.getProducerPropsWithRegistry())) {
            for (String name : nameArray) {
                AlarmOverrideKey key = new AlarmOverrideKey(name, type);
                producer.send(key, value);
            }
        }
    }

    @RolesAllowed("jaws-admin")
    public void clearCache() {
        em.createQuery("delete from AlarmOverride").executeUpdate();
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, CriteriaQuery<? extends Object> cq, Root<AlarmOverride> root,
                                       List<OverriddenAlarmType> typeList,
                                       BigInteger[] locationIdArray,
                                       BigInteger priorityId, BigInteger teamId, String alarmName, String actionName,
                                       String componentName, Map<String, Join> joins) {
        List<Predicate> filters = new ArrayList<>();

        Join<AlarmOverride, Alarm> alarmJoin = root.join("alarm", JoinType.LEFT);
        Join<Alarm, Action> actionJoin = alarmJoin.join("action", JoinType.LEFT);
        Join<Action, Component> componentJoin = actionJoin.join("component", JoinType.LEFT);

        joins.put("alarm", alarmJoin);
        joins.put("action", actionJoin);
        joins.put("component", componentJoin);

        if(typeList != null && typeList.size() > 0) {
            filters.add(root.get("overridePK").get("type").in(typeList));
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
    public List<AlarmOverride> filterList(List<OverriddenAlarmType> typeList, BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId, String alarmName, String actionName, String componentName, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AlarmOverride> cq = cb.createQuery(AlarmOverride.class);
        Root<AlarmOverride> root = cq.from(AlarmOverride.class);
        cq.select(root);

        Map<String, Join> joins = new HashMap<>();

        List<Predicate> filters = getFilters(cb, cq, root, typeList, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName, joins);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        List<Order> orders = new ArrayList<>();
        Path p0 = joins.get("action").get("priority");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        Path p1 = root.get("overridePK").get("name");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public long countList(List<OverriddenAlarmType> typeList, BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId, String alarmName, String actionName, String componentName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AlarmOverride> root = cq.from(AlarmOverride.class);

        Map<String, Join> joins = new HashMap<>();

        List<Predicate> filters = getFilters(cb, cq, root, typeList, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName, joins);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public List<AlarmOverride> findByAlarmName(String name) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AlarmOverride> cq = cb.createQuery(AlarmOverride.class);
        Root<AlarmOverride> root = cq.from(AlarmOverride.class);
        cq.select(root);
        cq.where(cb.equal(root.get("overridePK").get("name"), name));
        TypedQuery<AlarmOverride> q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }
}
