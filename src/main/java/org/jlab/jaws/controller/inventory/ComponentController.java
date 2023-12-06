package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.ComponentFacade;
import org.jlab.jaws.persistence.entity.Component;

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
@WebServlet(name = "ComponentController", urlPatterns = {"/inventory/components"})
public class ComponentController extends HttpServlet {

    @EJB
    ComponentFacade componentFacade;

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

        List<Component> componentList = componentFacade.findAll(new AbstractFacade.OrderDirective("name"));

        request.setAttribute("componentList", componentList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/components.jsp").forward(request, response);
    }
}
