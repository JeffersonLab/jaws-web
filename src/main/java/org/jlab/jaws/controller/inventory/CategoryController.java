package org.jlab.jaws.controller.inventory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author ryans
 */
@WebServlet(name = "CategoryController", urlPatterns = {"/inventory/categories"})
public class CategoryController extends HttpServlet {
    
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

        
        request.getRequestDispatcher("/WEB-INF/views/inventory/categories.jsp").forward(request, response);
    }
}
