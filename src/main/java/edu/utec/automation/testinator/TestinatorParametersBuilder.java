package edu.utec.automation.testinator;

import java.util.Properties;

public class TestinatorParametersBuilder {
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

  public TestinatorParameters build() throws Exception {
    return new TestinatorParameters(testCaseErrorScreenshotPath, driverUrl, driverAuthUser,
            driverAuthPassword, browser, driverPath, driverOptions, capabilities, pageLoadTimeout,
            pageImplicitlyWait, maximizeWindow, deleteAllCookies, keepBrowserOpenAtTheEnd,
            alwaysTakeScreeshotAtTheEnd);
  }

  public TestinatorParametersBuilder setTestCaseErrorScreenshotPath(
          String testCaseErrorScreenshotPath) {
    this.testCaseErrorScreenshotPath = testCaseErrorScreenshotPath;
    return this;
  }

  public TestinatorParametersBuilder setDriverUrl(String driverUrl) {
    this.driverUrl = driverUrl;
    return this;
  }

  public TestinatorParametersBuilder setDriverAuthUser(String driverAuthUser) {
    this.driverAuthUser = driverAuthUser;
    return this;
  }

  public TestinatorParametersBuilder setDriverAuthPassword(String driverAuthPassword) {
    this.driverAuthPassword = driverAuthPassword;
    return this;
  }

  public TestinatorParametersBuilder setBrowser(String browser) {
    this.browser = browser;
    return this;
  }

  public TestinatorParametersBuilder setDriverPath(String driverPath) {
    this.driverPath = driverPath;
    return this;
  }

  public TestinatorParametersBuilder setDriverOptions(String[] driverOptions) {
    this.driverOptions = driverOptions;
    return this;
  }

  public TestinatorParametersBuilder setCapabilities(Properties capabilities) {
    this.capabilities = capabilities;
    return this;
  }

  public TestinatorParametersBuilder setPageLoadTimeout(int pageLoadTimeout) {
    this.pageLoadTimeout = pageLoadTimeout;
    return this;
  }

  public TestinatorParametersBuilder setPageImplicitlyWait(int pageImplicitlyWait) {
    this.pageImplicitlyWait = pageImplicitlyWait;
    return this;
  }

  public TestinatorParametersBuilder setMaximizeWindow(boolean maximizeWindow) {
    this.maximizeWindow = maximizeWindow;
    return this;
  }

  public TestinatorParametersBuilder setDeleteAllCookies(boolean deleteAllCookies) {
    this.deleteAllCookies = deleteAllCookies;
    return this;
  }

  public TestinatorParametersBuilder setKeepBrowserOpenAtTheEnd(boolean keepBrowserOpenAtTheEnd) {
    this.keepBrowserOpenAtTheEnd = keepBrowserOpenAtTheEnd;
    return this;
  }

  public TestinatorParametersBuilder setAlwaysTakeScreeshotAtTheEnd(
          boolean alwaysTakeScreeshotAtTheEnd) {
    this.alwaysTakeScreeshotAtTheEnd = alwaysTakeScreeshotAtTheEnd;
    return this;
  }

}
