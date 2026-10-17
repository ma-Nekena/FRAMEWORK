package mg.itu.framework.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mg.itu.framework.annotation.controller.Json;
import mg.itu.framework.modelview.ModelAndView;
import mg.itu.framework.util.HtmlViewHelper;
import mg.itu.framework.util.Mapping;
import mg.itu.framework.util.UrlKey;
import mg.itu.framework.util.FileData;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private List<String> listeController = new ArrayList<>();
    private Map<UrlKey, Mapping> urlMapping = new HashMap<>();
    
    private String viewPrefix;
    private String viewSuffix;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        super.init();
        
        ServletContext context = getServletContext();
        
        this.urlMapping = (Map<UrlKey, Mapping>) context.getAttribute("urlMapping");
        this.listeController = (List<String>) context.getAttribute("listeController");
        
        if (this.urlMapping == null) this.urlMapping = new HashMap<>();
        if (this.listeController == null) this.listeController = new ArrayList<>();

        this.viewPrefix = (String) context.getAttribute("viewPrefix");
        this.viewSuffix = (String) context.getAttribute("viewSuffix");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext context = getServletContext();
        String initError = (String) context.getAttribute("INIT_ERROR");

        if (initError != null) {
            response.sendError(505, "Framework Init Error: " + initError);
            return; 
        }
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String pathInfo = requestURI.substring(contextPath.length());

        String realPath = getServletContext().getRealPath(pathInfo);
        if (realPath != null) {
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                getServletContext().getNamedDispatcher("default").forward(request, response);
                return; 
            }
        }

        String lookupPath = pathInfo; 
        String currentMethod = request.getMethod();
        UrlKey currentKey = new UrlKey(lookupPath, currentMethod);

        if (urlMapping.containsKey(currentKey)) { 
            Mapping target = urlMapping.get(currentKey);

            try {
                Class<?> clazz = Class.forName(target.getClassName());
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                
                Method targetMethod = null;
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().equals(target.getMethod())) {
                        targetMethod = m;
                        break;
                    }
                }

                if (targetMethod == null) {
                    throw new NoSuchMethodException("Méthode " + target.getMethod() + " introuvable dans " + target.getClassName());
                }

                                                
                Parameter[] parameters = targetMethod.getParameters();
                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {

                    Parameter param = parameters[i];
                    Class<?> paramType = param.getType();
                    String paramName = param.getName();

                    if (isSimpleType(paramType)) {
                        String paramValue = request.getParameter(paramName);
                        args[i] = castSimpleType(paramValue, paramType);

                    } else {
                        try {
                            args[i] = populateObject(
                                paramType,
                                request,
                                ""
                            );

                        } catch (Exception e) {

                            throw new ServletException(
                                "Erreur lors du binding de l'objet "
                                + paramType.getName(),
                                e
                            );
                        }
                    }
                }

                Object result = targetMethod.invoke(controllerInstance, args);


                if (targetMethod.isAnnotationPresent(Json.class)) {
                    response.setContentType("application/json;charset=UTF-8");

                    Json JsonAnnotation = targetMethod.getAnnotation(Json.class);

                    try (PrintWriter out = response.getWriter()) {
                        if (result == null) {
                            out.print("{}");
                        } else if (JsonAnnotation.isJson()) {
                            out.print((String) result);
                        } else {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            out.print(gson.toJson(result));
                        }
                        out.flush();
                    }
                    return; 
                }

                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;
                    
                    Map<String, Object> data = mv.getData();
                    if (data != null) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }

                    String fullViewPath = this.viewPrefix + mv.getView() + this.viewSuffix;

                    RequestDispatcher dispatcher = request.getRequestDispatcher(fullViewPath);
                    dispatcher.forward(request, response);
                    return; 
                }

                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<h2 style='color:green;'>✔ Méthode exécutée avec succès mais sans vue associée.</h2>");
                    out.println("<p>Objet retourné : " + result + "</p>");
                }

            } catch (Exception e) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<h2 style='color:red;'>❌ Erreur lors de l'exécution de la méthode</h2>");
                    out.println("<pre>");
                    if (e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null) {
                        e.getCause().printStackTrace(out);
                    } else {
                        e.printStackTrace(out);
                    }
                    out.println("</pre>");
                }
            }
        } else {

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                if (pathInfo.equals("/") || pathInfo.isEmpty()) {
                    pathInfo = "";
                } else {
                    pathInfo = pathInfo.substring(1);
                }
                out.println("<h3> URL demandee: " + pathInfo + "[" + currentMethod + "]</h3>");
                out.println("<h2> l'url [" + currentMethod + "] n'est pas supportee pour " + lookupPath + " </h2>");
                out.println("<p> Voici les url validées : </p>");
                HtmlViewHelper.afficherTableauRoutes(out, urlMapping);
            }
        }
    }

    private boolean isSimpleType(Class<?> type) {

        return type == String.class
                || type == int.class
                || type == Integer.class
                || type == double.class
                || type == Double.class
                || type == boolean.class
                || type == Boolean.class
                || type == float.class
                || type == Float.class
                || type == long.class
                || type == Long.class;
    }

    private Object castSimpleType(String value, Class<?> type) {
        if (value == null || value.trim().isEmpty()) {
            if (type == boolean.class) {
                return false;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == double.class) {
                return 0.0;
            }
            if (type == float.class) {
                return 0.0f;
            }
            if (type == long.class) {
                return 0L;
            }
            return null;
        }
        if (type == String.class) {
            return value;
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }

        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }

        throw new IllegalArgumentException(
            "Type non pris en charge : " + type.getName()
        );
    }

    private Object populateObject(Class<?> clazz, HttpServletRequest request, String prefix) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();

            String paramKey = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;

            if (isSimpleType(fieldType)) {
                String requestValue = request.getParameter(paramKey);
                if (requestValue != null && !requestValue.trim().isEmpty()) {
                    Object convertedValue = castSimpleType(requestValue, fieldType);
                    field.set(instance, convertedValue);
                }
            } 

            else if (fieldType == FileData.class) {
                try {
                    Part part = request.getPart(paramKey);
                    if (part != null && part.getSize() > 0) {

                        String fileName = part.getSubmittedFileName();
                        
                        String extension = "";
                        if (fileName != null && fileName.contains(".")) {
                            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                        }

                        InputStream is = part.getInputStream();
                        byte[] bytes = is.readAllBytes();

                        FileData fileData = new FileData(fileName, extension, bytes);

                        String uploadFolder = "/Users/rotsy/Documents/S5/M.NAINA/TEST-FRAMEWORK/uploads/";
                        fileData.writeTo(uploadFolder); 

                        field.set(instance, fileData);
                    }
                } catch (Exception e) {

                }
            } 
            else {
                Object childObject = populateObject(fieldType, request, paramKey);
                field.set(instance, childObject);
            }
        }

        return instance;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}