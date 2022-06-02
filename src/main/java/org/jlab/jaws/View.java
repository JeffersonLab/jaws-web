package org.jlab.jaws;

import org.jlab.jaws.model.FieldDefinition;
import org.jlab.jaws.model.FieldType;

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
        registrationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        registrationFields.add(new FieldDefinition("category", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("class", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("location", FieldType.MULTI_ENUM, false, true));
        registrationFields.add(new FieldDefinition("priority", FieldType.ENUM, false, true));
        registrationFields.add(new FieldDefinition("rationale", FieldType.MARKDOWN, false, false));
        registrationFields.add(new FieldDefinition("action", FieldType.MARKDOWN, false, false));
        registrationFields.add(new FieldDefinition("latching", FieldType.BOOLEAN, false, false));
        registrationFields.add(new FieldDefinition("filterable", FieldType.BOOLEAN, false, false));
        registrationFields.add(new FieldDefinition("ondelay", FieldType.NUMBER, false, false));
        registrationFields.add(new FieldDefinition("offdelay", FieldType.NUMBER, false, false));
        registrationFields.add(new FieldDefinition("contact", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("epicspv", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("maskedby", FieldType.STRING, false, false));
        registrationFields.add(new FieldDefinition("screencommand", FieldType.STRING, false, false));

        List<FieldDefinition> classFields = new ArrayList<>();
        classFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        classFields.add(new FieldDefinition("category", FieldType.STRING, false, true));
        classFields.add(new FieldDefinition("priority", FieldType.ENUM, false, true));
        classFields.add(new FieldDefinition("rationale", FieldType.MARKDOWN, false, false));
        classFields.add(new FieldDefinition("action", FieldType.MARKDOWN, false, false));
        classFields.add(new FieldDefinition("latching", FieldType.BOOLEAN, false, false));
        classFields.add(new FieldDefinition("filterable", FieldType.BOOLEAN, false, false));
        classFields.add(new FieldDefinition("ondelay", FieldType.NUMBER,false, false));
        classFields.add(new FieldDefinition("offdelay", FieldType.NUMBER, false, false));
        classFields.add(new FieldDefinition("contact", FieldType.STRING, false, true));

        List<FieldDefinition> instanceFields = new ArrayList<>();
        instanceFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        instanceFields.add(new FieldDefinition("class", FieldType.STRING, false, true));
        instanceFields.add(new FieldDefinition("location", FieldType.MULTI_ENUM, false, true));
        instanceFields.add(new FieldDefinition("epicspv", FieldType.STRING, false, true));
        instanceFields.add(new FieldDefinition("maskedby", FieldType.STRING, false, false));
        instanceFields.add(new FieldDefinition("screencommand", FieldType.STRING, false, false));

        List<FieldDefinition> locationFields = new ArrayList<>();
        locationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        locationFields.add(new FieldDefinition("parent", FieldType.STRING, false, true));

        List<FieldDefinition> categoryFields = new ArrayList<>();
        categoryFields.add(new FieldDefinition("name", FieldType.STRING, true, true));

        request.setAttribute("registrationFields", registrationFields);
        request.setAttribute("classFields", classFields);
        request.setAttribute("instanceFields", instanceFields);
        request.setAttribute("locationFields", locationFields);
        request.setAttribute("categoryFields", categoryFields);

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}