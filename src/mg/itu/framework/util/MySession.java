package mg.itu.framework.util;

import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MySession {
    private Map<String, Object> sessionData = new HashMap();

    public MySession(){}

    public Object get(String key){
        return sessionData.get(key);
    }

    public void add(String key, Object value){
        sessionData.put(key,value);
    }

    public void delete(String key){
        sessionData.remove(key);
    }

    public void populateFromHttpSession(HttpSession httpSession) {
        if (httpSession != null) {
            Enumeration<String> attributeNames = httpSession.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                sessionData.put(name, httpSession.getAttribute(name));
            }
        }
    }    
    public void syncToHttpSession(HttpSession httpSession) {
        if (httpSession != null) {
            for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
                httpSession.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    public Map<String, Object> getSessionData() {
        return sessionData;
    }
}