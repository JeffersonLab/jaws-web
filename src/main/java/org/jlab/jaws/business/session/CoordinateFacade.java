package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Coordinate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class CoordinateFacade extends AbstractFacade<Coordinate> {
    private static final Logger logger = Logger.getLogger(CoordinateFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoordinateFacade() {
        super(Coordinate.class);
    }
}
