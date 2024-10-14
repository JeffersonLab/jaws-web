package org.jlab.jaws.presentation.controller.setup.syncs;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.AlarmFacade;
import org.jlab.jaws.business.session.SyncRuleFacade;
import org.jlab.jaws.persistence.entity.AlarmEntity;
import org.jlab.jaws.persistence.entity.SyncRule;
import org.jlab.jaws.persistence.model.AlarmSyncDiff;
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
  @EJB AlarmFacade alarmFacade;

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
    String summary = request.getParameter("summary");

    if (syncRuleId != null) {
      rule = syncFacade.find(syncRuleId);
    }

    LinkedHashMap<BigInteger, AlarmEntity> remoteList = null;
    LinkedHashMap<String, AlarmEntity> danglingByNameList = null;
    LinkedHashMap<String, AlarmEntity> danglingByPvList = null;
    List<AlarmEntity> localList = null;
    String error = null;
    AlarmSyncDiff diff = null;
    long collisionCount = 0;
    long matchCount = 0;
    long addCount = 0;
    long removeCount = 0;
    long updateCount = 0;
    long linkCount = 0;

    if (rule != null) {
      try {
        remoteList = syncFacade.executeRule(rule);

        localList = alarmFacade.findByRule(rule);

        diff = alarmFacade.diff(remoteList, localList);

        danglingByNameList = alarmFacade.findDanglingByName(diff.addList);
        danglingByPvList = alarmFacade.findDanglingByPv(diff.addList);

        HashSet<BigInteger> alarmCollisions = new HashSet<BigInteger>();
        for (AlarmEntity alarm : danglingByNameList.values()) {
          alarmCollisions.add(alarm.getAlarmId());
        }

        for (AlarmEntity alarm : danglingByPvList.values()) {
          alarmCollisions.add(alarm.getAlarmId());
        }

        collisionCount = alarmCollisions.size();

        matchCount = diff.matchList.size();
        addCount = Math.max(0, diff.addList.size() - collisionCount);
        removeCount = diff.removeList.size();
        updateCount = diff.updateList.size();
        linkCount = collisionCount;

      } catch (UserFriendlyException e) {
        error = e.getUserMessage();
      }
    }

    if ("Y".equals(summary)) {
      response.setContentType("application/json");

      OutputStream out = response.getOutputStream();

      try (JsonGenerator gen = Json.createGenerator(out)) {
        gen.writeStartObject();
        if (error != null) {
          gen.write("error", error);
        } else {
          gen.write("matchCount", matchCount);
          gen.write("addCount", addCount);
          gen.write("removeCount", removeCount);
          gen.write("updateCount", updateCount);
          gen.write("linkCount", linkCount);
        }
        gen.writeEnd();
      }
    } else {
      request.setAttribute("rule", rule);
      request.setAttribute("remoteList", remoteList);
      request.setAttribute("localList", localList);
      request.setAttribute("error", error);
      request.setAttribute("diff", diff);
      request.setAttribute("danglingByNameList", danglingByNameList);
      request.setAttribute("danglingByPvList", danglingByPvList);
      request.setAttribute("collisionCount", collisionCount);
      request.setAttribute("matchCount", matchCount);
      request.setAttribute("addCount", addCount);
      request.setAttribute("removeCount", removeCount);
      request.setAttribute("updateCount", updateCount);
      request.setAttribute("linkCount", linkCount);

      request
          .getRequestDispatcher("/WEB-INF/views/setup/syncs/sync-detail.jsp")
          .forward(request, response);
    }
  }
}
