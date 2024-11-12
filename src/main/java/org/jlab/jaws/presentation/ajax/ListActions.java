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
import org.jlab.jaws.business.session.ActionFacade;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListActions",
    urlPatterns = {"/ajax/list-actions"})
public class ListActions extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListActions.class.getName());

  @EJB ActionFacade actionFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Action> actionList = null;

    String stat = "ok";
    String error = null;

    try {
      String actionName = request.getParameter("actionName");
      BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
      String componentName = request.getParameter("componentName");
      BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
      int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
      int max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", Integer.MAX_VALUE);

      actionList =
          actionFacade.filterList(priorityId, teamId, actionName, componentName, offset, max);
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list Actions";
      logger.log(Level.SEVERE, error, e);
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
        for (Action action : actionList) {
          gen.writeStartObject();
          gen.write("name", action.getName());
          gen.write("id", action.getActionId());
          gen.write("system", action.getSystem().getName());
          gen.write("correctiveActionMarkdown", action.getCorrectiveAction());
          gen.write("rationaleMarkdown", action.getRationale());
          gen.writeStartObject("priority");
          gen.write("name", action.getPriority().getName());
          gen.write("id", action.getPriority().getPriorityId());
          gen.writeEnd();
          gen.write("filterable", action.isFilterable());
          gen.write("latchable", action.isLatchable());
          if (action.getOnDelaySeconds() == null) {
            gen.writeNull("ondelayseconds");
          } else {
            gen.write("ondelayseconds", action.getOnDelaySeconds());
          }
          if (action.getOffDelaySeconds() == null) {
            gen.writeNull("offdelayseconds");
          } else {
            gen.write("offdelayseconds", action.getOffDelaySeconds());
          }
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
