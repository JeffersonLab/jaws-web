package org.jlab.jaws.presentation.controller;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.persistence.model.JAWSModel;

@WebServlet(
    name = "Debug",
    urlPatterns = {"/debug/*"})
public class Debug extends HttpServlet {

  @Inject private JAWSModel model;

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

    request.setAttribute("model", model);

    request.getRequestDispatcher("/WEB-INF/views/debug.jsp").forward(request, response);
  }
}
