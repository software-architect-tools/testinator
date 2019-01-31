package edu.utec.automation.testinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import edu.utec.automation.selenium.common.CommandsConstants;
import edu.utec.automation.selenium.common.XmlReaderUtil;
import edu.utec.automation.selenium.core.SeleniumCommand;
import edu.utec.automation.testinator.common.ScreenShotUtil;
import edu.utec.automation.testinator.common.SeleniumBotConstants;
import edu.utec.automation.testinator.common.WebDriverFactory;
import edu.utec.plugin.PluginUtil;

public class Testinator {

  private final Logger logger = LoggerFactory.getLogger(Testinator.class);

  private TestinatorParameters parameters;

  private HashMap<String, SeleniumCommand> commands;

  private String screenshotFilePath;

  private WebDriver driver;

  public Testinator() throws Exception {
  }

  public void configure() throws Exception {

    commands = new HashMap<>();

    logger.info("Load commands as plugins...");

    Map<String, Class<?>> seleniumCommandPlugins = PluginUtil
            .scanClassPathInheritOf(SeleniumCommand.class);

    for (Entry<String, Class<?>> entry : seleniumCommandPlugins.entrySet()) {
      if (!entry.getValue().isInterface()) {
        logger.info(entry.getKey());
        commands.put(entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1,
                entry.getKey().length()), (SeleniumCommand) entry.getValue().newInstance());
      }
    }

    screenshotFilePath = parameters.getTestCaseErrorScreenshotPath();

    WebDriverFactory driverFactory = new WebDriverFactory();
    driver = driverFactory.createDriver(parameters);
  }

  public boolean executeKatalonXmlAutomation(String xmlKatalonTestCaseFilePath) throws Exception {

    String banner = "\n"
            + "                       _            _                            \n"
            + "                      | |          | |                         _ \n"
            + " _ __   _____      __ | |_ ___  ___| |_    ___ __ _ ___  ___  (_)\n"
            + "| '_ \\ / _ \\ \\ /\\ / / | __/ _ \\/ __| __|  / __/ _` / __|/ _ \\\n"
            + "| | | |  __/\\ V  V /  | ||  __/\\__ \\ |_  | (_| (_| \\__ \\  __/  _ \n"
            + "|_| |_|\\___| \\_/\\_/    \\__\\___||___/\\__|  \\___\\__,_|___/\\___| (_)\n";

    logger.info(banner);
    logger.info("Katalon test case path: " + xmlKatalonTestCaseFilePath);

    if (driver != null && driver instanceof RemoteWebDriver) {
      logger.info(String.format("Session id: %s", ((RemoteWebDriver) driver).getSessionId()));
    }

    NodeList nodeList = XmlReaderUtil.loadNodeList(xmlKatalonTestCaseFilePath,
            CommandsConstants.KEY_TAG_NAME_SELENESE);
    logger.info("Number of detected test steps: " + nodeList.getLength());

    if (nodeList != null && nodeList.getLength() > 0) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        try {
          HashMap<String, String> commandArgs = XmlReaderUtil.xmlNodeToMap(nodeList.item(i));

          String commandName = commandArgs.get("command");
          logger.info("Command detected: " + commandName);

          SeleniumCommand command = commands
                  .get(PluginUtil.getDefaultImplClassNameFromSeleniumCommandName(commandName)
                          + "SeleniumCommand");

          if (command == null) {
            throw new Exception(
                    "Selenium command class was not found for this command: " + commandName);
          }

          logger.info("Command: " + command.getClass());

          command.execute(driver, commandArgs);

        } catch (Exception e) {

          logger.error("Error detected.", e);
          if (driver != null && driver instanceof RemoteWebDriver) {
            logger.error(
                    String.format("Session id: %s", ((RemoteWebDriver) driver).getSessionId()));
          }
          return SeleniumBotConstants.WRONG_EXECUTION;
        }
      }

      logger.info("Test case ended successfully.");
      return SeleniumBotConstants.SUCCESS_EXECUTION;
    } else {
      logger.info("Katalon xml is wrong or empty");
      return SeleniumBotConstants.SUCCESS_EXECUTION;
    }
  }

  public void finalizeAutomation(boolean result, boolean keepBrowserOpenAtTheEnd,
          boolean alwaysTakeScreeshotAtTheEnd) throws Exception {

    if (!result) {
      ScreenShotUtil.saveSeleniumScreenshotAsLocalFile(driver, screenshotFilePath);
    } else if (alwaysTakeScreeshotAtTheEnd) {
      ScreenShotUtil.saveSeleniumScreenshotAsLocalFile(driver, screenshotFilePath);
    }

    if (driver != null && !keepBrowserOpenAtTheEnd) {
      driver.close();
      driver.quit();
    }
  }

  public TestinatorParameters getParameters() {
    return parameters;
  }

  public void setParameters(TestinatorParameters parameters) {
    this.parameters = parameters;
  }

}
