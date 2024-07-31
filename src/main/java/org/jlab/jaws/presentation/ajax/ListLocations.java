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
import org.jlab.jaws.business.session.LocationFacade;
import org.jlab.jaws.persistence.entity.Location;

/**
 * @author ryans
 */
@WebServlet(
    name = "ListLocations",
    urlPatterns = {"/ajax/list-locations"})
public class ListLocations extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListLocations.class.getName());

  @EJB LocationFacade locationFacade;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Location> locationList = null;

    String stat = "ok";
    String error = null;

    try {
      locationList = locationFacade.findAll(new AbstractFacade.OrderDirective("locationId"));
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to list Locations";
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
        for (Location location : locationList) {
          gen.writeStartObject();
          gen.write("name", location.getName());
          gen.write("id", location.getLocationId());
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
        gen.writeEnd();
      }
      gen.writeEnd();
    }
  }
}
