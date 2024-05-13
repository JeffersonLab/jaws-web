package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Priority;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class PriorityFacade extends AbstractFacade<Priority> {
    private static final Logger logger = Logger.getLogger(PriorityFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PriorityFacade() {
        super(Priority.class);
    }

    private List<Predicate> getFilters(CriteriaBuilder cb, Root<Priority> root, String name) {
        List<Predicate> filters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
        }

        return filters;
    }

    @PermitAll
    public List<Priority> filterList(String name, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Priority> cq = cb.createQuery(Priority.class);
        Root<Priority> root = cq.from(Priority.class);
        cq.select(root);

        List<Predicate> filters = getFilters(cb, root, name);

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
    public Priority findByName(String name) {
        List<Priority> list = this.filterList(name, 0, 1);

        Priority entity = null;

        if(list != null && !list.isEmpty()) {
            entity = list.get(0);
        }

        return entity;
    }
}
