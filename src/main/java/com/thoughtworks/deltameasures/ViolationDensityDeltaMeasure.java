package com.thoughtworks.deltameasures;

import org.sonar.api.measures.Measure;

public class ViolationDensityDeltaMeasure extends DeltaMeasure {
    public ViolationDensityDeltaMeasure(Measure measure, Double value) {
        super(measure, value);
    }

    @Override
    public String logResultsAndReturnMessage(String projectName) {
        boolean fail = false;
        String lastSuccessfulHistoryData = getLastSuccessfulHistoryData(projectName, measure.getMetric().getName());

        if(lastSuccessfulHistoryData != null) {
            fail = measure.getValue() < Double.valueOf(lastSuccessfulHistoryData);
        }

        recordHistoryData(projectName, measure, fail);

        if(fail) {
            return "Your violations density decrease by " + (Double.valueOf(lastSuccessfulHistoryData) - measure.getValue()) + "\n";
        }
        return "";
    }
}
