package org.jlab.jaws.presentation.util;

import org.hibernate.envers.RevisionListener;
import org.jlab.smoothness.presentation.filter.AuditContext;
import org.jlab.jaws.persistence.entity.ApplicationRevisionInfo;

/**
 * @author ryans
 */
public class ApplicationRevisionInfoListener implements RevisionListener {

    @Override
    public void newRevision(Object o) {
        ApplicationRevisionInfo revisionInfo = (ApplicationRevisionInfo) o;

        AuditContext context = AuditContext.getCurrentInstance();

        String ip = null;
        String username = null;

        if (context == null || "jaws-admin".equals(context.getExtra("effectiveRole"))) {
            ip = "localhost";
            username = "jaws-admin";
        } else {
            ip = context.getIp();
            username = context.getUsername();
        }

        revisionInfo.setAddress(ip);
        revisionInfo.setUsername(username);
    }
}
