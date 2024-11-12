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
import org.jlab.jaws.business.session.SystemFacade;
import org.jlab.jaws.persistence.entity.SystemEntity;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListSystems",
    urlPatterns = {"/ajax/list-systems"})
public class ListSystems extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListSystems.class.getName());

  @EJB SystemFacade systemFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<SystemEntity> systemList = null;

    String stat = "ok";
    String error = null;

    try {
      String systemName = request.getParameter("systemName");
      BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
      int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
      int max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", Integer.MAX_VALUE);

      systemList = systemFacade.filterList(systemName, teamId, offset, max);
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list components";
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
        for (SystemEntity system : systemList) {
          gen.writeStartObject();
          gen.write("name", system.getName());
          gen.write("id", system.getSystemId());
          gen.writeStartObject("team");
          gen.write("name", system.getTeam().getName());
          gen.write("id", system.getTeam().getTeamId());
          gen.writeEnd();
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
