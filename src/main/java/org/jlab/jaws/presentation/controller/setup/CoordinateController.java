package org.jlab.jaws.presentation.controller.setup;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.CoordinateFacade;
import org.jlab.jaws.persistence.entity.Coordinate;

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
@WebServlet(name = "CoordinateController", urlPatterns = {"/setup/coordinates"})
public class CoordinateController extends HttpServlet {

    @EJB
    CoordinateFacade coordinateFacade;

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

        List<Coordinate> coordinateList = coordinateFacade.findAll(new AbstractFacade.OrderDirective("topic"));

        request.setAttribute("coordinateList", coordinateList);

        request.getRequestDispatcher("/WEB-INF/views/setup/coordinates.jsp").forward(request, response);
    }
}
