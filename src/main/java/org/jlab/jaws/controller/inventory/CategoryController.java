package org.jlab.jaws.controller.inventory;

import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.CategoryFacade;
import org.jlab.jaws.persistence.entity.Category;

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
@WebServlet(name = "CategoryController", urlPatterns = {"/inventory/categories"})
public class CategoryController extends HttpServlet {

    @EJB
    CategoryFacade categoryFacade;

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

        List<Category> categoryList = categoryFacade.findAll(new AbstractFacade.OrderDirective("name"));

        request.setAttribute("categoryList", categoryList);

        request.getRequestDispatcher("/WEB-INF/views/inventory/categories.jsp").forward(request, response);
    }
}
