package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
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
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@WebServlet(
    name = "AcknowledgeAll",
    urlPatterns = {"/ajax/acknowledge-all"})
public class AcknowledgeAll extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AcknowledgeAll.class.getName());

  @EJB OverrideFacade overrideFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;
    int count = 0;

    try {
      count = overrideFacade.acknowledgeAll();
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to acknowledge all: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error =
          "Unable to acknowledge all: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to acknowledge all";
      logger.log(Level.SEVERE, "Unable to acknowledge all", e);
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      gen.write("count", count);
      if (error != null) {
        gen.write("error", error);
      }
      gen.writeEnd();
    }
  }

  public static OverriddenAlarmType convertOverrideType(HttpServletRequest request, String name) {
    String value = request.getParameter(name);

    OverriddenAlarmType type = null;

    if (value != null && !value.isBlank()) {
      type = OverriddenAlarmType.valueOf(value);
    }

    return type;
  }
}
