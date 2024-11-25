package org.jlab.jaws.presentation.controller.setup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

    String systemName = request.getParameter("systemName");

    List<RuleSet> ruleSetList = syncRuleFacade.findSystemRuleSetList(systemName);

    long totalRecords = 0;

    if (ruleSetList != null) {
      for (RuleSet rs : ruleSetList) {
        totalRecords = totalRecords + rs.count();
      }
    }

    String selectionMessage = createSelectionMessage(totalRecords, systemName);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("ruleSetList", ruleSetList);

    request
        .getRequestDispatcher("/WEB-INF/views/setup/sync-summary.jsp")
        .forward(request, response);
  }

  private String createSelectionMessage(long totalRecords, String systemName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Sync Rules ";

    List<String> filters = new ArrayList<>();

    if (systemName != null && !systemName.isBlank()) {
      filters.add("System Name \"" + systemName + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    selectionMessage = selectionMessage + " {" + formatter.format(totalRecords) + "}";

    return selectionMessage;
  }
}
