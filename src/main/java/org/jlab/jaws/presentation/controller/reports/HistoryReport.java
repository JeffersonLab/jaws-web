package org.jlab.jaws.presentation.controller.reports;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.*;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.jaws.persistence.model.OverriddenState;
import org.jlab.jaws.presentation.controller.Notifications;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "HistoryReport",
    urlPatterns = {"/reports/history"})
public class HistoryReport extends HttpServlet {

  @EJB NotificationHistoryFacade historyFacade;
  @EJB TeamFacade teamFacade;
  @EJB PriorityFacade priorityFacade;
  @EJB ActionFacade actionFacade;
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

    Date start, end;

    try {
      start = ParamConverter.convertFriendlyDateTime(request, "start");
      end = ParamConverter.convertFriendlyDateTime(request, "end");
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse date");
    }

    BinaryState state = Notifications.convertState(request, "state");
    Boolean overridden = ParamConverter.convertYNBoolean(request, "overridden");
    OverriddenAlarmType override = Notifications.convertOverrideKey(request, "override");
    String activationType = request.getParameter("type");
    String alarmName = request.getParameter("alarmName");
    BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");
    String actionName = request.getParameter("actionName");
    BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
    String componentName = request.getParameter("componentName");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    Boolean registered = ParamConverter.convertYNBoolean(request, "registered");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<NotificationHistory> notificationList =
        historyFacade.filterList(
            start,
            end,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            alarmName,
            actionName,
            componentName,
            offset,
            maxPerPage);
    List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<OverriddenState> overrideList = Arrays.asList(OverriddenState.values());
    List<BinaryState> stateList = Arrays.asList(BinaryState.values());
    List<Priority> priorityList =
        priorityFacade.findAll(new AbstractFacade.OrderDirective("priorityId"));
    List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));
    Location locationRoot = locationFacade.findBranch(Location.TREE_ROOT);
    List<String> typeList = new ArrayList<>();

    typeList.add("NotActive");
    typeList.add("Simple");
    typeList.add("ChannelError");
    typeList.add("EPICS");
    typeList.add("Note");

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
        historyFacade.countList(
            start,
            end,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            alarmName,
            actionName,
            componentName);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        Notifications.createSelectionMessage(
            paginator,
            start,
            end,
            state,
            overridden,
            override,
            activationType,
            selectedLocationList,
            selectedPriority,
            selectedTeam,
            registered,
            alarmName,
            actionName,
            componentName);

    request.setAttribute("notificationList", notificationList);
    request.setAttribute("actionList", actionList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("teamList", teamList);
    request.setAttribute("stateList", stateList);
    request.setAttribute("overrideList", overrideList);
    request.setAttribute("typeList", typeList);
    request.setAttribute("priorityList", priorityList);
    request.setAttribute("locationRoot", locationRoot);
    request.setAttribute("paginator", paginator);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/history.jsp")
        .forward(request, response);
  }
}
