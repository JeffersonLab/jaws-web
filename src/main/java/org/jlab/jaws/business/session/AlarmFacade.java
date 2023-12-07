package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.persistence.entity.Component;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class AlarmFacade extends AbstractFacade<Alarm> {
    private static final Logger logger = Logger.getLogger(AlarmFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlarmFacade() {
        super(Alarm.class);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, Root<Alarm> root, BigInteger[] locationIdArray,
                                       BigInteger priorityId, BigInteger teamId, String alarmName, String actionName,
                                       String componentName) {
        List<Predicate> filters = new ArrayList<>();

        Join<Alarm, Action> actionJoin = root.join("action");
        Join<Action, Component> componentJoin = actionJoin.join("component");

        if(locationIdArray != null && locationIdArray.length > 0) {

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

        List<Predicate> filters = getFilters(cb, root, locationIdArray, priorityId, teamId, alarmName, actionName,
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

        List<Predicate> filters = getFilters(cb, root, locationIdArray, priorityId, teamId, alarmName, actionName,
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
}
