package org.jlab.jaws.business.session;

import org.checkerframework.checker.units.qual.A;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Location;
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
public class AlarmFacade extends AbstractFacade<Alarm> {
    private static final Logger logger = Logger.getLogger(AlarmFacade.class.getName());

    @EJB
    LocationFacade locationFacade;

    @EJB
    ActionFacade actionFacade;

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlarmFacade() {
        super(Alarm.class);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, CriteriaQuery<? extends Object> cq, Root<Alarm> root, BigInteger[] locationIdArray,
                                       BigInteger priorityId, BigInteger teamId, String alarmName, String actionName,
                                       String componentName) {
        List<Predicate> filters = new ArrayList<>();

        Join<Alarm, Action> actionJoin = root.join("action");
        Join<Action, Component> componentJoin = actionJoin.join("component");

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
                Join<Location, Alarm> alarmJoin = subqueryRoot.join("alarmList");
                subquery.select(alarmJoin.get("alarmId"));
                subquery.where(subqueryRoot.get("locationId").in(locationIdList));
                filters.add(cb.in(root.get("alarmId")).value(subquery));
            }
        }

        if (alarmName != null && !alarmName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), alarmName.toLowerCase()));
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
    public List<Alarm> filterList(BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId,
                                  String alarmName, String actionName, String componentName, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Alarm> cq = cb.createQuery(Alarm.class);
        Root<Alarm> root = cq.from(Alarm.class);
        cq.select(root);

        List<Predicate> filters = getFilters(cb, cq, root, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("name");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public long countList(BigInteger[] locationIdArray, BigInteger priorityId, BigInteger teamId, String alarmName,
                          String actionName, String componentName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Alarm> root = cq.from(Alarm.class);

        List<Predicate> filters = getFilters(cb, cq, root, locationIdArray, priorityId, teamId, alarmName, actionName,
                componentName);
        
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    public Alarm findByName(String name) {
        return null;
    }

    @RolesAllowed("jaws-admin")
    public void addAlarm(String name, BigInteger actionId, BigInteger[] locationIdArray, String device, String screenCommand, String maskedBy, String pv) throws UserFriendlyException {
        if(name == null || name.isBlank()) {
            throw new UserFriendlyException("Name is required");
        }

        if(actionId == null) {
            throw new UserFriendlyException("Action is required");
        }

        Action action = actionFacade.find(actionId);

        if(action == null) {
            throw new UserFriendlyException("Action not found with ID: " + actionId);
        }

        List<Location> locationList = new ArrayList<>();

        if(locationIdArray != null && locationIdArray.length > 0) {
            for(BigInteger id: locationIdArray) {
                if(id == null) {  // TODO: the convertBigIntegerArray method should be excluding empty/null
                    continue;
                }

                Location l = locationFacade.find(id);

                if(l != null) {
                    locationList.add(l);
                }
            }
        }

        Alarm alarm = new Alarm();

        alarm.setName(name);
        alarm.setAction(action);
        alarm.setLocationList(locationList);
        alarm.setDevice(device);
        alarm.setScreenCommand(screenCommand);
        alarm.setMaskedBy(maskedBy);
        alarm.setPv(pv);

        create(alarm);
    }

    @RolesAllowed("jaws-admin")
    public void removeAlarm(BigInteger alarmId) throws UserFriendlyException {
        if(alarmId == null) {
            throw new UserFriendlyException("Alarm ID is required");
        }

        Alarm alarm = find(alarmId);

        if(alarm == null) {
            throw new UserFriendlyException("Alarm not found with ID: " + alarmId);
        }

        remove(alarm);
    }
}
