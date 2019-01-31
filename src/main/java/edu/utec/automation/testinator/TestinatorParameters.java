package edu.utec.automation.testinator;

import java.util.Properties;

public class TestinatorParameters {

  private String testCaseErrorScreenshotPath;
  private String driverUrl;
  private String driverAuthUser;
  private String driverAuthPassword;
  private String browser;
  private String driverPath;
  private String[] driverOptions;
  private Properties capabilities;
  private int pageLoadTimeout;
  private int pageImplicitlyWait;
  private boolean maximizeWindow;
  private boolean deleteAllCookies;
  private boolean keepBrowserOpenAtTheEnd;
  private boolean alwaysTakeScreeshotAtTheEnd;

  public TestinatorParameters(String testCaseErrorScreenshotPath,
          String driverUrl, String driverAuthUser, String driverAuthPassword, String browser,
          String driverPath, String[] driverOptions, Properties capabilities, int pageLoadTimeout,
          int pageImplicitlyWait, boolean maximizeWindow, boolean deleteAllCookies,
          boolean keepBrowserOpenAtTheEnd, boolean alwaysTakeScreeshotAtTheEnd) {
    super();
    this.testCaseErrorScreenshotPath = testCaseErrorScreenshotPath;
    this.driverUrl = driverUrl;
    this.driverAuthUser = driverAuthUser;
    this.driverAuthPassword = driverAuthPassword;
    this.browser = browser;
    this.driverPath = driverPath;
    this.driverOptions = driverOptions;
    this.capabilities = capabilities;
    this.pageLoadTimeout = pageLoadTimeout;
    this.pageImplicitlyWait = pageImplicitlyWait;
    this.maximizeWindow = maximizeWindow;
    this.deleteAllCookies = deleteAllCookies;
    this.keepBrowserOpenAtTheEnd = keepBrowserOpenAtTheEnd;
    this.alwaysTakeScreeshotAtTheEnd = alwaysTakeScreeshotAtTheEnd;
  }

  public String getTestCaseErrorScreenshotPath() {
    return testCaseErrorScreenshotPath;
  }

  public String getDriverUrl() {
    return driverUrl;
  }

  public String getDriverAuthUser() {
    return driverAuthUser;
  }

  public String getDriverAuthPassword() {
    return driverAuthPassword;
  }

  public String getBrowser() {
    return browser;
  }

  public String getDriverPath() {
    return driverPath;
  }

  public String[] getDriverOptions() {
    return driverOptions;
  }

  public Properties getCapabilities() {
    return capabilities;
  }

  public int getPageLoadTimeout() {
    return pageLoadTimeout;
  }

  public int getPageImplicitlyWait() {
    return pageImplicitlyWait;
  }

  public boolean isMaximizeWindow() {
    return maximizeWindow;
  }

  public boolean isDeleteAllCookies() {
    return deleteAllCookies;
  }

  public boolean keepBrowserOpenAtTheEnd() {
    return keepBrowserOpenAtTheEnd;
  }

  public boolean alwaysTakeScreeshotAtTheEnd() {
    return alwaysTakeScreeshotAtTheEnd;
  }

}
