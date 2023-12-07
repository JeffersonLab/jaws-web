package org.jlab.jaws.presentation.controller.inventory;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.LocationFacade;
import org.jlab.jaws.persistence.entity.Location;

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
@WebServlet(name = "LocationController", urlPatterns = {"/inventory/locations"})
public class LocationController extends HttpServlet {

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
        List<Location> locationList = locationFacade.findAll(new AbstractFacade.OrderDirective("locationId"));

        request.setAttribute("locationList", locationList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/locations.jsp").forward(request, response);
    }
}
