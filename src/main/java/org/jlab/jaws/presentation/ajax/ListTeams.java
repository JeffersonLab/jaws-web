package org.jlab.jaws.presentation.ajax;

import java.io.IOException;
import java.io.OutputStream;
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
import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.TeamFacade;
import org.jlab.jaws.persistence.entity.Team;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListTeams",
    urlPatterns = {"/ajax/list-teams"})
public class ListTeams extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListTeams.class.getName());

  @EJB TeamFacade teamFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Team> teamList = null;

    String stat = "ok";
    String error = null;

    try {
      teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list teams";
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
        for (Team team : teamList) {
          gen.writeStartObject();
          gen.write("name", team.getName());
          gen.write("id", team.getTeamId());
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
