package edu.utec.automation.testinator.cmd;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import edu.utec.automation.testinator.Testinator;
import edu.utec.automation.testinator.TestinatorParameters;
import edu.utec.automation.testinator.TestinatorParametersBuilder;
import edu.utec.automation.testinator.common.BrowserConfigurations;
import edu.utec.automation.testinator.common.DriverConfigurations;
import edu.utec.automation.testinator.common.SeleniumAdvancedConfigurations;
import edu.utec.automation.testinator.common.SeleniumBotConstants;

public class TestinatorCmdLauncher {

  private static Logger logger = LoggerFactory.getLogger(TestinatorCmdLauncher.class);

  public static void main(final String[] args) throws Exception {

    TestinatorParameters parameters = null;
    List<String> testCasesFilePaths = null;
    Settings settings = null;

    try {

      settings = new Settings();
      JCommander commander = new JCommander(settings);
      commander.parse(args);

      SeleniumAdvancedConfigurations.initFromRootClasspathFile("selenium.properties");

      if (settings.getBrowserPropertiesFilePath() == null) {
        logger.error("File with browser configurations is required.");
        return;
      } else {
        BrowserConfigurations.initFromExternalFile(settings.getBrowserPropertiesFilePath());
      }

      if (settings.getDriverConfigurations() == null) {
        logger.error("File with driver configurations is required.");
        return;
      } else {
        DriverConfigurations.initFromExternalFile(settings.getDriverConfigurations());
      }

      String browser = BrowserConfigurations.getRequiredProperty("browser");
      Properties specificDriverConfigurations = DriverConfigurations.getSubset(browser, false);

      parameters = new TestinatorParametersBuilder()
              .setDriverUrl(DriverConfigurations.getProperty("driver.url"))
              .setDriverAuthUser(DriverConfigurations.getProperty("driver.auth.user"))
              .setDriverAuthPassword(DriverConfigurations.getProperty("driver.auth.password"))
              .setBrowser(browser)
              .setDriverPath(specificDriverConfigurations.getProperty("driver.path"))
              .setDriverOptions(BrowserConfigurations.getPropertyAsStringArray("driver.options"))
              .setCapabilities(BrowserConfigurations.getSubset("driver.capability", false))
              .setPageLoadTimeout(Integer.parseInt(BrowserConfigurations.getProperty(
                      "page.load.timeout", SeleniumBotConstants.DEFAULT_PAGE_LOAD_TIMEOUT)))
              .setPageImplicitlyWait(Integer.parseInt(BrowserConfigurations.getProperty(
                      "page.implicitly.wait", SeleniumBotConstants.DEFAULT_PAGE_IMPLICITLY_WAIT)))
              .setMaximizeWindow(Boolean.parseBoolean(BrowserConfigurations.getProperty(
                      "page.window.maximize", SeleniumBotConstants.DEFAULT_PAGE_MAXIMIZE)))
              .setDeleteAllCookies(Boolean.parseBoolean(
                      BrowserConfigurations.getProperty("page.window.deleteAllCookies",
                              SeleniumBotConstants.DEFAULT_PAGE_DELETE_ALL_COOKIES)))
              .setKeepBrowserOpenAtTheEnd(Boolean.parseBoolean(BrowserConfigurations.getProperty(
                      "page.keep.open", SeleniumBotConstants.DEFAULT_KEEP_BROWSER_OPEN_AT_THE_END)))
              .setAlwaysTakeScreeshotAtTheEnd(Boolean
                      .parseBoolean(BrowserConfigurations.getProperty("page.always.screeshot",
                              SeleniumBotConstants.DEFAULT_ALWAYS_TAKE_SCREENSHOT__AT_THE_END)))
              .setTestCaseErrorScreenshotPath(BrowserConfigurations.getProperty("screenshot.path"))
              .build();

      if (settings.getTestCasesFolderPath() == null) {
        logger.error("Folder containing test cases is required.");
        logger.error("No automation will be launched.");
        return;
      }

      if (settings.getExecutionOrderFilePath() == null) {
        logger.error("Execution order of testcases is unknown. Add the execution order file path");
        logger.error("No automation will be launched.");
        return;
      }

      testCasesFilePaths = FileUtils.readLines(new File(settings.getExecutionOrderFilePath()),
              "utf-8");

      if (testCasesFilePaths == null || testCasesFilePaths.size() < 1) {
        logger.error("File with order execution is empty");
        return;
      }

      String firstTestCaseFilePath = String.format("%s%s%s", settings.getTestCasesFolderPath(),
              File.separator, testCasesFilePaths.get(0));

      if (!new File(firstTestCaseFilePath).exists()) {
        logger.error(String.format(
                "Testcase folder path or test case order are wrong. This testcase is not found",
                firstTestCaseFilePath));
        return;
      }

    } catch (Exception e) {
      throw new Exception("Failed in selenium, browser and other configurations .", e);
    }

    Testinator seleniumBot = new Testinator();
    try {
      seleniumBot.setParameters(parameters);
      seleniumBot.configure();

      boolean result = true;

      for (String testCaseFilePath : testCasesFilePaths) {
        result = seleniumBot.executeKatalonXmlAutomation(
                settings.getTestCasesFolderPath() + "/" + testCaseFilePath);

        if (!result) {
          result = false;
          break;
        }
      }

      seleniumBot.finalizeAutomation(result, parameters.keepBrowserOpenAtTheEnd(),
              parameters.alwaysTakeScreeshotAtTheEnd());

    } catch (Exception e) {
      seleniumBot.finalizeAutomation(false, parameters.keepBrowserOpenAtTheEnd(),
              parameters.alwaysTakeScreeshotAtTheEnd());
    }

  }

}
