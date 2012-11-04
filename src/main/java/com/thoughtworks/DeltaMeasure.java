package com.thoughtworks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.Measure;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DeltaMeasure {
    public static final Logger LOG = LoggerFactory.getLogger(DeltaMeasure.class);
    private static final String SONAR_HOME = System.getenv("SONAR_HOME") + "/extensions/plugins/";
    private Measure measure;
    private Double threshold;

    public DeltaMeasure(Measure measure, Double threshold) {
        this.measure = measure;
        this.threshold = threshold;
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

    public boolean worseThanThreshold() {
        return measure != null && measure.getValue() < threshold;
    }

    private String getLastSuccessfulHistoryData(String projectName, String metricName) {
        String snapshotFile = SONAR_HOME + projectName + "_" + metricName + ".csv";
        try {
            File file = new File(snapshotFile);
            if (!file.exists()) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> valueAndStatuses = readHistoryData(reader);
            for(int i = valueAndStatuses.size() - 1; i >= 0;i--) {
                String[] valueAndStatus = valueAndStatuses.get(i).split(",");
                if(valueAndStatus[1].equals("true")) return valueAndStatus[0];
            }
        } catch (IOException e) {
            LOG.error("Cannot open file " + snapshotFile + " to write");
        }
        return null;
    }

    private List<String> readHistoryData(BufferedReader reader) throws IOException {
        List<String> valueAndStatuses = new ArrayList<String>();
        String line;
        while((line = reader.readLine()) != null) {
            valueAndStatuses.add(line);
        }
        return valueAndStatuses;
    }

    private void recordHistoryData(String projectName, Measure measure, boolean fail) {
        String projectDataFile = SONAR_HOME + projectName + "_" + measure.getMetric().getName() + ".csv";
        try {
            File file = new File(projectDataFile);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true));
            fileWriter.write(measure.getValue() + "," + !fail);
            fileWriter.newLine();
            fileWriter.close();
        } catch (IOException e) {
            LOG.error("Cannot open file " + projectDataFile + " to write");
        }
    }
}
