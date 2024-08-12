package org.jlab.jaws.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.LocationFacade;
import org.jlab.jaws.persistence.entity.Location;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "Active",
    urlPatterns = {"/active"})
public class Active extends HttpServlet {

  @EJB LocationFacade locationFacade;

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

    BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");

    List<Location> selectedLocationList = new ArrayList<>();
    Set<Location> materializedLocations = new HashSet<>();
    String listActiveParams =
        "?state=Active&alwaysIncludeUnregistered=Y&alwaysIncludeUnfilterable=Y";

    if (locationIdArray != null && locationIdArray.length > 0) {
      for (BigInteger id : locationIdArray) {
        if (id == null) { // TODO: the convertBigIntegerArray method should be excluding empty/null
          continue;
        }

        Location l = locationFacade.find(id);
        selectedLocationList.add(l);

        listActiveParams = listActiveParams + "&locationId=" + id;

        Set<Location> subset = locationFacade.findBranchAsSet(id);
        materializedLocations.addAll(subset);
      }
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

    String selectionMessage = createSelectionMessage(selectedLocationList);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("locationRoot", locationRoot);
    request.setAttribute("materializedLocationsArrayStr", materializedLocationsArrayStr);
    request.setAttribute("listActiveParams", listActiveParams);

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
