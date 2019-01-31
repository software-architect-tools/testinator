package edu.utec.automation.common.configurations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utec.automation.testinator.common.Configurations;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestConfigurations {

  private final Logger logger = LoggerFactory.getLogger(TestConfigurations.class);
  
  public String createConfigurationForTest() throws IOException {
    String tempDir = System.getProperty("java.io.tmpdir");
    String fileName = "Configurations_" + (new Date().getTime() + ".properties");
    String absoluteConfigurationsFilePath = String.format("%s%s%s", tempDir, File.separator,
            fileName);

    logger.info("file:" + absoluteConfigurationsFilePath);

    StringBuffer configurationsAsString = new StringBuffer();
    configurationsAsString.append("driver.type = chrome  \n");
    configurationsAsString.append("driver.path = /home/rleon/Documents/qa/chromedriver-2.43  \n");
    configurationsAsString.append("screenshot.path = /tmp/qa  \n");
    configurationsAsString
            .append("chrome.driver.options = start-maximized --no-sandbox --disable-extensions \n");
    FileUtils.writeStringToFile(new File(absoluteConfigurationsFilePath),
            configurationsAsString.toString(), Charsets.toCharset("UTF-8"));

    return absoluteConfigurationsFilePath;
  }

  public void deleteFile(String absoluteConfigurationsFilePath) {
    File file = new File(absoluteConfigurationsFilePath);

    if (file.delete()) {
      logger.info("File deleted successfully");
    }
  }

  @Test
  public void test_001_options() throws Exception {
    String absoluteConfigurationsFilePath = createConfigurationForTest();
    Configurations.initFromExternalFile(absoluteConfigurationsFilePath);

    String stringOptions = Configurations.getProperty("chrome.driver.options");
    assertNotNull(stringOptions);
    String[] options = Configurations
            .getPropertyAsStringArray(String.format("%s.driver.options", "chrome"));

    assertNotNull(options);
    assertEquals("Options must be 3.", new Integer(options.length), new Integer(3));

    deleteFile(absoluteConfigurationsFilePath);
  }

}
