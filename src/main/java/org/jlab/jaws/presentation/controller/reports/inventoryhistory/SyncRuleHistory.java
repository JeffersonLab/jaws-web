package org.jlab.jaws.presentation.controller.reports.inventoryhistory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.SyncRuleAudFacade;
import org.jlab.jaws.persistence.entity.aud.SyncRuleAud;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "SyncRuleHistory",
    urlPatterns = {"/reports/inventory-history/sync-rule"})
public class SyncRuleHistory extends HttpServlet {

  @EJB SyncRuleAudFacade audFacade;

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

    BigInteger syncRuleId = ParamConverter.convertBigInteger(request, "syncRuleId");
    BigInteger revisionId = ParamConverter.convertBigInteger(request, "revisionId");

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 5;

    List<SyncRuleAud> revisionList = null;
    Long totalRecords = 0L;

    if (syncRuleId != null) {
      revisionList = audFacade.filterList(syncRuleId, revisionId, offset, maxPerPage);
      totalRecords = audFacade.countFilterList(syncRuleId, revisionId);

      audFacade.loadStaff(revisionList);
    }

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    request.setAttribute("revisionList", revisionList);
    request.setAttribute("paginator", paginator);

    request
        .getRequestDispatcher("/WEB-INF/views/reports/inventory-history/sync-rule.jsp")
        .forward(request, response);
  }
}
