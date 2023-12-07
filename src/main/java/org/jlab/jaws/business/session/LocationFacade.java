package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Location;

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
public class LocationFacade extends AbstractFacade<Location> {
    private static final Logger logger = Logger.getLogger(LocationFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocationFacade() {
        super(Location.class);
    }

    @PermitAll
    public List<Component> filterList(int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root);
        
        List<Predicate> filters = new ArrayList<>();

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
    public long countList() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Component> root = cq.from(Component.class);
        
        List<Predicate> filters = new ArrayList<>();
        
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public Location findBranch(BigInteger locationId) {
        // We query all locations such that the EntityManager already resolved hierarchical parent/child relationships.
        TypedQuery<Location> q = em.createQuery(
                "select l from Location l left join fetch l.childList", Location.class);

        // Ignore ResultSet - EntityManger is now primed.
        q.getResultList();

        // Search should hit primed EntityManager cache.
        Location branchRoot = find(locationId);

        return branchRoot;
    }
}
