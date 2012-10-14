package com.thoughtworks;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;

import java.math.BigDecimal;
import java.util.List;

public class CheckCoverageDelta implements PostJob {
    private DatabaseSession session;

    public CheckCoverageDelta(DatabaseSession session) {
        this.session = session;
    }

    public void executeOn(Project project, SensorContext sensorContext) {
        Double coverageChanges = sensorContext.getMeasure(CoreMetrics.COVERAGE).getVariation1();
        if(coverageChanges < 0) {
            throw new RuntimeException("Your code coverage decrease by " + coverageChanges);
        }
    }
}
