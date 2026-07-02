package mg.itu.framework.util;

import mg.itu.framework.annotation.controller.UrlMapping;
import mg.itu.framework.annotation.controller.GetMapping;
import mg.itu.framework.annotation.controller.PostMapping;

import java.util.Objects;

public class UrlKey {
    private String url;
    private String method;

    public UrlKey(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public UrlKey(UrlMapping annotation) {
        this.url = annotation.value();
        this.method = annotation.method();
    }

    public UrlKey(GetMapping annotation) {
        this.url = annotation.value();
        this.method = "GET";
    }

    public UrlKey(PostMapping annotation) {
        this.url = annotation.value();
        this.method = "POST";
    }


    public String getUrl() { return url; }
    public String getMethod() { return method; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlKey)) return false;
        UrlKey urlKey = (UrlKey) o;
        return Objects.equals(url, urlKey.url) && Objects.equals(method, urlKey.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return "[" + url + " , " + method + "]";
    }
}