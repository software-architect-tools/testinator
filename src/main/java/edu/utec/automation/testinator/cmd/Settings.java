package edu.utec.automation.testinator.cmd;

import com.beust.jcommander.Parameter;

public class Settings {

  @Parameter(names = "-b", description = "Properties file with browser configutations", required = false)
  private String browserPropertiesFilePath;

  @Parameter(names = "-t", description = "Test cases folder", required = true)
  private String testCasesFolderPath;

  @Parameter(names = "-o", description = "Test cases file order.", required = true)
  private String executionOrderFilePath;

  @Parameter(names = "-d", description = "Test cases data file", required = false)
  private String dataFilePath;

  @Parameter(names = "-dc", description = "Driver configurations", required = false)
  private String driverConfigurations;

  public String getBrowserPropertiesFilePath() {
    return browserPropertiesFilePath;
  }

  public String getTestCasesFolderPath() {
    return testCasesFolderPath;
  }

  public String getDataFilePath() {
    return dataFilePath;
  }

  public String getExecutionOrderFilePath() {
    return executionOrderFilePath;
  }

  public String getDriverConfigurations() {
    return driverConfigurations;
  }

}
