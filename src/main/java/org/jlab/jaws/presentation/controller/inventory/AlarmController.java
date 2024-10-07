package org.jlab.jaws.presentation.controller.inventory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.*;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "AlarmController",
    urlPatterns = {"/inventory/alarms"})
public class AlarmController extends HttpServlet {

  @EJB AlarmFacade alarmFacade;

  @EJB TeamFacade teamFacade;

  @EJB PriorityFacade priorityFacade;

  @EJB LocationFacade locationFacade;

  @EJB ActionFacade actionFacade;

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
    String pv = request.getParameter("pv");
    String alarmName = request.getParameter("alarmName");
    BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");
    String actionName = request.getParameter("actionName");
    BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
    String systemName = request.getParameter("systemName");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    Boolean synced = ParamConverter.convertYNBoolean(request, "synced");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<AlarmEntity> alarmList =
        alarmFacade.filterList(
            locationIdArray,
            priorityId,
            teamId,
            synced,
            pv,
            alarmName,
            actionName,
            systemName,
            offset,
            maxPerPage);
    List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<Priority> priorityList =
        priorityFacade.findAll(new AbstractFacade.OrderDirective("priorityId"));
    List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));
    Location locationRoot = locationFacade.findBranch(Location.TREE_ROOT);

    List<Location> selectedLocationList = new ArrayList<>();

    if (locationIdArray != null && locationIdArray.length > 0) {
      for (BigInteger id : locationIdArray) {
        if (id == null) { // TODO: the convertBigIntegerArray method should be excluding empty/null
          continue;
        }

        Location l = locationFacade.find(id);
        selectedLocationList.add(l);
      }
    }

    Priority selectedPriority = null;

    if (priorityId != null) {
      selectedPriority = priorityFacade.find(priorityId);
    }

    Team selectedTeam = null;

    if (teamId != null) {
      selectedTeam = teamFacade.find(teamId);
    }

    long totalRecords =
        alarmFacade.countList(
            locationIdArray, priorityId, teamId, synced, pv, alarmName, actionName, systemName);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        createSelectionMessage(
            paginator,
            selectedLocationList,
            selectedPriority,
            selectedTeam,
            synced,
            pv,
            alarmName,
            actionName,
            systemName);

    request.setAttribute("actionList", actionList);
    request.setAttribute("alarmList", alarmList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("teamList", teamList);
    request.setAttribute("priorityList", priorityList);
    request.setAttribute("locationRoot", locationRoot);
    request.setAttribute("paginator", paginator);

    request.getRequestDispatcher("/WEB-INF/views/inventory/alarms.jsp").forward(request, response);
  }

  private String createSelectionMessage(
      Paginator paginator,
      List<Location> locationList,
      Priority priority,
      Team team,
      Boolean synced,
      String pv,
      String alarmName,
      String actionName,
      String systemName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Alarms ";

    List<String> filters = new ArrayList<>();

    if (locationList != null && !locationList.isEmpty()) {
      String sublist = "\"" + locationList.get(0).getName() + "\"";

      for (int i = 1; i < locationList.size(); i++) {
        Location l = locationList.get(i);
        sublist = sublist + ", \"" + l.getName() + "\"";
      }

      filters.add("Location " + sublist);
    }

    if (priority != null) {
      filters.add("Priority \"" + priority.getName() + "\"");
    }

    if (team != null) {
      filters.add("Team \"" + team.getName() + "\"");
    }

    if (synced != null) {
      filters.add("Synced \"" + (synced ? "Yes" : "No") + "\"");
    }

    if (pv != null && !pv.isBlank()) {
      filters.add("PV \"" + pv + "\"");
    }

    if (alarmName != null && !alarmName.isBlank()) {
      filters.add("Alarm Name \"" + alarmName + "\"");
    }

    if (actionName != null && !actionName.isBlank()) {
      filters.add("Action Name \"" + actionName + "\"");
    }

    if (systemName != null && !systemName.isBlank()) {
      filters.add("System Name \"" + systemName + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    return selectionMessage;
  }
}
