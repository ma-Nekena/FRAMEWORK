package mg.itu.framework.util;

import mg.itu.framework.annotation.controller.Controller;
import mg.itu.framework.annotation.controller.UrlMapping;
import jakarta.servlet.ServletException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class FinderAnnotation {

    public static void chargerRoutes(String packageToScan, String basePath, List<String> listeController, Map<UrlKey, Mapping> urlMapping) throws Exception {
        List<Class<?>> allPrunedClasses;

        // Choix du mode de scan selon la configuration du web.xml
        if (packageToScan != null && !packageToScan.trim().isEmpty()) {
            String packagePath = packageToScan.replace('.', '/');
            File rootDir = new File(basePath + "/" + packagePath);
            allPrunedClasses = PackageScanner.scan(rootDir, packageToScan);
        } else {
            File rootDir = new File(basePath);
            allPrunedClasses = PackageScanner.scan(rootDir, "");
        }

        // Analyse des classes et méthodes
        for (Class<?> cls : allPrunedClasses) {
            if (cls.isAnnotationPresent(Controller.class)) {
                String simpleName = cls.getSimpleName();
                if (!listeController.contains(simpleName)) {
                    listeController.add(simpleName);
                }

                for (Method method : cls.getDeclaredMethods()) {
                    UrlKey key = null;

                    // Détection de l'annotation @UrlMapping
                    if (method.isAnnotationPresent(UrlMapping.class)) {
                        UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                        key = new UrlKey(annotation); // Utilisation du constructeur intelligent
                    }

                    // C'est ici que tu ajouteras la détection de @GetMapping ou @PostMapping plus tard

                    // Si une route a été trouvée
                    if (key != null) {
                        Mapping mapping = new Mapping(cls.getName(), method.getName());

                        // Gestion stricte des doublons au démarrage
                        if (urlMapping.containsKey(key)) {
                            Mapping conflit = urlMapping.get(key);
                            throw new ServletException(
                                " [ERREUR DUBLON] L'URL '" + key.getUrl() + "' avec la méthode [" + key.getMethod() + "] " +
                                "est déjà déclarée dans la classe " + conflit.getClassName() + 
                                " (méthode: " + conflit.getMethod() + "()). " +
                                "Impossible de la redéclarer dans " + cls.getName() + "." + method.getName() + "() !"
                            );
                        }
                        urlMapping.put(key, mapping);
                    }
                }
            }
        }
    }
}