package org.jlab.jaws.presentation.controller.setup;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.SyncServerFacade;
import org.jlab.jaws.persistence.entity.SyncServer;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "SyncServerController",
    urlPatterns = {"/setup/sync-servers"})
public class SyncServerController extends HttpServlet {

  @EJB SyncServerFacade serverFacade;

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

    BigInteger syncId = ParamConverter.convertBigInteger(request, "syncId");
    String actionName = request.getParameter("actionName");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<SyncServer> serverList = serverFacade.filterList(null, offset, maxPerPage);

    long totalRecords = serverFacade.countList(null);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage = createSelectionMessage(paginator, syncId, actionName);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("serverList", serverList);
    request.setAttribute("paginator", paginator);

    request
        .getRequestDispatcher("/WEB-INF/views/setup/sync-servers.jsp")
        .forward(request, response);
  }

  private String createSelectionMessage(Paginator paginator, BigInteger syncId, String actionName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Sync Servers ";

    List<String> filters = new ArrayList<>();

    if (syncId != null) {
      filters.add("Server ID \"" + syncId + "\"");
    }

    if (actionName != null && !actionName.isBlank()) {
      filters.add("Action Name \"" + actionName + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
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

    return selectionMessage;
  }
}
