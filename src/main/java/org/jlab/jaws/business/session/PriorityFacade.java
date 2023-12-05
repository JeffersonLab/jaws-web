package org.jlab.jaws.business.session;

import org.jlab.jaws.persistence.entity.Priority;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
}
