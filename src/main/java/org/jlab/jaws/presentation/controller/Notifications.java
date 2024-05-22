package org.jlab.jaws.presentation.controller;

import org.jlab.jaws.business.session.*;
import org.jlab.jaws.entity.AlarmState;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.jaws.persistence.model.OverriddenState;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Notifications", urlPatterns = {"/notifications"})
public class Notifications extends HttpServlet {

    @EJB
    NotificationFacade notificationFacade;
    @EJB
    TeamFacade teamFacade;
    @EJB
    PriorityFacade priorityFacade;
    @EJB
    ActionFacade actionFacade;
    @EJB
    LocationFacade locationFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BinaryState state = convertState(request, "state");
        OverriddenAlarmType override = convertOverrideKey(request, "override");
        String activationType = request.getParameter("type");
        String alarmName = request.getParameter("alarmName");
        BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");
        String actionName = request.getParameter("actionName");
        BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
        String componentName = request.getParameter("componentName");
        BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 100;

        List<Notification> notificationList = notificationFacade.filterList(state, override, activationType, locationIdArray, priorityId, teamId, alarmName, actionName, componentName, offset, maxPerPage);
        List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
        List<OverriddenState> overrideList = Arrays.asList(OverriddenState.values());
        List<BinaryState> stateList = Arrays.asList(BinaryState.values());
        List<Priority> priorityList = priorityFacade.findAll(new AbstractFacade.OrderDirective("priorityId"));
        List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));
        Location locationRoot = locationFacade.findBranch(Location.TREE_ROOT);
        List<String> typeList = new ArrayList<>();

        typeList.add("NotActive");
        typeList.add("Simple");
        typeList.add("ChannelError");
        typeList.add("EPICS");
        typeList.add("Note");

        List<Location> selectedLocationList = new ArrayList<>();

        if(locationIdArray != null && locationIdArray.length > 0) {
            for(BigInteger id: locationIdArray) {
                if(id == null) {  // TODO: the convertBigIntegerArray method should be excluding empty/null
                    continue;
                }

                Location l = locationFacade.find(id);
                selectedLocationList.add(l);
            }
        }

        Priority selectedPriority = null;

        if(priorityId != null) {
            selectedPriority = priorityFacade.find(priorityId);
        }

        Team selectedTeam = null;

        if(teamId != null) {
            selectedTeam = teamFacade.find(teamId);
        }

        long totalRecords = notificationFacade.countList(state, override, activationType, locationIdArray, priorityId, teamId, alarmName, actionName, componentName);

        Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

        String selectionMessage = createSelectionMessage(paginator, state, override, activationType, selectedLocationList, selectedPriority, selectedTeam, alarmName, actionName, componentName);

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

        request.getRequestDispatcher("/WEB-INF/views/notifications.jsp").forward(request, response);
    }

    private OverriddenAlarmType convertOverrideKey(HttpServletRequest request, String name) {
        String value = request.getParameter(name);

        OverriddenAlarmType type = null;

        if(value != null && !value.isBlank()) {
            OverriddenState intermediate = OverriddenState.valueOf(value);
            type = intermediate.getOverrideType();
        }

        return type;
    }

    private BinaryState convertState(HttpServletRequest request, String name) {
        String value = request.getParameter(name);

        BinaryState state = null;

        if(value != null && !value.isBlank()) {
            state = BinaryState.valueOf(value);
        }

        return state;
    }

    private String createSelectionMessage(Paginator paginator, BinaryState state, OverriddenAlarmType override, String activationType, List<Location> locationList, Priority priority, Team team, String alarmName, String actionName, String componentName) {
        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Notifications ";

        List<String> filters = new ArrayList<>();

        if(state != null) {
            filters.add("State \"" + state + "\"");
        }

        if(override != null) {
            filters.add("Override \"" + override + "\"");
        }

        if(activationType != null && !activationType.isBlank()) {
            filters.add("Activation Type \"" + activationType + "\"");
        }

        if(locationList != null && !locationList.isEmpty()) {
            String sublist = "\"" + locationList.get(0).getName() + "\"";

            for(int i = 1; i < locationList.size(); i++) {
                Location l = locationList.get(i);
                sublist = sublist + ", \"" + l.getName() + "\"";
            }

            filters.add("Location " + sublist);
        }

        if(priority != null) {
            filters.add("Priority \"" + priority.getName() + "\"");
        }

        if(team != null) {
            filters.add("Team \"" + team.getName() + "\"");
        }

        if(alarmName != null && !alarmName.isBlank()) {
            filters.add("Alarm Name \"" + alarmName + "\"");
        }

        if(actionName != null && !actionName.isBlank()) {
            filters.add("Action Name \"" + actionName + "\"");
        }

        if(componentName != null && !componentName.isBlank()) {
            filters.add("Component Name \"" + componentName + "\"");
        }

        if (!filters.isEmpty()) {
            selectionMessage = filters.get(0);

            for (int i = 1; i < filters.size(); i++) {
                String filter = filters.get(i);
                selectionMessage += " and " + filter;
            }
        }

        if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
            selectionMessage = selectionMessage + " {" + formatter.format(
                    paginator.getTotalRecords()) + "}";
        } else {
            selectionMessage = selectionMessage + " {"
                    + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords()) + "}";
        }

        return selectionMessage;
    }
}
