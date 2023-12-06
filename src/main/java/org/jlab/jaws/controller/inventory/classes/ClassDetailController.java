package org.jlab.jaws.controller.inventory.classes;

import org.jlab.jaws.business.session.ClassFacade;
import org.jlab.jaws.persistence.entity.AlarmClass;
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
@WebServlet(name = "ClassDetailController", urlPatterns = {"/inventory/classes/detail"})
public class ClassDetailController extends HttpServlet {

    @EJB
    ClassFacade classFacade;

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

        AlarmClass entity = null;

        BigInteger classId = ParamConverter.convertBigInteger(request, "classId");
        String name = request.getParameter("name");

        if(classId != null) {
            entity = classFacade.find(classId);
        } else if(name != null && !name.isBlank()) {
            entity = classFacade.findByName(name);
        }

        boolean editable = false;

        request.setAttribute("entity", entity);
        request.setAttribute("editable", editable);

        request.getRequestDispatcher("/WEB-INF/views/inventory/classes/class-detail.jsp").forward(request, response);
    }
}