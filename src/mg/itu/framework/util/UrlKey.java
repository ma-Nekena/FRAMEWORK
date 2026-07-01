package mg.itu.framework.util;

import java.util.Objects;

public class UrlKey {
    private String url;
    private String method;

    public UrlKey(String url, String method){
        this.url = url;
        this.method = method !=null ? method.toUpperCase() : "GET";
    }

    public String getUrl(){
        return url;
    }
    public String getMethod(){
        return method;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlKey urlKey = (UrlKey) o;

        return Objects.equals(url, urlKey.url) && Objects.equals(method, urlKey.method);
    }

    @Override
    public int hashCode(){
        return Objects.hash(url, method);
    }
}