package org.jlab.jaws.presentation.controller.inventory.alarms;

import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.jaws.business.session.NotificationFacade;
import org.jlab.jaws.business.session.OverrideFacade;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.persistence.entity.AlarmOverride;
import org.jlab.jaws.persistence.entity.Notification;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author ryans
 */
@WebServlet(name = "AlarmDetailController", urlPatterns = {"/inventory/alarm-detail"})
public class AlarmDetailController extends HttpServlet {

    @EJB
    AlarmFacade alarmFacade;
    @EJB
    NotificationFacade notificationFacade;
    @EJB
    OverrideFacade overrideFacade;

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

        Alarm alarm = null;

        BigInteger alarmId = ParamConverter.convertBigInteger(request, "alarmId");
        String name = request.getParameter("name");

        if(alarmId != null) {
            alarm = alarmFacade.find(alarmId);
        } else if(name != null && !name.isBlank()) {
            alarm = alarmFacade.findByName(name);
        }

        if(alarm != null) {
            // We couldn't get @OneToOne to work in Hibernate 5.3 so we manually add.  This has nice benefit of only
            // loading for detail page and avoiding loading on list page, but it loads here as extra query instead of join
            // so not great.
            //
            // We did try adding @PrimaryKeyJoinColumn to Alarm and @MapsId with separate Id field to Notification with no
            // luck.  Oh well, this works.
            Notification notification = notificationFacade.find(alarm.getName());
            alarm.setNotification(notification);

            // OverrideList makes sense as a separate query attached a-la-carte to a transient field.
            List<AlarmOverride> overrideList = overrideFacade.findByAlarmName(alarm.getName());
            Collections.sort(overrideList);
            alarm.setOverrideList(overrideList);
        }

        boolean editable = false;

        request.setAttribute("alarm", alarm);
        request.setAttribute("editable", editable);

        request.getRequestDispatcher("/WEB-INF/views/inventory/alarms/alarm-detail.jsp").forward(request, response);
    }
}