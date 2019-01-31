package edu.utec.automation.testinator.common;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenShotUtil {

  private final static Logger logger = LoggerFactory.getLogger(ScreenShotUtil.class);

  public static void saveSeleniumScreenshotAsLocalFile(WebDriver driver, String filePath)
          throws Exception {
    
    if(filePath==null || filePath.equals("")) {
      throw new Exception("Screenshot path is wrong or empty");
    }
    
    logger.info("Saving screenshot of last visible page in: "+filePath);

    File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

    filePath = String.format("%s%s%s.png", Configurations.getProperty("screenshot.path"),
            File.separator, new Date().getTime());

    logger.info("Screenshot path:" + filePath);

    FileUtils.copyFile(scrFile, new File(filePath));

  }
}
