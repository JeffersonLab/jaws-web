package org.jlab.jaws.presentation.ajax;

import org.jlab.jaws.business.session.ComponentFacade;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListComponents",
    urlPatterns = {"/ajax/list-components"})
public class ListComponents extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListComponents.class.getName());

  @EJB
  ComponentFacade componentFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String componentName = request.getParameter("componentName");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", Integer.MAX_VALUE);

    List<Component> componentList = null;

    String stat = "ok";
    String error = null;

    try {
      componentList = componentFacade.filterList(componentName, teamId, offset, max);
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
        for (Component component : componentList) {
          gen.writeStartObject();
          gen.write("name", component.getName());
          gen.write("id", component.getComponentId());
          gen.writeStartObject("team");
          gen.write("name", component.getTeam().getName());
          gen.write("id", component.getTeam().getTeamId());
          gen.writeEnd();
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
