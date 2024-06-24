package mg.itu.prom16.Util;

import java.util.HashMap;

public class ModelView 
{
    String url;
    HashMap<String, Object> data;

    public ModelView(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public ModelView(String url, HashMap<String, Object> data) {
        this.url = url;
        this.data = data;
    }

    public ModelView() {
        this.data = new HashMap<>();
    }

    public void addObject(String nom,Object valeur)
    {
        data.put(nom, valeur);
    }
}
