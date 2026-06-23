package mg.itu.framework.controller;

import mg.itu.framework.annotation.controller.Controller;
import mg.itu.framework.util.PackageScanner;
import mg.itu.framework.annotation.controller.UrlMapping;
import mg.itu.framework.util.Mapping;
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

public class FrontControllerServlet extends HttpServlet {

    private List<String> listeController = new ArrayList<>();
    private HashMap<String, Mapping> urlMapping = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        urlMapping.clear();
        listeController.clear();
        try {
            String packageToScan = getInitParameter("packageScan");
            String basePath = getServletContext().getRealPath("/WEB-INF/classes");

            if (basePath != null && packageToScan != null && !packageToScan.trim().isEmpty()) {
                String packagePath = packageToScan.replace('.', '/');
                File rootDir = new File(basePath + "/" + packagePath);
                
                List<Class<?>> allPrunedClasses = PackageScanner.scan(rootDir, packageToScan);

                for (Class<?> cls : allPrunedClasses) {
                    if (cls.isAnnotationPresent(Controller.class)) {
                        String simpleName = cls.getSimpleName();
                        if (!listeController.contains(simpleName)) {
                            listeController.add(simpleName);
                        }

                        java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
                        for (java.lang.reflect.Method method : methods) {
                            if (method.isAnnotationPresent(UrlMapping.class)) {
                                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                                String url = annotation.value();

                                Mapping mapping = new Mapping(cls.getName(), method.getName());
                                urlMapping.put(url, mapping);
                            }
                        }
                    }
                }
            } else if (basePath != null) {
                File rootDir = new File(basePath);
                List<Class<?>> allPrunedClasses = PackageScanner.scan(rootDir, "");
                
                for (Class<?> cls : allPrunedClasses) {
                    if (cls.isAnnotationPresent(Controller.class)) {
                        String simpleName = cls.getSimpleName();
                        if (!listeController.contains(simpleName)) {
                            listeController.add(simpleName);
                        }

                        java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
                        for (java.lang.reflect.Method method : methods) {
                            if (method.isAnnotationPresent(UrlMapping.class)) {
                                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                                String url = annotation.value();

                                Mapping mapping = new Mapping(cls.getName(), method.getName());
                                urlMapping.put(url, mapping);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            if (pathInfo.equals("/") || pathInfo.isEmpty()) {
                pathInfo = "";
            } else {
                pathInfo = pathInfo.substring(1);
            }

            out.println("<h3>" + pathInfo + "</h3>");

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
                out.println(" ");

            } else if (urlMapping.containsKey(lookupPath)) {
                Mapping target = urlMapping.get(lookupPath);

                out.println("<h2> url supporté !</h2>");
                out.println("<p> url : " + lookupPath + "</p>");
                out.println("<p> class : " + target.getClassName() + "</p>");
                out.println("<p> methode : " + target.getMethod() + "</p>");

            } else {
                out.println("<h2> l'url n'est pas supporté : " + lookupPath + "</h2>");
                out.println("<p> Voici les url validés : </p>");
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