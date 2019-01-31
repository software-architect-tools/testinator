package edu.utec.automation.testinator.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Configurations {

  private static final Logger logger = LoggerFactory.getLogger(Configurations.class);

  private static Properties properties;

  public static void initFromExternalFile(String configurationsAbsoluteFilePath) throws Exception {

    logger.info("Reading configurations from external file:" + configurationsAbsoluteFilePath);

    File configurationsFile = new File(configurationsAbsoluteFilePath);
    if (!configurationsFile.exists()) {
      throw new FileNotFoundException(String.format("External configurations was not found :%s",
              configurationsAbsoluteFilePath));
    }

    FileInputStream inputStream = new FileInputStream(configurationsAbsoluteFilePath);
    init(inputStream);
  }

  public static void initFromRootClasspathFile(String fileName) throws Exception {

    logger.info("Reading configurations from default classpath file:" + fileName);

    InputStream inputStream = Configurations.class.getClassLoader().getResourceAsStream(fileName);
    init(inputStream);
  }

//  public static void initFromCustomFile(String fileName) throws Exception {
//
//    initFromRootClasspathFile(fileName);
//  }
//  
//  public static void initFromDefaultFile() throws Exception {
//
//    initFromRootClasspathFile("default.properties");
//  }  

  private static void init(InputStream inputStream) throws Exception {

    if (properties == null) {
      properties = new Properties();
    }

    try {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new Exception("Failed to load configurations file.", e);
    }
  }

  public static String getProperty(String key) {
    return resolveValueIfHasEnvironmentVarSyntax(properties.getProperty(key));
  }

  public static String getRequiredProperty(String key) throws Exception {

    if (properties.getProperty(key) == null || properties.getProperty(key).equals("")) {
      throw new Exception(
              String.format("Required %s parameter was not found in configuration file.", key));
    }

    return resolveValueIfHasEnvironmentVarSyntax(properties.getProperty(key));
  }

  public static String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  // http://www.java2s.com/Code/Java/Development-Class/Extractsaspecificpropertykeysubsetfromtheknownproperties.htm
  @SuppressWarnings("rawtypes")
  public static Properties getSubset(String prefix, boolean keepPrefix) {
    Properties result = new Properties();

    // sanity check
    if (prefix == null || prefix.length() == 0) {
      return result;
    }

    String prefixMatch; // match prefix strings with this
    String prefixSelf; // match self with this
    if (prefix.charAt(prefix.length() - 1) != '.') {
      // prefix does not end in a dot
      prefixSelf = prefix;
      prefixMatch = prefix + '.';
    } else {
      // prefix does end in one dot, remove for exact matches
      prefixSelf = prefix.substring(0, prefix.length() - 1);
      prefixMatch = prefix;
    }
    // POSTCONDITION: prefixMatch and prefixSelf are initialized!

    // now add all matches into the resulting properties.
    // Remark 1: #propertyNames() will contain the System properties!
    // Remark 2: We need to give priority to System properties. This is done
    // automatically by calling this class's getProperty method.
    String key;
    for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
      key = (String) e.nextElement();

      if (keepPrefix) {
        // keep full prefix in result, also copy direct matches
        if (key.startsWith(prefixMatch) || key.equals(prefixSelf)) {
          result.setProperty(key, getProperty(key));
        }
      } else {
        // remove full prefix in result, dont copy direct matches
        if (key.startsWith(prefixMatch)) {
          result.setProperty(key.substring(prefixMatch.length()), getProperty(key));
        }
      }
    }

    // done
    return result;
  }

  public static String[] getPropertyAsStringArray(String key) {
    String driverArgumentsAsString = getProperty(key, "");

    if (driverArgumentsAsString.equals("")) {
      return new String[] {};
    }

    return driverArgumentsAsString.split("\\s+");
  }

  public static String resolveValueIfHasEnvironmentVarSyntax(String value) {
    if (null == value) {
      return null;
    }

    Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
    Matcher m = p.matcher(value);
    StringBuffer sb = new StringBuffer();
    boolean hasEnvironmentSyntax = false;
    while (m.find()) {
      hasEnvironmentSyntax = true;
      String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
      String envVarValue = System.getenv(envVarName);
      m.appendReplacement(sb, null == envVarValue ? "" : Matcher.quoteReplacement(envVarValue));
    }
    m.appendTail(sb);

    if (hasEnvironmentSyntax) {
      return sb.toString();
    } else {
      return value;
    }

  }

}
