package edu.utec.automation.testinator.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utec.automation.selenium.common.StringUtil;
import edu.utec.automation.testinator.TestinatorParameters;

public class WebDriverFactory {

  private Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

  private DesiredCapabilities loadCapabilities(Properties capabilitiesFromProperties) {

    logger.info("Loading capabilities");

    DesiredCapabilities caps = new DesiredCapabilities();

    if (capabilitiesFromProperties == null || capabilitiesFromProperties.isEmpty()) {
      logger.warn("Capabilities are empty");
      return caps;
    }

    for (Entry<Object, Object> entry : capabilitiesFromProperties.entrySet()) {
      logger.warn(String.format("%s : %s", (String) entry.getKey(), (String) entry.getValue()));
      caps.setCapability((String) entry.getKey(), (String) entry.getValue());
    }

    return caps;
  }

  public WebDriver createDriver(TestinatorParameters parameters) throws Exception {

    logger.info(String.format("Loading %s driver...", parameters.getBrowser()));

    WebDriver driver = null;

    if (parameters.getDriverUrl() == null) {
      logger.info("driver.url was not founded. Local driver will be loaded instead remote driver");
      driver = loadLocalDriver(parameters);
    } else {
      logger.info("driver.url parameters was found. Remote driver will be configured.");
      driver = loadRemoteDriver(parameters);
    }

    if (parameters.isMaximizeWindow()) {
      driver.manage().window().maximize();
    }

    if (parameters.isDeleteAllCookies()) {
      driver.manage().deleteAllCookies();
    }

    driver.manage().timeouts().pageLoadTimeout(parameters.getPageLoadTimeout(), TimeUnit.SECONDS);
    driver.manage().timeouts().implicitlyWait(parameters.getPageImplicitlyWait(), TimeUnit.SECONDS);

    return driver;
  }

  private WebDriver loadLocalDriver(TestinatorParameters parameters) throws Exception {

    String browser = parameters.getBrowser();

    Properties seleniumConfigurations = SeleniumAdvancedConfigurations.getSubset(browser, false);

    if (seleniumConfigurations.getProperty("driver.binary.key") != null) {
      System.setProperty(seleniumConfigurations.getProperty("driver.binary.key"),
              parameters.getDriverPath());
    }

    DesiredCapabilities caps = createCapabilitiesAndOptions(parameters, seleniumConfigurations);

    if (seleniumConfigurations.getProperty("driver.class") == null
            || seleniumConfigurations.getProperty("driver.class").equals("")) {
      throw new Exception("driver.class is empty for for this browser:"+browser);
    }

    Class<?> dynamicDriverClass = Class.forName(seleniumConfigurations.getProperty("driver.class"));
    return (WebDriver) dynamicDriverClass.getConstructor(Capabilities.class).newInstance(caps);
  }

  private WebDriver loadRemoteDriver(TestinatorParameters parameters) throws Exception {

    String remoteUrlDriver = null;
    WebDriver webDriver = null;
    String driver = parameters.getBrowser();

    if ((parameters.getDriverAuthUser() != null && !parameters.getDriverAuthUser().equals(""))
            && (parameters.getDriverAuthPassword() != null
                    && !parameters.getDriverAuthPassword().equals(""))) {
      logger.warn("Basic auth credentials was found.");
      remoteUrlDriver = StringUtil.addBasicAuthCredentialsToHttpUrl(parameters.getDriverUrl(),
              parameters.getDriverAuthUser(), parameters.getDriverAuthPassword());
    } else {
      remoteUrlDriver = parameters.getDriverUrl();
      logger.warn("Basic auth credentials are empty or are not configured."
              + "Check driver.auth.user and driver.auth.password in your properties");
    }

    Properties seleniumConfigurations = SeleniumAdvancedConfigurations.getSubset(driver, false);
    DesiredCapabilities caps = createCapabilitiesAndOptions(parameters, seleniumConfigurations);

    logger.info("Connecting to remote driver: " + parameters.getDriverUrl());
    webDriver = new RemoteWebDriver(new URL(remoteUrlDriver), caps);
    return webDriver;
  }

  private DesiredCapabilities createCapabilitiesAndOptions(TestinatorParameters parameters,
          Properties seleniumConfigurations) throws ClassNotFoundException, InstantiationException,
          IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException,
          IllegalArgumentException, InvocationTargetException {

    DesiredCapabilities caps = loadCapabilities(parameters.getCapabilities());

    if (seleniumConfigurations.getProperty("options.class") != null
            && parameters.getDriverOptions() != null && parameters.getDriverOptions().length > 0) {

      Class<?> dynamicOptionClass = Class
              .forName(seleniumConfigurations.getProperty("options.class"));

      Object options = dynamicOptionClass.newInstance();

      Method addArgumentsMethod = dynamicOptionClass.getMethod("addArguments", String[].class);
      addArgumentsMethod.invoke(options, new Object[] { parameters.getDriverOptions() });

      Field capabilityField = dynamicOptionClass
              .getField(seleniumConfigurations.getProperty("options.capability.key"));

      caps.setCapability((String) capabilityField.get(null), options);
    }

    return caps;
  }

}
