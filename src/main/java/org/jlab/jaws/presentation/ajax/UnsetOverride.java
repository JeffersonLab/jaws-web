package org.jlab.jaws.presentation.ajax;

import org.jlab.jaws.business.session.OverrideFacade;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@WebServlet(name = "UnsetOverride", urlPatterns = {"/ajax/unset-override"})
public class UnsetOverride extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UnsetOverride.class.getName());

    @EJB
    OverrideFacade overrideFacade;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] nameArray = request.getParameterValues("name[]");
        OverriddenAlarmType type = convertOverrideType(request, "type");

        String stat = "ok";
        String error = null;
        
        try {
            overrideFacade.kafkaSet(nameArray, type, null);
        } catch(UserFriendlyException e) {
            stat = "fail";
            error = "Unable to unset overrides: " + e.getMessage();
        } catch (EJBAccessException e) {
            stat = "fail";
            error = "Unable to unset overrides: Not authenticated / authorized (do you need to re-login?)";
        } catch(RuntimeException e) {
            stat = "fail";
            error = "Unable to unset overrides";
            logger.log(Level.SEVERE, "Unable to unset overrides", e);
        }
        
        response.setContentType("application/json");

        OutputStream out = response.getOutputStream();
        
        try (JsonGenerator gen = Json.createGenerator(out)) {
            gen.writeStartObject()
                    .write("stat", stat);
            if(error != null) {
                gen.write("error", error);
            }
            gen.writeEnd();
        }
    }

    public static OverriddenAlarmType convertOverrideType(HttpServletRequest request, String name) {
        String value = request.getParameter(name);

        OverriddenAlarmType type = null;

        if(value != null && !value.isBlank()) {
            type = OverriddenAlarmType.valueOf(value);
        }

        return type;
    }

}
