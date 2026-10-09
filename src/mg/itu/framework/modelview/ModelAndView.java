package mg.itu.framework.modelview;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> data;

    public ModelAndView(){
        this.data = new HashMap<>();
    }

    public ModelAndView(String view){
        this.view = view;
        this.data = new HashMap<>();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setAttribut(String key, Object value) {
        this.data.put(key, value);
    }

    public void addObject(String nom, Object valeur){
        data.put(nom, valeur);
    }
}