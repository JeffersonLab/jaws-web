package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Priority;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class ActionFacade extends AbstractFacade<Action> {
    private static final Logger logger = Logger.getLogger(ActionFacade.class.getName());

    @EJB
    ComponentFacade componentFacade;

    @EJB
    PriorityFacade priorityFacade;

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActionFacade() {
        super(Action.class);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, Root<Action> root, BigInteger priorityId, BigInteger teamId, String actionName, String componentName) {
        List<Predicate> filters = new ArrayList<>();

        if (priorityId != null) {
            filters.add(cb.equal(root.get("priority"), priorityId));
        }

        if (actionName != null && !actionName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), actionName.toLowerCase()));
        }

        Join<Action, Component> componentJoin = null;

        if(componentName != null && !componentName.isEmpty() || teamId != null) {
           componentJoin = root.join("component");
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
    public List<Action> filterList(BigInteger priorityId, BigInteger teamId, String actionName, String componentName, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Action> cq = cb.createQuery(Action.class);
        Root<Action> root = cq.from(Action.class);
        cq.select(root);

        List<Predicate> filters = getFilters(cb, root, priorityId, teamId, actionName, componentName);

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
    public long countList(BigInteger priorityId, BigInteger teamId, String actionName, String componentName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Action> root = cq.from(Action.class);

        List<Predicate> filters = getFilters(cb, root, priorityId, teamId, actionName, componentName);
        
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public Action findByName(String name) {
        return null;
    }

    @RolesAllowed("jaws-admin")
    public void addAction(String name, BigInteger componentId, BigInteger priorityId, String correctiveAction,
                          String rationale, Boolean filterable,
                          Boolean latchable, BigInteger onDelaySeconds, BigInteger offDelaySeconds)
            throws UserFriendlyException {
        if(name == null || name.isBlank()) {
            throw new UserFriendlyException("Name is required");
        }

        if(componentId == null) {
            throw new UserFriendlyException("Component is required");
        }

        Component component = componentFacade.find(componentId);

        if(component == null) {
            throw new UserFriendlyException("Component not found with ID: " + componentId);
        }

        if(priorityId == null) {
            throw new UserFriendlyException("Priority is required");
        }

        Priority priority = priorityFacade.find(priorityId);

        if(priority == null) {
            throw new UserFriendlyException("Priority not found with ID: " + priorityId);
        }

        if(correctiveAction == null || correctiveAction.isBlank()) {
            throw new UserFriendlyException("Corrective Action is required");
        }

        if(rationale == null || rationale.isBlank()) {
            throw new UserFriendlyException("Rationale is required");
        }

        if(filterable == null) {
            throw new UserFriendlyException("Filterable is required");
        }

        if(latchable == null) {
            throw new UserFriendlyException("Latchable is required");
        }

        Action action = new Action();

        action.setName(name);
        action.setComponent(component);
        action.setPriority(priority);
        action.setCorrectiveAction(correctiveAction);
        action.setRationale(rationale);
        action.setFilterable(filterable);
        action.setLatchable(latchable);
        action.setOnDelaySeconds(onDelaySeconds);
        action.setOffDelaySeconds(offDelaySeconds);

        create(action);
    }

    @RolesAllowed("jaws-admin")
    public void removeAction(BigInteger actionId) throws UserFriendlyException {
        if(actionId == null) {
            throw new UserFriendlyException("Alarm ID is required");
        }

        Action action = find(actionId);

        if(action == null) {
            throw new UserFriendlyException("Action not found with ID: " + actionId);
        }

        remove(action);
    }
}
