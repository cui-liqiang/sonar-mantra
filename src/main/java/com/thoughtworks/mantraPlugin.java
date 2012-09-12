package com.thoughtworks;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * This class is the entry point for all extensions
 */
@Properties({
    @Property(
        key = mantraPlugin.MY_PROPERTY,
        name = "mantra",
        description = "a plugin to check whether your code coverage is lower than last build")})
public final class mantraPlugin extends SonarPlugin {

  public static final String MY_PROPERTY = "com.thoughtworks.mantra";

  public List getExtensions() {
    return Arrays.asList(CheckCoverageDelta.class);
  }
}