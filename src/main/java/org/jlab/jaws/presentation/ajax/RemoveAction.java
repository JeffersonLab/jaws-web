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
    name = "RemoveAction",
    urlPatterns = {"/ajax/remove-action"})
public class RemoveAction extends HttpServlet {

  private static final Logger logger = Logger.getLogger(RemoveAction.class.getName());

  @EJB ActionFacade actionFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    BigInteger id = ParamConverter.convertBigInteger(request, "id");

    String stat = "ok";
    String error = null;

    try {
      actionFacade.removeAction(id);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to remove Action: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to remove Action: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to remove Action";
      logger.log(Level.SEVERE, "Unable to remove Alarm", e);
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
