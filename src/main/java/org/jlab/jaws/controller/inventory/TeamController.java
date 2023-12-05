package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.PriorityFacade;
import org.jlab.jaws.business.session.TeamFacade;
import org.jlab.jaws.persistence.entity.Priority;
import org.jlab.jaws.persistence.entity.Team;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "TeamController", urlPatterns = {"/inventory/teams"})
public class TeamController extends HttpServlet {

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

        List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));

        request.setAttribute("teamList", teamList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/teams.jsp").forward(request, response);
    }
}
