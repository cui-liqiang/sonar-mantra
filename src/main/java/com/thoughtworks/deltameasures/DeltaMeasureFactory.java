package com.thoughtworks.deltameasures;

import org.sonar.api.measures.Measure;

public class DeltaMeasureFactory {
    public DeltaMeasure getDeltaMeasure(Measure measure, Double value) {
        String name = measure.getMetric().getName();
        if("rules compliance".equalsIgnoreCase(name)) {
            return new ViolationDensityDeltaMeasure(measure, value);
        } else if("coverage".equalsIgnoreCase(name)) {
            return new CoverageDeltaMeasure(measure, value);
        }
        throw new RuntimeException("The measure type you are asking for, " + name + ", is not available now");
    }
}
