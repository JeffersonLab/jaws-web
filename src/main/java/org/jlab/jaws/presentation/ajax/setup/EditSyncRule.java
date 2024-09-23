package org.jlab.jaws.presentation.ajax.setup;

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
import org.jlab.jaws.business.session.SyncRuleFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditSyncRule",
    urlPatterns = {"/ajax/edit-sync"})
public class EditSyncRule extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditSyncRule.class.getName());

  @EJB SyncRuleFacade syncFacade;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    BigInteger id = ParamConverter.convertBigInteger(request, "id");
    BigInteger actionId = ParamConverter.convertBigInteger(request, "actionId");
    String server = request.getParameter("server");
    String query = request.getParameter("query");
    String screencommand = request.getParameter("screencommand");
    String pv = request.getParameter("pv");

    String stat = "ok";
    String error = null;

    try {
      syncFacade.editSync(id, actionId, server, query, screencommand, pv);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Sync Rule: " + e.getMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Sync Rule: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Sync Rule";
      logger.log(Level.SEVERE, "Unable to edit Sync Rule", e);
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
