package org.jlab.jaws.presentation.controller.setup;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.SyncRuleFacade;
import org.jlab.jaws.persistence.model.RuleSet;

/**
 * @author ryans
 */
@WebServlet(
    name = "SyncSummaryController",
    urlPatterns = {"/setup/sync-summary"})
public class SyncSummaryController extends HttpServlet {

  @EJB SyncRuleFacade syncRuleFacade;

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

    List<RuleSet> ruleSetList = syncRuleFacade.findSystemRuleSetList();

    request.setAttribute("ruleSetList", ruleSetList);

    request
        .getRequestDispatcher("/WEB-INF/views/setup/sync-summary.jsp")
        .forward(request, response);
  }
}
