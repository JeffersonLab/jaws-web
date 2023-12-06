package org.jlab.jaws.business.session;

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
public class ComponentFacade extends AbstractFacade<Component> {
    private static final Logger logger = Logger.getLogger(ComponentFacade.class.getName());
    
    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentFacade() {
        super(Component.class);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, Root<Component> root, String componentName, BigInteger teamId) {
        List<Predicate> filters = new ArrayList<>();

        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
        }

        if (teamId != null) {
            filters.add(cb.equal(root.get("team"), teamId));
        }

        return filters;
    }

    @PermitAll
    public List<Component> filterList(String componentName, BigInteger teamId, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root);
        
        List<Predicate> filters = getFilters(cb, root, componentName, teamId);

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
    public long countList(String componentName, BigInteger teamId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Component> root = cq.from(Component.class);

        List<Predicate> filters = getFilters(cb, root, componentName, teamId);

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }
}
