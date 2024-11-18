package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.jaws.persistence.entity.AlarmEntity;
import org.jlab.jaws.persistence.entity.Location;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListAlarms",
    urlPatterns = {"/ajax/list-alarms"})
public class ListAlarms extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListAlarms.class.getName());

  @EJB AlarmFacade alarmFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<AlarmEntity> alarmList = null;

    String stat = "ok";
    String error = null;

    try {
      String alarmName = request.getParameter("alarmName");
      BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");
      String actionName = request.getParameter("actionName");
      BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
      String componentName = request.getParameter("componentName");
      BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
      int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
      int max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", Integer.MAX_VALUE);

      alarmList =
          alarmFacade.filterList(
              locationIdArray,
              priorityId,
              teamId,
              null,
              null,
              alarmName,
              actionName,
              componentName,
              offset,
              max);
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list Alarms";
      logger.log(Level.SEVERE, "Unable to list Alarms", e);
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      if (error != null) {
        gen.write("error", error);
        // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } else {
        gen.writeStartArray("list");
        for (AlarmEntity alarm : alarmList) {
          gen.writeStartObject();
          gen.write("name", alarm.getName());
          gen.write("id", alarm.getAlarmId());
          if (alarm.getAlias() == null) {
            gen.writeNull("alias");
          } else {
            gen.write("alias", alarm.getAlias());
          }
          if (alarm.getAction() == null) {
            gen.writeNull("action");
          } else {
            gen.writeStartObject("action");
            gen.write("name", alarm.getAction().getName());
            gen.write("id", alarm.getAction().getActionId());
            gen.writeEnd();
          }
          if (alarm.getDevice() == null) {
            gen.writeNull("device");
          } else {
            gen.write("device", alarm.getDevice());
          }
          if (alarm.getPv() == null) {
            gen.writeNull("pv");
          } else {
            gen.write("pv", alarm.getPv());
          }
          if (alarm.getSyncRule() == null) {
            gen.writeNull("syncRuleId");
          } else {
            gen.write("syncRuleId", alarm.getSyncRule().getSyncRuleId());
          }
          if (alarm.getSyncElementId() == null) {
            gen.writeNull("syncElementId");
          } else {
            gen.write("syncElementId", alarm.getSyncElementId());
          }
          if (alarm.getMaskedBy() == null) {
            gen.writeNull("maskedBy");
          } else {
            gen.write("maskedBy", alarm.getMaskedBy());
          }
          if (alarm.getScreenCommand() == null) {
            gen.writeNull("screenCommand");
          } else {
            gen.write("screenCommand", alarm.getScreenCommand());
          }
          gen.writeStartArray("locations");
          if (alarm.getLocationList() != null) {
            for (Location location : alarm.getLocationList()) {
              gen.writeStartObject();
              gen.write("name", location.getName());
              gen.write("id", location.getId());
              if (location.getWeight() == null) {
                gen.writeNull("weight");
              } else {
                gen.write("weight", location.getWeight());
              }
              if (location.getParent() == null) {
                gen.writeNull("parent");
              } else {
                gen.write("parent", location.getParent().getLocationId());
              }
              gen.writeEnd();
            }
          }
          gen.writeEnd();
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
