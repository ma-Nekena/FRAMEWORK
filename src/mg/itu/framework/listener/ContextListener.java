package mg.itu.framework.listener;

import mg.itu.framework.database.Database;
import mg.itu.framework.database.DatabaseConfig;
import mg.itu.framework.util.FinderAnnotation;
import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext context = sce.getServletContext();

        // 1. Configuration des vues
        String prefix = context.getInitParameter("prefix");
        String suffix = context.getInitParameter("suffix");

        if (prefix == null) prefix = "/WEB-INF/views/";
        if (suffix == null) suffix = ".jsp";

        context.setAttribute("viewPrefix", prefix);
        context.setAttribute("viewSuffix", suffix);

        // 2. Initialisation des contrôleurs et des routes
        List<String> listeController = new ArrayList<>();
        Map<UrlKey, Mapping> urlMapping = new HashMap<>();

        try {
            String packageToScan = context.getInitParameter("packageScan");
            String basePath = context.getRealPath("/WEB-INF/classes");

            if (basePath == null) {
                throw new IllegalStateException(
                    "Impossible d'accéder au chemin des classes."
                );
            }

            FinderAnnotation.chargerRoutes(
                packageToScan,
                basePath,
                listeController,
                urlMapping
            );

            context.setAttribute("urlMapping", urlMapping);
            context.setAttribute("listeController", listeController);

        } catch (Exception e) {
            context.setAttribute("INIT_ERROR", e.getMessage());
            context.log("Erreur d'initialisation des routes.", e);
        }

        // 3. Initialisation de PostgreSQL
        try (InputStream input = context.getResourceAsStream(
                "/WEB-INF/database.properties")) {

            if (input == null) {
                context.log(
                    "Aucun fichier database.properties trouvé. "
                    + "La base de données n'est pas configurée."
                );
                return;
            }

            DatabaseConfig config = DatabaseConfig.fromInputStream(input);

            Database.configure(config);

            context.log("Configuration PostgreSQL chargée avec succès.");

        } catch (Exception e) {

            context.setAttribute("DATABASE_INIT_ERROR", e.getMessage());

            context.log("Erreur d'initialisation de PostgreSQL.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}