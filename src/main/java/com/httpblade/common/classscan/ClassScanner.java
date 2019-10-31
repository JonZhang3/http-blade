package com.httpblade.common.classscan;

import com.httpblade.common.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    private static final String DOT = ".";
    private static final String CLASS_FILE_SUFFIX = ".class";

    private List<String> packageNames = new LinkedList<>();
    private Filter filter = new SampleClassFilter();
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private ClassScanner() {
    }

    public static ClassScanner newScanner() {
        return new ClassScanner();
    }

    public static ClassScanner newScanner(String... packageNames) {
        return new ClassScanner().packageName(packageNames);
    }

    private ClassScanner packageName(String... packageNames) {
        if (packageNames != null && packageNames.length > 0) {
            for (String packageName : packageNames) {
                if (Utils.isNotEmpty(packageName)) {
                    if (!packageName.endsWith(DOT)) {
                        packageName += DOT;
                    }
                    if (!this.packageNames.contains(packageName)) {
                        this.packageNames.add(packageName);
                    }
                }
            }
        }
        return this;
    }

    public ClassScanner setFilter(Filter filter) {
        if (filter != null) {
            this.filter = filter;
        }
        return this;
    }

    public List<Class> scan() throws IOException {
        final List<Class> classes = new LinkedList<>();
        for (String packageName : packageNames) {
            String packagePath = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                fillClasses(packageName, resources.nextElement(), classes);
            }
        }
        return classes;
    }

    private void fillClasses(String packageName, URL url, List<Class> classes) {
        String classPath = decodeClassPath(url.getPath());
        if (!classPath.equals("")) {
            File file = new File(classPath);
            if (url.getProtocol().equals("file")) {
                processFile(packageName, file, classes);
            } else if (url.getProtocol().equals("jar")) {
                processJarFile(packageName, file, classes);
            }
        }
    }

    private void processFile(String packageName, File file, final List<Class> classes) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if(packageName.endsWith(DOT)) {
                        packageName = packageName.substring(0, packageName.length() - 1);
                    }
                    processFile(packageName + DOT + child.getName(), child, classes);
                }
            }
        } else if (file.isFile() && isClass(file.getName())) {
            String className = packageName.replace(CLASS_FILE_SUFFIX, "");
            fillClass(className, classes);
        }
    }

    private void processJarFile(String packageName, File file, final List<Class> classes) {
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            JarEntry entry;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (isClass(entry.getName())) {
                    final String className = entry.getName().replace("/", ".")
                        .replace(CLASS_FILE_SUFFIX, "");
                    if (className.startsWith(packageName)) {
                        fillClass(className, classes);
                    }
                }
            }
        } catch (Exception ignore) {
        }
    }

    private void fillClass(String className, final List<Class> classes) {
        try {
            final Class<?> clazz = Class.forName(className, false, classLoader);
            if (filter.accept(clazz)) {
                classes.add(clazz);
            }
        } catch (Exception ignore) {
        }
    }

    private boolean isClass(String name) {
        return name.endsWith(CLASS_FILE_SUFFIX);
    }

    private String decodeClassPath(String classPath) {
        try {
            return URLDecoder.decode(classPath, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static class SampleClassFilter implements Filter {
        @Override
        public boolean accept(Class<?> aClass) {
            return true;
        }
    }

}
