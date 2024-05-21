package org.jlab.jaws.business.session;

import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.OverrideProducer;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.persistence.entity.AlarmOverride;
import org.jlab.jaws.persistence.entity.OverridePK;
import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Stateless
public class OverrideFacade extends AbstractFacade<AlarmOverride> {
    private static final Logger logger = Logger.getLogger(OverrideFacade.class.getName());

    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OverrideFacade() {
        super(AlarmOverride.class);
    }

    // Note: Can't restrict to jaws-admin because caller in KafkaOverrideFacade RunAs doesn't work
    @PermitAll
    public void oracleSet(Alarm alarm, OverriddenAlarmType type, AlarmOverrideUnion value) {
        em.createQuery("delete from AlarmOverride o where o.overridePK = :a").setParameter("a", new OverridePK(alarm, type)).executeUpdate();

        AlarmOverride override = new AlarmOverride();

        OverridePK pk = new OverridePK();
        pk.setAlarm(alarm);
        pk.setType(type);

        override.setOverridePK(pk);

        if(value != null) {
            if (value.getUnion() instanceof DisabledOverride) {
                DisabledOverride disabledOverride = (DisabledOverride) value.getUnion();
                override.setComments(disabledOverride.getComments());
            } else if(value.getUnion() instanceof FilteredOverride) {
                FilteredOverride filteredOverride = (FilteredOverride) value.getUnion();
                override.setComments(filteredOverride.getFiltername());
            } else if(value.getUnion() instanceof ShelvedOverride) {
                ShelvedOverride shelvedOverride = (ShelvedOverride) value.getUnion();
                override.setComments(shelvedOverride.getComments());
                override.setOneshot(shelvedOverride.getOneshot());
                override.setExpiration(new Date(shelvedOverride.getExpiration()));
                override.setShelvedReason(shelvedOverride.getReason().name());
            }
        }

        create(override);
    }

    @RolesAllowed("jaws-admin")
    public void kafkaSet(String[] nameArray, OverriddenAlarmType type, AlarmOverrideUnion value) throws UserFriendlyException {
        if(nameArray == null || nameArray.length == 0) {
            throw new UserFriendlyException("Names selection must not be empty");
        }

        if(type == null) {
            throw new UserFriendlyException("Type selection must not be null");
        }

        try(OverrideProducer producer = new OverrideProducer(KafkaConfig.getProducerPropsWithRegistry())) {
            for (String name : nameArray) {
                AlarmOverrideKey key = new AlarmOverrideKey(name, type);
                producer.send(key, value);
            }
        }
    }
}
