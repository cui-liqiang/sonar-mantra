package com.thoughtworks.deltameasures;

import org.sonar.api.measures.Measure;

public class CoverageDeltaMeasure extends DeltaMeasure {
    public CoverageDeltaMeasure(Measure measure, Double value) {
        super(measure, value);
    }

    public String logResultsAndReturnMessage(String projectName) {
        boolean fail = false;
        String lastSuccessfulHistoryData = getLastSuccessfulHistoryData(projectName, measure.getMetric().getName());

        if(lastSuccessfulHistoryData != null) {
            fail = measure.getValue() < Double.valueOf(lastSuccessfulHistoryData);
        }

        recordHistoryData(projectName, measure, fail);

        if(fail) {
            return "Your code coverage decrease by " + (Double.valueOf(lastSuccessfulHistoryData) - measure.getValue()) + "\n";
        }
        return "";
    }
}
