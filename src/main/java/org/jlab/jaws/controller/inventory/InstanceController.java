package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.InstanceFacade;
import org.jlab.jaws.persistence.entity.Instance;

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
@WebServlet(name = "InstanceController", urlPatterns = {"/inventory/instances"})
public class InstanceController extends HttpServlet {

    @EJB
    InstanceFacade instanceFacade;

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
        List<Instance> instanceList = instanceFacade.filterList(0, Integer.MAX_VALUE);

        request.setAttribute("instanceList", instanceList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/instances.jsp").forward(request, response);
    }
}
