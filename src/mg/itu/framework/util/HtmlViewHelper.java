package mg.itu.framework.util;

import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;
import java.io.PrintWriter;
import java.util.Map;

public class HtmlViewHelper {

    public static void afficherTableauRoutes(PrintWriter out, Map<UrlKey, Mapping> urlMapping) {
        if (urlMapping == null || urlMapping.isEmpty()) {
            out.println("<p style='color:orange;'>Aucune route enregistrée.</p>");
        } else {
            out.println("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
            out.println("<tr style='background-color: #f2f2f2;'><th>URL</th><th>Méthode HTTP</th><th>Classe Contrôleur</th><th>Fonction / Méthode</th></tr>");
            for (UrlKey key : urlMapping.keySet()) {
                Mapping m = urlMapping.get(key);
                out.println("<tr>");

                out.println("<td><strong>" + key.getUrl() + "</strong></td>");
                out.println("<td><span style='color:blue; font-weight:bold;'>" + key.getMethod() + "</span></td>");
                out.println("<td>" + m.getClassName() + "</td>");
                out.println("<td><span style='color:#e67e22;'>" + m.getMethod() + "()</span></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }
}