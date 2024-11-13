package org.jlab.jaws.presentation.controller.reports;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.ApplicationRevisionInfoFacade;
import org.jlab.jaws.persistence.entity.ApplicationRevisionInfo;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "InventoryHistoryReport",
    urlPatterns = {"/reports/inventory-history"})
public class InventoryHistoryReport extends HttpServlet {

  @EJB ApplicationRevisionInfoFacade revisionFacade;

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

    Date modifiedStart, modifiedEnd;

    try {
      modifiedStart = ParamConverter.convertFriendlyDateTime(request, "start");
      modifiedEnd = ParamConverter.convertFriendlyDateTime(request, "end");
    } catch (UserFriendlyException e) {
      throw new RuntimeException("Date format error", e);
    }

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<ApplicationRevisionInfo> transactionList =
        revisionFacade.filterList(modifiedStart, modifiedEnd, offset, maxPerPage);
    Long totalRecords = revisionFacade.countFilterList(modifiedStart, modifiedEnd);

    revisionFacade.loadUsers(transactionList);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Transactions ";

    String filters = message(modifiedStart, modifiedEnd);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    if (paginator.getTotalRecords() < maxPerPage && offset == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    request.setAttribute("transactionList", transactionList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("paginator", paginator);
    request.setAttribute("now", new Date());

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/inventory-history.jsp")
        .forward(request, response);
  }

  private String message(Date modifiedStart, Date modifiedEnd) {
    List<String> filters = new ArrayList<>();

    if (modifiedStart != null && modifiedEnd != null) {
      filters.add(TimeUtil.formatSmartRangeSeparateTime(modifiedStart, modifiedEnd));
    } else if (modifiedStart != null) {
      filters.add("Starting " + TimeUtil.formatSmartSingleTime(modifiedStart));
    } else if (modifiedEnd != null) {
      filters.add("Before " + TimeUtil.formatSmartSingleTime(modifiedEnd));
    }

    String message = "";

    if (!filters.isEmpty()) {
      message = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        message += " and " + filter;
      }
    }

    return message;
  }
}
