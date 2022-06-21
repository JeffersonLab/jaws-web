package org.jlab.jaws;

import org.jlab.jaws.model.JAWSModel;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "JAWSTypes", urlPatterns = {"/resources/modules/jaws-admin-gui/jaws-types.mjs"})
public class JAWSTypes extends HttpServlet {

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

        request.setAttribute("model", model);

        request.getRequestDispatcher("/WEB-INF/views/jaws-types.jsp").forward(request, response);
    }
}