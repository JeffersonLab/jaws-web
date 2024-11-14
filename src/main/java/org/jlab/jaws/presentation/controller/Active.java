package org.jlab.jaws.presentation.controller;

import java.io.IOException;
import java.util.*;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.LocationFacade;
import org.jlab.jaws.business.session.SystemFacade;
import org.jlab.jaws.persistence.entity.Location;
import org.jlab.jaws.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "Active",
    urlPatterns = {"/active"})
public class Active extends HttpServlet {

  @EJB LocationFacade locationFacade;
  @EJB SystemFacade systemFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String[] locationArray = request.getParameterValues("location");

    List<Location> selectedLocationList = new ArrayList<>();
    Set<Location> materializedLocations = new HashSet<>();
    String locationFilterStr = "";
    String rootLocationFilterStr = "";

    if (locationArray != null && locationArray.length > 0) {
      for (String name : locationArray) {
        if (name == null
            || name.isBlank()) { // TODO: the convertBigIntegerArray method should be excluding
          // empty/null
          continue;
        }

        Location l = locationFacade.findByName(name);
        selectedLocationList.add(l);

        locationFilterStr = locationFilterStr + "&locationId=" + l.getLocationId();

        Set<Location> subset = locationFacade.findBranchAsSet(l.getLocationId());
        materializedLocations.addAll(subset);
      }
    }

    if (!locationFilterStr.isBlank()) {
      rootLocationFilterStr = "?" + locationFilterStr.substring(1);
    }

    String materializedLocationsArrayStr = "[]";

    if (!materializedLocations.isEmpty()) {
      Iterator<Location> iterator = materializedLocations.iterator();

      materializedLocationsArrayStr = "['" + iterator.next().getName() + "'";

      while (iterator.hasNext()) {
        materializedLocationsArrayStr += ",'" + iterator.next().getName() + "'";
      }

      materializedLocationsArrayStr = materializedLocationsArrayStr + "]";
    }

    Location locationRoot = locationFacade.findBranch(Location.TREE_ROOT);

    List<Location> locationList = locationFacade.findAll();

    List<SystemEntity> systemList = systemFacade.findAll(new AbstractFacade.OrderDirective("name"));

    String selectionMessage = createSelectionMessage(selectedLocationList);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("locationRoot", locationRoot);
    request.setAttribute("locationList", locationList);
    request.setAttribute("materializedLocationsArrayStr", materializedLocationsArrayStr);
    request.setAttribute("locationFilterStr", locationFilterStr);
    request.setAttribute("RootlocationFilterStr", rootLocationFilterStr);
    request.setAttribute("systemList", systemList);

    request.getRequestDispatcher("/WEB-INF/views/active.jsp").forward(request, response);
  }

  public static String createSelectionMessage(List<Location> locationList) {
    String selectionMessage = "";

    List<String> filters = new ArrayList<>();

    if (locationList != null && !locationList.isEmpty()) {
      String sublist = "\"" + locationList.get(0).getName() + "\"";

      for (int i = 1; i < locationList.size(); i++) {
        Location l = locationList.get(i);
        sublist = sublist + ", \"" + l.getName() + "\"";
      }

      filters.add("Location " + sublist);
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    return selectionMessage;
  }
}
