package org.jlab.jaws.controller.inventory.actions;

import org.jlab.jaws.business.session.ActionFacade;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;


/**
 *
 * @author ryans
 */
@WebServlet(name = "ActionDetailController", urlPatterns = {"/inventory/actions/detail"})
public class ActionDetailController extends HttpServlet {

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

        Action entity = null;

        BigInteger classId = ParamConverter.convertBigInteger(request, "classId");
        String name = request.getParameter("name");

        if(classId != null) {
            entity = actionFacade.find(classId);
        } else if(name != null && !name.isBlank()) {
            entity = actionFacade.findByName(name);
        }

        boolean editable = false;

        request.setAttribute("entity", entity);
        request.setAttribute("editable", editable);

        request.getRequestDispatcher("/WEB-INF/views/inventory/actions/action-detail.jsp").forward(request, response);
    }
}