package edu.utec.automation.common.configurations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utec.automation.testinator.common.Configurations;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestConfigurationsSubSet {
  
  private final Logger logger = LoggerFactory.getLogger(TestConfigurationsSubSet.class);
  
  public static String absoluteConfigurationsFilePath = null;

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
    configurationsAsString.append("remote_node.driver.options = --no-sandbox  \n");
    configurationsAsString.append("remote_node.driver.capability.key1 = value1\n");
    configurationsAsString.append("remote_node.driver.capability.key2 = value2\n");
    configurationsAsString
            .append("chrome.driver.options = start-maximized --no-sandbox --disable-extensions \n");
    FileUtils.writeStringToFile(new File(absoluteConfigurationsFilePath),
            configurationsAsString.toString(), Charsets.toCharset("UTF-8"));

    return absoluteConfigurationsFilePath;
  }

  public void deleteFile(String absoluteConfigurationsFilePath) {
    File file = new File(absoluteConfigurationsFilePath);

    if(!file.exists()) {
      logger.info("File does not exist");
      return;
    }
    
    if (file.delete()) {
      logger.info("File deleted successfully");
    }
  }

  @Test
  public void test_001_subset_size() throws Exception {
    absoluteConfigurationsFilePath = createConfigurationForTest();
    Configurations.initFromExternalFile(absoluteConfigurationsFilePath);

    Properties subSet = Configurations.getSubset("remote_node.driver.capability", true);
    assertNotNull(subSet);

    assertEquals("Options must be 2.", new Integer(subSet.size()), new Integer(2));
  }

  
  @Test
  public void test_002_subset_keep_prefix_match_key_value() throws Exception {
    
    Properties subSet = Configurations.getSubset("remote_node.driver.capability", true);
    assertNotNull(subSet);
    
    HashMap<String, String> expectedValues = new HashMap<>();
    expectedValues.put("remote_node.driver.capability.key1", "value1");
    expectedValues.put("remote_node.driver.capability.key2", "value2");

    for (Entry<Object, Object> entry : subSet.entrySet()) {
      assertTrue("Key must exists in properties file.", expectedValues.containsKey(entry.getKey()));
      String foundedValue = (String) entry.getValue();
      String expectedValue = expectedValues.get(entry.getKey());
      assertTrue("Founded value must match with expected value", foundedValue.equals(expectedValue));
    }

  }
  
  @Test
  public void test_003_subset_not_prefix_match_key_value() throws Exception {
    
    Properties subSet = Configurations.getSubset("remote_node.driver.capability", false);
    assertNotNull(subSet);
    
    HashMap<String, String> expectedValues = new HashMap<>();
    expectedValues.put("key1", "value1");
    expectedValues.put("key2", "value2");

    for (Entry<Object, Object> entry : subSet.entrySet()) {
      assertTrue("Key must exists in properties file.", expectedValues.containsKey(entry.getKey()));
      String foundedValue = (String) entry.getValue();
      String expectedValue = expectedValues.get(entry.getKey());
      assertTrue("Founded value must match with expected value", foundedValue.equals(expectedValue));
    }

    deleteFile(absoluteConfigurationsFilePath);
  }  
  
}
