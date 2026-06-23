package mg.itu.framework.util;

public class Mapping{
    private String className;
    private String method;

    public Mapping(String className, String method){
        this.className = className;
        this.method = method;
    }

    public String getClassName(){
        return className;
    }
    public void setClassName(){
        this.className = className;
    }

    public String getMethod(){
        return method;
    }
    public void setMethod(){
        this.method = method;
    }
}