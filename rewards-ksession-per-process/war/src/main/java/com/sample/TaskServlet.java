package com.sample;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.task.query.TaskSummary;

public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private TaskLocal taskService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cmd = req.getParameter("cmd");
        String user = req.getParameter("user");

        if (cmd.equals("list")) {
            List<TaskSummary> taskList;
            try {
                taskList = taskService.retrieveTaskList(user);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            req.setAttribute("taskList", taskList);
            ServletContext context = this.getServletContext();
            RequestDispatcher dispatcher = context.getRequestDispatcher("/task.jsp");
            dispatcher.forward(req, res);
            return;
        } else if (cmd.equals("approve")) {
            String message = "";
            long taskId = Long.parseLong(req.getParameter("taskId"));
            long processInstanceId = Long.parseLong(req.getParameter("processInstanceId"));
            try {
                taskService.approveTask(processInstanceId, user, taskId);
                message = "Task (id = " + taskId + ") has been completed by " + user;
            } catch (ProcessOperationException e) {
                // Recoverable exception
                message = "Task operation failed. Please retry : " + e.getMessage();
            } catch (Exception e) {
                // Unexpected exception
                throw new ServletException(e);
            }
            req.setAttribute("message", message);
            ServletContext context = this.getServletContext();
            RequestDispatcher dispatcher = context.getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, res);
            return;
        }

    }
}