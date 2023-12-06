package org.jlab.jaws.controller.inventory.instances;

import org.jlab.jaws.business.session.InstanceFacade;
import org.jlab.jaws.persistence.entity.Instance;
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
@WebServlet(name = "InstanceDetailController", urlPatterns = {"/inventory/instances/detail"})
public class InstanceDetailController extends HttpServlet {

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

        Instance instance = null;

        BigInteger instanceId = ParamConverter.convertBigInteger(request, "instanceId");
        String name = request.getParameter("name");

        if(instanceId != null) {
            instance = instanceFacade.find(instanceId);
        } else if(name != null && !name.isBlank()) {
            instance = instanceFacade.findByName(name);
        }

        boolean editable = false;

        request.setAttribute("instance", instance);
        request.setAttribute("editable", editable);

        request.getRequestDispatcher("/WEB-INF/views/inventory/instances/instance-detail.jsp").forward(request, response);
    }
}