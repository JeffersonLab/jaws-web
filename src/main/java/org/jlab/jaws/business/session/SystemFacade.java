package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.SystemProducer;
import org.jlab.jaws.entity.AlarmSystem;
import org.jlab.jaws.persistence.entity.SystemEntity;
import org.jlab.jaws.persistence.entity.Team;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class SystemFacade extends AbstractFacade<SystemEntity> {
  private static final Logger logger = Logger.getLogger(SystemFacade.class.getName());

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @EJB TeamFacade teamFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public SystemFacade() {
    super(SystemEntity.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb, Root<SystemEntity> root, String componentName, BigInteger teamId) {
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
  public List<SystemEntity> filterList(
      String componentName, BigInteger teamId, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SystemEntity> cq = cb.createQuery(SystemEntity.class);
    Root<SystemEntity> root = cq.from(SystemEntity.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, componentName, teamId);

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
  public long countList(String componentName, BigInteger teamId) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SystemEntity> root = cq.from(SystemEntity.class);

    List<Predicate> filters = getFilters(cb, root, componentName, teamId);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @RolesAllowed("jaws-admin")
  public void addSystem(String name, BigInteger teamId) throws UserFriendlyException {
    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (teamId == null) {
      throw new UserFriendlyException("Team is required");
    }

    Team team = teamFacade.find(teamId);

    if (team == null) {
      throw new UserFriendlyException("Team not found with ID: " + teamId);
    }

    SystemEntity system = new SystemEntity();

    system.setName(name);
    system.setTeam(team);

    create(system);

    kafkaSet(Arrays.asList(system));
  }

  @RolesAllowed("jaws-admin")
  public void editSystem(BigInteger systemId, String name, BigInteger teamId)
      throws UserFriendlyException {
    if (systemId == null) {
      throw new UserFriendlyException("System ID is required");
    }

    SystemEntity system = find(systemId);

    if (system == null) {
      throw new UserFriendlyException("System not found with ID: " + systemId);
    }

    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (teamId == null) {
      throw new UserFriendlyException("Team is required");
    }

    Team team = teamFacade.find(teamId);

    system.setName(name);
    system.setTeam(team);

    edit(system);

    kafkaSet(Arrays.asList(system));
  }

  @RolesAllowed("jaws-admin")
  public void removeSystem(BigInteger systemId) throws UserFriendlyException {
    if (systemId == null) {
      throw new UserFriendlyException("System ID is required");
    }

    SystemEntity system = find(systemId);

    if (system == null) {
      throw new UserFriendlyException("System not found with ID: " + systemId);
    }

    remove(system);

    kafkaUnset(Arrays.asList(system.getName()));
  }

  @PermitAll
  public SystemEntity findByName(String name) {
    List<SystemEntity> systemList = this.filterList(name, null, 0, 1);

    SystemEntity system = null;

    if (systemList != null && !systemList.isEmpty()) {
      system = systemList.get(0);
    }

    return system;
  }

  @RolesAllowed("jaws-admin")
  public void kafkaSet(List<SystemEntity> systemList) {
    if (systemList != null) {
      try (SystemProducer producer =
          new SystemProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (SystemEntity component : systemList) {
          String key = component.getName();

          Team team = component.getTeam();

          AlarmSystem value = new AlarmSystem(team.getName());

          producer.send(key, value);
        }
      }
    }
  }

  @RolesAllowed("jaws-admin")
  public void kafkaUnset(List<String> list) {
    if (list != null) {
      try (SystemProducer producer =
          new SystemProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (String name : list) {
          producer.send(name, null);
        }
      }
    }
  }
}
