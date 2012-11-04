package com.thoughtworks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckCoverageDelta implements PostJob {

    private  static double THRESHOLD = 80;
    private static final Logger LOG = LoggerFactory.getLogger(CheckCoverageDelta.class);

    public void executeOn(Project project, SensorContext sensorContext) {
        Measure measure = sensorContext.getMeasure(CoreMetrics.COVERAGE);
        if(measure == null) {
            return;
        }
        if(measure.getValue() < THRESHOLD) {
            boolean fail = false;
            String lastSuccessfulHistoryData = getLastSuccessfulHistoryData(project.getName());
            if(lastSuccessfulHistoryData != null) {
                fail = measure.getValue() < Double.valueOf(lastSuccessfulHistoryData);
            }
            recordHistoryData(project.getName(), measure, fail);
            if(fail) {
                throw new RuntimeException("Your code coverage decrease by " + (Double.valueOf(lastSuccessfulHistoryData) - measure.getValue()));
            }
        }
    }

    private String getLastSuccessfulHistoryData(String projectName) {
        String snapshotFile = System.getenv("SONAR_HOME") + "/extensions/plugins/" + projectName + ".csv";
        try {
            File file = new File(snapshotFile);
            if (!file.exists()) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();

            List<String> valueAndStatuses = readFile(reader);
            for(int i = valueAndStatuses.size() - 1; i >= 0;i--) {
                String[] split = valueAndStatuses.get(i).split(",");
                if(split[1].equals("true")) return split[0];
            }
        } catch (IOException e) {
            LOG.error("Cannot open file " + snapshotFile + " to write");
        }
        return null;
    }

    private List<String> readFile(BufferedReader reader) throws IOException {
        List<String> valueAndStatuses = new ArrayList<String>();
        String line;
        while((line = reader.readLine()) != null) {
            valueAndStatuses.add(line);
        }
        return valueAndStatuses;
    }

    private void recordHistoryData(String projectName, Measure measure, boolean fail) {
        String projectDataFile = System.getenv("SONAR_HOME") + "/extensions/plugins/" + projectName + ".csv";
        try {
            File file = new File(projectDataFile);
            boolean newFile = !file.exists();
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true));
            if (newFile) {
                writeHeader(fileWriter);
            }
            fileWriter.write(measure.getValue() + "," + !fail);
            fileWriter.newLine();
            fileWriter.close();
        } catch (IOException e) {
            LOG.error("Cannot open file " + projectDataFile + " to write");
        }
    }

    private void writeHeader(BufferedWriter fileWriter) throws IOException {
        fileWriter.write("Coverage,success");
        fileWriter.newLine();
    }
}
