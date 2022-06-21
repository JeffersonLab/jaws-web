package org.jlab.jaws;

import org.jlab.jaws.model.JAWSModel;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "View", urlPatterns = {"/view/*"})
public class View extends HttpServlet {

    @Inject
    private JAWSModel model;

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

        request.setAttribute("alarmFields", model.getAlarmFields());
        request.setAttribute("activationFields", model.getActivationFields());
        request.setAttribute("categoryFields", model.getCategoryFields());
        request.setAttribute("classFields", model.getClassFields());
        request.setAttribute("instanceFields", model.getInstanceFields());
        request.setAttribute("locationFields", model.getLocationFields());
        request.setAttribute("notificationFields", model.getNotificationFields());
        request.setAttribute("overrideFields", model.getOverrideFields());
        request.setAttribute("registrationFields", model.getRegistrationFields());

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}