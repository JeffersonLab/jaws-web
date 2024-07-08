package org.jlab.jaws.business.session;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Singleton;

@Singleton
public class ServerStatus {
  private boolean registrationsSent = false;
  private boolean overridesSent = false;

  @PermitAll
  public boolean isHealthy() {
    return registrationsSent && overridesSent;
  }

  @RolesAllowed("jaws-admin")
  public void setRegistrationsSent() {
    this.registrationsSent = true;
  }

  @RolesAllowed("jaws-admin")
  public void setOverridesSent() {
    this.overridesSent = true;
  }
}
