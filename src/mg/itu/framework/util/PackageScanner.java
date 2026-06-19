package mg.itu.framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageScanner {

    public static List<Class<?>> scan(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return classes;

        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackageName = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                classes.addAll(scan(file, subPackageName));
            } else if (file.getName().endsWith(".class")) {
                String pureClassName = file.getName().substring(0, file.getName().length() - 6);
                String className = packageName.isEmpty() ? pureClassName : packageName + "." + pureClassName;
                
                try {
                    Class<?> cls = Class.forName(className);
                    classes.add(cls);
                } catch (Throwable t) {
                }
            }
        }
        return classes;
    }
}