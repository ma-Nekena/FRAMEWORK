package mg.itu.framework.controller;

import mg.itu.framework.annotation.controller.Controller;
import mg.itu.framework.util.PackageScanner;
import mg.itu.framework.annotation.controller.UrlMapping;
import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;
import mg.itu.framework.util.HtmlViewHelper; 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletContext;

public class FrontControllerServlet extends HttpServlet {

    private List<String> listeController = new ArrayList<>();
    private Map<UrlKey, Mapping> urlMapping = new HashMap<>();

@SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        super.init();
        
        ServletContext context = getServletContext();
        
        this.urlMapping = (Map<UrlKey, Mapping>) context.getAttribute("urlMapping");
        this.listeController = (List<String>) context.getAttribute("listeController");
        
        if (this.urlMapping == null) this.urlMapping = new HashMap<>();
        if (this.listeController == null) this.listeController = new ArrayList<>();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            String lookupPath = pathInfo; 
            String currentMethod = request.getMethod();
            UrlKey currentKey = new UrlKey(lookupPath, currentMethod);

            if (pathInfo.equals("/") || pathInfo.isEmpty()) {
                pathInfo = "";
            } else {
                pathInfo = pathInfo.substring(1);
            }

            out.println("<h3> URL demandee: " + pathInfo + "[" + currentMethod + "]</h3>");

            out.println("<h2>Liste des Contrôleurs détectés au démarrage :</h2>");
            if (listeController.isEmpty()) {
                out.println("<p style='color:red;'>Aucun contrôleur trouvé.</p>");
            } else {
                out.println("<ul>");
                for (String ctrl : listeController) {
                    out.println("<li>" + ctrl + "</li>");
                }
                out.println("</ul>");
            }

            if (lookupPath.equals("/") || lookupPath.isEmpty()) {
                out.println("<h3>Tableau de toutes les routes de l'application :</h3>");
                HtmlViewHelper.afficherTableauRoutes(out, urlMapping);

            } else if (urlMapping.containsKey(currentKey)) { 
                Mapping target = urlMapping.get(currentKey);

                try {
                    Class<?> clazz = Class.forName(target.getClassName());
                    Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                    java.lang.reflect.Method method = clazz.getDeclaredMethod(target.getMethod());

                    method.invoke(controllerInstance);

                    out.println("<h2 style='color:green;'>✔ URL et méthode HTTP supportées !</h2>");
                    out.println("<p><strong>URL :</strong> " + lookupPath + "</p>");
                    out.println("<p><strong>Méthode HTTP :</strong> " + currentMethod + "</p>");
                    out.println("<p><strong>Classe exécutée :</strong> " + target.getClassName() + "</p>");
                    out.println("<p><strong>Fonction invoquée :</strong> " + target.getMethod() + "()</p>");
                } catch (Exception e) {
                    out.println("<h2 style='color:red;'>❌ Erreur lors de l'exécution de la méthode</h2>");
                    out.println("<pre>");

                    if (e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null) {
                        out.println("<strong>Cause réelle :</strong> " + e.getCause());
                        e.getCause().printStackTrace(out);
                    } else {
                        e.printStackTrace(out);
                    }
                    out.println("</pre>");
                }

            } else {
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