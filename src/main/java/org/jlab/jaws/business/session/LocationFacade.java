package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.persistence.entity.Location;

/**
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

  private List<Predicate> getFilters(CriteriaBuilder cb, Root<Location> root, String name) {
    List<Predicate> filters = new ArrayList<>();

    if (name != null && !name.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
    }

    return filters;
  }

  @PermitAll
  public List<Location> filterList(String name, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Location> cq = cb.createQuery(Location.class);
    Root<Location> root = cq.from(Location.class);
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
    Root<Location> root = cq.from(Location.class);

    List<Predicate> filters = getFilters(cb, root, name);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @PermitAll
  public Location findBranch(BigInteger locationId) {
    // We query all locations such that the EntityManager already resolved hierarchical parent/child
    // relationships.
    TypedQuery<Location> q =
        em.createQuery("select l from Location l left join fetch l.childList", Location.class);

    // Ignore ResultSet - EntityManger is now primed.
    q.getResultList();

    // Search should hit primed EntityManager cache.
    Location branchRoot = find(locationId);

    return branchRoot;
  }

  @PermitAll
  public Set<Location> findBranchAsSet(BigInteger locationId) {
    Location branchRoot = findBranch(locationId);

    Set<Location> locationSet = new HashSet<>();

    addToSet(branchRoot, locationSet);

    return locationSet;
  }

  private void addToSet(Location location, Set<Location> locationSet) {
    locationSet.add(location);

    for (Location child : location.getChildList()) {
      addToSet(child, locationSet);
    }
  }

  @PermitAll
  public Location findByName(String name) {
    List<Location> list = this.filterList(name, 0, 1);

    Location entity = null;

    if (list != null && !list.isEmpty()) {
      entity = list.get(0);
    }

    return entity;
  }
}
