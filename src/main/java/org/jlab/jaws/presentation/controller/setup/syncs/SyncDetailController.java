package org.jlab.jaws.presentation.controller.setup.syncs;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.jaws.business.session.SyncRuleFacade;
import org.jlab.jaws.persistence.entity.AlarmEntity;
import org.jlab.jaws.persistence.entity.SyncRule;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "SyncDetailController",
    urlPatterns = {"/setup/sync-detail"})
public class SyncDetailController extends HttpServlet {

  @EJB SyncRuleFacade syncFacade;
  @EJB
  AlarmFacade alarmFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    SyncRule rule = null;

    BigInteger syncRuleId = ParamConverter.convertBigInteger(request, "syncRuleId");

    if (syncRuleId != null) {
      rule = syncFacade.find(syncRuleId);
    }

    List<AlarmEntity> alarmList = null;
    List<AlarmEntity> existingList = null;
    String error = null;

    if (rule != null) {
      try {
        alarmList = syncFacade.executeRule(rule);

        existingList = alarmFacade.findByRule(rule);
      } catch (UserFriendlyException e) {
        error = e.getMessage();
      }
    }

    boolean editable = false;

    request.setAttribute("rule", rule);
    request.setAttribute("alarmList", alarmList);
    request.setAttribute("existingList", existingList);
    request.setAttribute("error", error);
    request.setAttribute("editable", editable);

    request
        .getRequestDispatcher("/WEB-INF/views/setup/syncs/sync-detail.jsp")
        .forward(request, response);
  }
}
