package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.OverrideFacade;
import org.jlab.jaws.entity.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "SetOverride",
    urlPatterns = {"/ajax/set-override"})
public class SetOverride extends HttpServlet {

  private static final Logger logger = Logger.getLogger(SetOverride.class.getName());

  @EJB OverrideFacade overrideFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String[] nameArray = request.getParameterValues("name[]");
    OverriddenAlarmType type = UnsetOverride.convertOverrideType(request, "type");
    String comments = request.getParameter("comments");
    String oneshot = request.getParameter("oneshot");

    Date expiration = null;
    try {
      expiration = ParamConverter.convertFriendlyDateTime(request, "expiration");
    } catch (UserFriendlyException e) {
      throw new RuntimeException("Invalid expiration date format");
    }

    String reason = request.getParameter("reason");

    String stat = "ok";
    String error = null;

    AlarmOverrideUnion value = new AlarmOverrideUnion();

    switch (type) {
      case Disabled:
        value.setUnion(new DisabledOverride(comments));
        break;
      case Filtered:
        value.setUnion(new FilteredOverride(comments));
        break;
      case Masked:
        value.setUnion(new MaskedOverride());
        break;
      case OnDelayed:
        value.setUnion(new OnDelayedOverride());
        break;
      case OffDelayed:
        value.setUnion(new OffDelayedOverride());
        break;
      case Shelved:
        Long expirationLong = expiration.getTime();
        value.setUnion(
            new ShelvedOverride(
                "true".equals(oneshot), expirationLong, ShelvedReason.valueOf(reason), comments));
        break;
      case Latched:
        value.setUnion(new LatchedOverride());
        break;
    }

    try {
      overrideFacade.kafkaSet(nameArray, type, value);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to set overrides: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to set overrides: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to set overrides";
      logger.log(Level.SEVERE, "Unable to set overrides", e);
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      if (error != null) {
        gen.write("error", error);
      }
      gen.writeEnd();
    }
  }
}
