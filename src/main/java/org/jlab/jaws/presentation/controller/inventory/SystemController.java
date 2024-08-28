package org.jlab.jaws.presentation.controller.inventory;

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
import org.jlab.jaws.business.session.AbstractFacade;
import org.jlab.jaws.business.session.SystemFacade;
import org.jlab.jaws.business.session.TeamFacade;
import org.jlab.jaws.persistence.entity.SystemEntity;
import org.jlab.jaws.persistence.entity.Team;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "SystemController",
    urlPatterns = {"/inventory/systems"})
public class SystemController extends HttpServlet {

  @EJB SystemFacade systemFacade;

  @EJB TeamFacade teamFacade;

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
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<SystemEntity> systemList = systemFacade.filterList(systemName, teamId, offset, maxPerPage);
    List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));

    Team selectedTeam = null;

    if (teamId != null) {
      selectedTeam = teamFacade.find(teamId);
    }

    long totalRecords = systemFacade.countList(systemName, teamId);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage = createSelectionMessage(paginator, selectedTeam, systemName);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("systemList", systemList);
    request.setAttribute("teamList", teamList);
    request.setAttribute("paginator", paginator);

    request.getRequestDispatcher("/WEB-INF/views/inventory/systems.jsp").forward(request, response);
  }

  private String createSelectionMessage(Paginator paginator, Team team, String systemName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Systems ";

    List<String> filters = new ArrayList<>();

    if (team != null) {
      filters.add("Team \"" + team.getName() + "\"");
    }

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
