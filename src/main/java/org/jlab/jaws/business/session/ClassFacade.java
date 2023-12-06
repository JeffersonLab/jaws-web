package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.AlarmClass;
import org.jlab.jaws.persistence.entity.Category;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class ClassFacade extends AbstractFacade<AlarmClass> {
    private static final Logger logger = Logger.getLogger(ClassFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClassFacade() {
        super(AlarmClass.class);
    }

    @PermitAll
    public List<AlarmClass> filterList(int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AlarmClass> cq = cb.createQuery(AlarmClass.class);
        Root<AlarmClass> root = cq.from(AlarmClass.class);
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
        Root<AlarmClass> root = cq.from(AlarmClass.class);
        
        List<Predicate> filters = new ArrayList<>();
        
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public AlarmClass findByName(String name) {
        return null;
    }
}
