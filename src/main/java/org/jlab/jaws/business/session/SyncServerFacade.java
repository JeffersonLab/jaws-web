package org.jlab.jaws.business.session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.SyncServer;

/**
 * @author ryans
 */
@Stateless
public class SyncServerFacade extends AbstractFacade<SyncServer> {
  private static final Logger logger = Logger.getLogger(SyncServerFacade.class.getName());

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public SyncServerFacade() {
    super(SyncServer.class);
  }

  private List<Predicate> getFilters(CriteriaBuilder cb, Root<SyncServer> root, String name) {
    List<Predicate> filters = new ArrayList<>();

    if (name != null && !name.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
    }

    return filters;
  }

  @PermitAll
  public List<SyncServer> filterList(String name, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SyncServer> cq = cb.createQuery(SyncServer.class);
    Root<SyncServer> root = cq.from(SyncServer.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, name);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public long countList(String name) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SyncServer> root = cq.from(SyncServer.class);

    List<Predicate> filters = getFilters(cb, root, name);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @PermitAll
  public SyncServer findByName(String name) {
    List<SyncServer> list = this.filterList(name, 0, 1);

    SyncServer entity = null;

    if (list != null && !list.isEmpty()) {
      entity = list.get(0);
    }

    return entity;
  }
}
