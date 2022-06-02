package org.jlab.jaws;

import org.jlab.jaws.model.FieldDefinition;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "View", urlPatterns = {"/view/*"})
public class View extends HttpServlet {

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

        List<FieldDefinition> registrationFields = new ArrayList<>();
        registrationFields.add(new FieldDefinition("name", true, true));
        registrationFields.add(new FieldDefinition("category", false, true));
        registrationFields.add(new FieldDefinition("class", false, true));
        registrationFields.add(new FieldDefinition("location", false, true));
        registrationFields.add(new FieldDefinition("priority", false, true));
        registrationFields.add(new FieldDefinition("contact", false, true));

        List<FieldDefinition> classFields = new ArrayList<>();
        classFields.add(new FieldDefinition("name", true, true));
        classFields.add(new FieldDefinition("category", false, true));
        classFields.add(new FieldDefinition("priority", false, true));
        classFields.add(new FieldDefinition("contact", false, true));

        List<FieldDefinition> instanceFields = new ArrayList<>();
        instanceFields.add(new FieldDefinition("name", true, true));
        instanceFields.add(new FieldDefinition("class", false, true));
        instanceFields.add(new FieldDefinition("location", false, true));
        instanceFields.add(new FieldDefinition("epicspv", false, true));

        List<FieldDefinition> locationFields = new ArrayList<>();
        locationFields.add(new FieldDefinition("name", true, true));
        locationFields.add(new FieldDefinition("parent", false, true));

        List<FieldDefinition> categoryFields = new ArrayList<>();
        categoryFields.add(new FieldDefinition("name", true, true));

        request.setAttribute("registrationFields", registrationFields);
        request.setAttribute("classFields", classFields);
        request.setAttribute("instanceFields", instanceFields);
        request.setAttribute("locationFields", locationFields);
        request.setAttribute("categoryFields", categoryFields);

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}