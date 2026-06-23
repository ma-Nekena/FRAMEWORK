package mg.itu.framework.util;

import mg.itu.framework.util.Mapping;
import java.io.PrintWriter;
import java.util.HashMap;

public class HtmlViewHelper {

    public static void afficherTableauRoutes(PrintWriter out, HashMap<String, Mapping> urlMapping) {
        if (urlMapping == null || urlMapping.isEmpty()) {
            out.println("<p style='color:orange;'>Aucune route enregistrée. Utilisez @UrlMapping(\"/votre-chemin\") sur vos méthodes.</p>");
        } else {
            out.println("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
            out.println("<tr style='background-color: #f2f2f2;'><th>URL</th><th>Classe Contrôleur</th><th>Fonction / Méthode</th></tr>");
            for (String url : urlMapping.keySet()) {
                Mapping m = urlMapping.get(url);
                out.println("<tr>");
                out.println("<td><strong>" + url + "</strong></td>");
                out.println("<td>" + m.getClassName() + "</td>");
                out.println("<td><span style='color:#e67e22;'>" + m.getMethod() + "()</span></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }
}