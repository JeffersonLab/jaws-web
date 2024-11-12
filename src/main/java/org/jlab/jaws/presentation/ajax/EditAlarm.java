package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditAlarm",
    urlPatterns = {"/ajax/edit-alarm"})
public class EditAlarm extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditAlarm.class.getName());

  @EJB AlarmFacade alarmFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;

    try {
      BigInteger alarmId = ParamConverter.convertBigInteger(request, "alarmId");
      String name = request.getParameter("name");
      BigInteger actionId = ParamConverter.convertBigInteger(request, "actionId");
      BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId[]");
      String alias = request.getParameter("alias");
      String device = request.getParameter("device");
      String screenCommand = request.getParameter("screenCommand");
      String managedBy = request.getParameter("managedBy");
      String maskedBy = request.getParameter("maskedBy");
      String pv = request.getParameter("pv");
      BigInteger syncRuleId = ParamConverter.convertBigInteger(request, "syncRuleId");
      BigInteger elementId = ParamConverter.convertBigInteger(request, "elementId");

      alarmFacade.editAlarm(
          alarmId,
          name,
          actionId,
          locationIdArray,
          alias,
          device,
          screenCommand,
          managedBy,
          maskedBy,
          pv,
          syncRuleId,
          elementId);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Alarm: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Alarm: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Alarm";
      logger.log(Level.SEVERE, "Unable to edit Alarm", e);
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
