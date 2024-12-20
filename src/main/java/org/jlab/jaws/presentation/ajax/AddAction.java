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
import org.jlab.jaws.business.session.ActionFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "AddAction",
    urlPatterns = {"/ajax/add-action"})
public class AddAction extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AddAction.class.getName());

  @EJB ActionFacade actionFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;

    try {
      String name = request.getParameter("name");
      BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
      BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
      String correctiveAction = request.getParameter("correctiveAction");
      String rationale = request.getParameter("rationale");
      Boolean filterable = ParamConverter.convertYNBoolean(request, "filterable");
      Boolean latchable = ParamConverter.convertYNBoolean(request, "latchable");
      BigInteger onDelaySeconds = ParamConverter.convertBigInteger(request, "onDelaySeconds");
      BigInteger offDelaySeconds = ParamConverter.convertBigInteger(request, "offDelaySeconds");

      actionFacade.addAction(
          name,
          systemId,
          priorityId,
          correctiveAction,
          rationale,
          filterable,
          latchable,
          onDelaySeconds,
          offDelaySeconds);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to add Action: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to add Action: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to add Action";
      logger.log(Level.SEVERE, "Unable to add Action", e);
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
