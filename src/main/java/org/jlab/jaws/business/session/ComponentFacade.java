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
import org.jlab.jaws.clients.CategoryProducer;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Team;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@Stateless
public class ComponentFacade extends AbstractFacade<Component> {
  private static final Logger logger = Logger.getLogger(ComponentFacade.class.getName());

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @EJB TeamFacade teamFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ComponentFacade() {
    super(Component.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb, Root<Component> root, String componentName, BigInteger teamId) {
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
    Root<Component> root = cq.from(Component.class);

    List<Predicate> filters = getFilters(cb, root, componentName, teamId);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @RolesAllowed("jaws-admin")
  public void addComponent(String name, BigInteger teamId) throws UserFriendlyException {
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

    Component component = new Component();

    component.setName(name);
    component.setTeam(team);

    create(component);

    kafkaSet(Arrays.asList(component));
  }

  @RolesAllowed("jaws-admin")
  public void editComponent(BigInteger componentId, String name, BigInteger teamId)
      throws UserFriendlyException {
    if (componentId == null) {
      throw new UserFriendlyException("Component ID is required");
    }

    Component component = find(componentId);

    if (component == null) {
      throw new UserFriendlyException("Component not found with ID: " + componentId);
    }

    if (name == null || name.isBlank()) {
      throw new UserFriendlyException("Name is required");
    }

    if (teamId == null) {
      throw new UserFriendlyException("Team is required");
    }

    Team team = teamFacade.find(teamId);

    component.setName(name);
    component.setTeam(team);

    edit(component);

    kafkaSet(Arrays.asList(component));
  }

  @RolesAllowed("jaws-admin")
  public void removeComponent(BigInteger componentId) throws UserFriendlyException {
    if (componentId == null) {
      throw new UserFriendlyException("Component ID is required");
    }

    Component component = find(componentId);

    if (component == null) {
      throw new UserFriendlyException("Component not found with ID: " + componentId);
    }

    remove(component);

    kafkaUnset(Arrays.asList(component.getName()));
  }

  @PermitAll
  public Component findByName(String name) {
    List<Component> componentList = this.filterList(name, null, 0, 1);

    Component component = null;

    if (componentList != null && !componentList.isEmpty()) {
      component = componentList.get(0);
    }

    return component;
  }

  @RolesAllowed("jaws-admin")
  public void kafkaSet(List<Component> componentList) {
    if (componentList != null) {
      try (CategoryProducer producer =
          new CategoryProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (Component component : componentList) {
          String key = component.getName();

          String value = "";

          producer.send(key, value);
        }
      }
    }
  }

  @RolesAllowed("jaws-admin")
  public void kafkaUnset(List<String> list) {
    if (list != null) {
      try (CategoryProducer producer =
          new CategoryProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (String name : list) {
          producer.send(name, null);
        }
      }
    }
  }
}
