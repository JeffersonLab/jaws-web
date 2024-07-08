package org.jlab.jaws.presentation.ajax.setup;

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
import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;

/**
 * @author ryans
 */
@WebServlet(
    name = "AddAlarmKeyValueList",
    urlPatterns = {"/ajax/setup/add-alarm-key-value-list"})
public class AddAlarmKeyValueList extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AddAlarmKeyValueList.class.getName());

  @EJB AlarmFacade alarmFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String alarms = request.getParameter("alarms");

    String stat = "ok";
    String error = null;

    try {
      alarmFacade.addKeyValueList(alarms);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to add Alarm Key Value List: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error =
          "Unable to add Alarm Key Value List: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to add Alarm Key Value List";
      logger.log(Level.SEVERE, "Unable to add Action Key Value List", e);
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
