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
import org.jlab.jaws.business.session.SystemFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditComponent",
    urlPatterns = {"/ajax/edit-component"})
public class EditComponent extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditComponent.class.getName());

  @EJB SystemFacade systemFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    BigInteger id = ParamConverter.convertBigInteger(request, "id");
    String name = request.getParameter("name");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");

    String stat = "ok";
    String error = null;

    try {
      systemFacade.editSystem(id, name, teamId);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Component: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Component: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Component";
      logger.log(Level.SEVERE, "Unable to edit Component", e);
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
