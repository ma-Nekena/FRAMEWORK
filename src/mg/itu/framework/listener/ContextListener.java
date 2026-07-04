package mg.itu.framework.listener;

import mg.itu.framework.util.FinderAnnotation;
import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        List<String> listeController = new ArrayList<>();
        Map<UrlKey, Mapping> urlMapping = new HashMap<>();

        try {
            String packageToScan = context.getInitParameter("packageScan");
            String basePath = context.getRealPath("/WEB-INF/classes");

            if (basePath == null) {
                throw new IllegalStateException("Impossible d'accéder au chemin des classes (basePath est nul).");
            }

            // Appel de notre utilitaire de scan
            FinderAnnotation.chargerRoutes(packageToScan, basePath, listeController, urlMapping);
            
            // Sauvegarde dans le ServletContext global de Tomcat
            context.setAttribute("urlMapping", urlMapping);
            context.setAttribute("listeController", listeController);

        } catch (Exception e) {
            context.setAttribute("INIT_ERROR", e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}