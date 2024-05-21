package org.jlab.jaws.presentation.ajax;

import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

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
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Acknowledge", urlPatterns = {"/ajax/acknowledge"})
public class Acknowledge extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Acknowledge.class.getName());

    @EJB
    AlarmFacade alarmFacade;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] nameArray = request.getParameterValues("name[]");
        
        String stat = "ok";
        String error = null;
        
        try {
            alarmFacade.acknowledge(nameArray);
        } catch(UserFriendlyException e) {
            stat = "fail";
            error = "Unable to acknowledge Alarms: " + e.getMessage();
        } catch (EJBAccessException e) {
            stat = "fail";
            error = "Unable to acknowledge Alarms: Not authenticated / authorized (do you need to re-login?)";
        } catch(RuntimeException e) {
            stat = "fail";
            error = "Unable to acknowledge Alarms";
            logger.log(Level.SEVERE, "Unable to acknowledge Alarms", e);
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

}
