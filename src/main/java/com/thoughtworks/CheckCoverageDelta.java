package com.thoughtworks;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

public class CheckCoverageDelta implements PostJob {

    private  static double THRESHOLD = 80;

    public void executeOn(Project project, SensorContext sensorContext) {
        Measure measure = sensorContext.getMeasure(CoreMetrics.COVERAGE);
        if(measure == null) {
            return;
        }
        if(measure.getValue() < THRESHOLD) {
            Double coverageChanges = measure.getVariation1();
            if(coverageChanges != null && coverageChanges < 0) {
                throw new RuntimeException("Your code coverage decrease by " + coverageChanges);
            }
        }
    }
}
