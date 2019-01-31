package edu.utec.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https://stackoverflow.com/questions/2548384/java-get-a-list-of-all-classes-loaded-in-the-jvm
 * http://icedtea.classpath.org/hg/icedtea-web/file/0527ad4eb2dd/netx/net/sourceforge/jnlp/controlpanel/ClassFinder.java
 * */
public class PluginUtil {

  private static final Logger logger = LoggerFactory.getLogger(PluginUtil.class);

  public static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
  public static final String CUSTOM_CLASS_PATH_PROPERTY = "custom.class.path";
  public static final String BOOT_CLASS_PATH_PROPERTY = "sun.boot.class.path";

  public static String getDefaultImplClassNameFromSeleniumCommandName(String seleniumCommandName) {
    return seleniumCommandName.substring(0, 1).toUpperCase() + seleniumCommandName.substring(1);
  }

  public static void loadCommandPlugins() throws NoSuchFieldException, SecurityException,
          IllegalArgumentException, IllegalAccessException {
    ClassLoader myCL = Thread.currentThread().getContextClassLoader();
    while (myCL != null) {
      System.out.println("ClassLoader: " + myCL);
      for (Iterator<?> iter = list(myCL); iter.hasNext();) {

        Class<?> clazz = (Class<?>) iter.next();

        System.out.println(clazz.toString());

      }
      myCL = myCL.getParent();
    }

  }

  public static Iterator<?> list(ClassLoader CL) throws NoSuchFieldException, SecurityException,
          IllegalArgumentException, IllegalAccessException {
    Class<?> CL_class = CL.getClass();
    while (CL_class != java.lang.ClassLoader.class) {
      CL_class = CL_class.getSuperclass();
    }
    java.lang.reflect.Field ClassLoader_classes_field = CL_class.getDeclaredField("classes");
    ClassLoader_classes_field.setAccessible(true);
    Vector<?> classes = (Vector<?>) ClassLoader_classes_field.get(CL);
    return classes.iterator();
  }

  public static Map<String, Class<?>> walkClassPath(Class<?> toFind) {
    Map<String, Class<?>> results = new HashMap<String, Class<?>>();
    Set<String> classPathRoots = getClassPathRoots();
    for (String classpathEntry : classPathRoots) {
      // need to avoid base jdk jars/modules
      if (!classpathEntry.toLowerCase().contains("jre/lib")) {
        File f = new File(classpathEntry);
        if (!f.exists()) {
          continue;
        }
        if (f.isDirectory()) {
          traverse(f.getAbsolutePath(), f, toFind, results);
        } else {
          File jar = new File(classpathEntry);
          try {
            JarInputStream is = new JarInputStream(new FileInputStream(jar));
            JarEntry entry;
            while ((entry = is.getNextJarEntry()) != null) {
              Class<?> c = determine(entry.getName(), toFind);
              if (c != null) {
                results.put(c.getCanonicalName(), c);
              }
            }

            is.close();

          } catch (IOException ex) {
            if (isDebugEnabled()) {
              logger.error("Failed to load plugins :", ex);
            }
          }
        }
      }
    }
    return results;
  }

  public static Set<String> getClassPathRoots() {
    String classapth1 = System.getProperty(CUSTOM_CLASS_PATH_PROPERTY);
    String classapth2 = System.getProperty(JAVA_CLASS_PATH_PROPERTY);
    String classapth3 = System.getProperty(BOOT_CLASS_PATH_PROPERTY);
    String classpath = "";
    if (classapth1 != null) {
      classpath = classpath + classapth1 + File.pathSeparator;
    }
    if (classapth2 != null) {
      classpath = classpath + classapth2 + File.pathSeparator;
    }
    if (classapth3 != null) {
      classpath = classpath + classapth3 + File.pathSeparator;
    }
    String[] pathElements = classpath.split(File.pathSeparator);
    Set<String> s = new HashSet<>(Arrays.asList(pathElements));
    return s;
  }

  public static void traverse(String root, File current, Class<?> toFind,
          Map<String, Class<?>> results) {
    File[] fs = current.listFiles();
    for (File f : fs) {
      if (f.isDirectory()) {
        traverse(root, f, toFind, results);
      } else {
        String ff = f.getAbsolutePath();
        String name = ff.substring(root.length());
        while (name.startsWith(File.separator)) {
          name = name.substring(1);
        }
        Class<?> c = determine(name, toFind);
        if (c != null) {
          results.put(c.getCanonicalName(), c);
        }
      }

    }
  }

  public static Class<?> determine(String name, Class<?> toFind) {
    if (name.contains("$")) {
      return null;
    }
    try {
      if (name.endsWith(".class")) {
        name = name.replace(".class", "");
        name = name.replace("/", ".");
        name = name.replace("\\", ".");
        Class<?> clazz = Class.forName(name);
        if (toFind.isAssignableFrom(clazz)) {
          return clazz;
        }
      }
    } catch (Throwable ex) {
      if (isDebugEnabled()) {
        logger.error("blacklisted class:", ex);
      }
    }
    return null;
  }

  public static <T> Map<String, Class<?>> scanClassPathInheritOf(Class<T> toFind) {
    Map<String, Class<?>> foundClasses = (Map<String, Class<?>>) walkClassPath(toFind);
    return foundClasses;
  }

  private static boolean isDebugEnabled() {
    String debugEnabled = System.getenv("katalon.engine");

    if (debugEnabled == null || debugEnabled.equals("")) {
      return false;
    }

    try {
      return Boolean.parseBoolean(debugEnabled);
    } catch (Exception e) {
      if (isDebugEnabled()) {
        logger.error("Failed to load plugins :", e);
      }
      return false;
    }

  }

}
