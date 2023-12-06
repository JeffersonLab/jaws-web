package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.ActionFacade;
import org.jlab.jaws.persistence.entity.Action;

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
@WebServlet(name = "ActionController", urlPatterns = {"/inventory/actions"})
public class ActionController extends HttpServlet {

    @EJB
    ActionFacade actionFacade;

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

        List<Action> actionList = actionFacade.filterList(0, Integer.MAX_VALUE);

        request.setAttribute("actionList", actionList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/actions.jsp").forward(request, response);
    }
}
