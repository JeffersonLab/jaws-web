package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.jaws.persistence.entity.Alarm;

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
@WebServlet(name = "AlarmController", urlPatterns = {"/inventory/alarms"})
public class AlarmController extends HttpServlet {

    @EJB
    AlarmFacade alarmFacade;

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
        List<Alarm> alarmList = alarmFacade.filterList(0, Integer.MAX_VALUE);

        request.setAttribute("alarmList", alarmList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/alarms.jsp").forward(request, response);
    }
}
