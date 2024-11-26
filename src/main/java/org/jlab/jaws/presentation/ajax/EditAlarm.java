package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
      String syncElementName = request.getParameter("syncElementName");
      BigInteger syncRuleId = ParamConverter.convertBigInteger(request, "syncRuleId");
      BigInteger syncElementId = ParamConverter.convertBigInteger(request, "syncElementId");

      // We leave existing fields as is unless a parameter name is provided.
      // This means if changing a single field, only that field needs to be provided.
      // To set a value to null/empty, then simply include parameter with empty string value
      // For multivalued parameters, set them to empty by including an extra parameter with the
      // field name prefixed with
      // empty and value 'Y'.  For example:
      // - Set location to empty with: "emptyLocationId=Y"
      Set<String> editableParams = new HashSet<>(Collections.list(request.getParameterNames()));

      if ("Y".equals(request.getParameter("emptyLocationId[]"))) {
        editableParams.add("locationId[]");
      }

      alarmFacade.editAlarm(
          editableParams,
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
          syncElementName,
          syncRuleId,
          syncElementId);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Alarm: " + e.getUserMessage();
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
