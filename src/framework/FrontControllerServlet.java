package framework;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet{

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
            response.setContentType("text/html");

            try (PrintWriter out = response.getWriter()){
                String requestURI = request.getRequestURI();

                String contextPath = request.getContextPath();

                String pathInfo = requestURI.substring(contextPath.length());

                if (pathInfo.equals("/") || pathInfo.isEmpty()){
                    pathInfo = "";
                }else{
                    pathInfo = pathInfo.substring(1);
                }

                out.println("<h3>" + pathInfo + "</h3>");
            }
        }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
                processRequest(request, response);
            }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
                processRequest(request, response);
            }
}