package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.ComponentFacade;
import org.jlab.jaws.business.session.TeamFacade;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Team;
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
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "ComponentController", urlPatterns = {"/inventory/components"})
public class ComponentController extends HttpServlet {

    @EJB
    ComponentFacade componentFacade;

    @EJB
    TeamFacade teamFacade;

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

        String componentName = request.getParameter("componentName");
        BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 100;

        List<Component> componentList = componentFacade.filterList(componentName, teamId, offset, maxPerPage);
        List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));

        Team selectedTeam = null;

        if(teamId != null) {
            selectedTeam = teamFacade.find(teamId);
        }

        long totalRecords = componentFacade.countList(componentName, teamId);

        Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

        String selectionMessage = createSelectionMessage(paginator, selectedTeam, componentName);

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("componentList", componentList);
        request.setAttribute("teamList", teamList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/inventory/components.jsp").forward(request, response);
    }

    private String createSelectionMessage(Paginator paginator, Team team, String componentName) {
        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Components ";

        List<String> filters = new ArrayList<>();

        if(team != null) {
            filters.add("Team \"" + team.getName() + "\"");
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
