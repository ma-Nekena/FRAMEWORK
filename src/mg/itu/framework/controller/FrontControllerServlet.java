package mg.itu.framework.controller;

import mg.itu.framework.annotation.controller.Json;
import mg.itu.framework.modelview.ModelAndView;
import mg.itu.framework.util.HtmlViewHelper;
import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontControllerServlet extends HttpServlet {

    private List<String> listeController = new ArrayList<>();
    private Map<UrlKey, Mapping> urlMapping = new HashMap<>();
    
    private String viewPrefix;
    private String viewSuffix;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        super.init();
        
        ServletContext context = getServletContext();
        
        this.urlMapping = (Map<UrlKey, Mapping>) context.getAttribute("urlMapping");
        this.listeController = (List<String>) context.getAttribute("listeController");
        
        if (this.urlMapping == null) this.urlMapping = new HashMap<>();
        if (this.listeController == null) this.listeController = new ArrayList<>();

        this.viewPrefix = (String) context.getAttribute("viewPrefix");
        this.viewSuffix = (String) context.getAttribute("viewSuffix");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext context = getServletContext();
        String initError = (String) context.getAttribute("INIT_ERROR");

        if (initError != null) {
            response.sendError(505, "Framework Init Error: " + initError);
            return; 
        }
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String pathInfo = requestURI.substring(contextPath.length());

        String realPath = getServletContext().getRealPath(pathInfo);
        if (realPath != null) {
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                getServletContext().getNamedDispatcher("default").forward(request, response);
                return; 
            }
        }

        String lookupPath = pathInfo; 
        String currentMethod = request.getMethod();
        UrlKey currentKey = new UrlKey(lookupPath, currentMethod);

        if (urlMapping.containsKey(currentKey)) { 
            Mapping target = urlMapping.get(currentKey);

            try {
                Class<?> clazz = Class.forName(target.getClassName());
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                
                Method targetMethod = null;
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().equals(target.getMethod())) {
                        targetMethod = m;
                        break;
                    }
                }

                if (targetMethod == null) {
                    throw new NoSuchMethodException("Méthode " + target.getMethod() + " introuvable dans " + target.getClassName());
                }

                Object result = targetMethod.invoke(controllerInstance);

                }

                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;
                    
                    Map<String, Object> data = mv.getData();
                    if (data != null) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }

                    String fullViewPath = this.viewPrefix + mv.getView() + this.viewSuffix;

                    RequestDispatcher dispatcher = request.getRequestDispatcher(fullViewPath);
                    dispatcher.forward(request, response);
                    return; 
                }

                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<h2 style='color:green;'>✔ Méthode exécutée avec succès mais sans vue associée.</h2>");
                    out.println("<p>Objet retourné : " + result + "</p>");
                }

            } catch (Exception e) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<h2 style='color:red;'>❌ Erreur lors de l'exécution de la méthode</h2>");
                    out.println("<pre>");
                    if (e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null) {
                        e.getCause().printStackTrace(out);
                    } else {
                        e.printStackTrace(out);
                    }
                    out.println("</pre>");
                }
            }
        } else {

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                if (pathInfo.equals("/") || pathInfo.isEmpty()) {
                    pathInfo = "";
                } else {
                    pathInfo = pathInfo.substring(1);
                }
                out.println("<h3> URL demandee: " + pathInfo + "[" + currentMethod + "]</h3>");
                out.println("<h2> l'url [" + currentMethod + "] n'est pas supportee pour " + lookupPath + " </h2>");
                out.println("<p> Voici les url validées : </p>");
                HtmlViewHelper.afficherTableauRoutes(out, urlMapping);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}