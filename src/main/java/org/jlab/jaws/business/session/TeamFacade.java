package org.jlab.jaws.business.session;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.jaws.persistence.entity.Team;

/**
 * @author ryans
 */
@Stateless
public class TeamFacade extends AbstractFacade<Team> {
  private static final Logger logger = Logger.getLogger(TeamFacade.class.getName());

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public TeamFacade() {
    super(Team.class);
  }
}
