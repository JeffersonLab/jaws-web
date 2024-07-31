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
import org.jlab.jaws.business.session.PriorityFacade;
import org.jlab.jaws.persistence.entity.Priority;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListPriorities",
    urlPatterns = {"/ajax/list-priorities"})
public class ListPriorities extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListPriorities.class.getName());

  @EJB PriorityFacade priorityFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Priority> priorityList = null;

    String stat = "ok";
    String error = null;

    try {
      priorityList = priorityFacade.findAll(new AbstractFacade.OrderDirective("name"));
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list priorities";
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
        for (Priority priority : priorityList) {
          gen.writeStartObject();
          gen.write("name", priority.getName());
          gen.write("id", priority.getPriorityId());
          /*if(priority.getWeight() == null) {
            gen.writeNull("weight");
          } else {
            gen.write("weight", priority.getWeight());
          }*/
          gen.writeEnd();
        }
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
