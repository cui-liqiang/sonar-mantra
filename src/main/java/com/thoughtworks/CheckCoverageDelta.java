package com.thoughtworks;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CheckCoverageDelta implements PostJob {

    private static final String CHECK_DELTA_CONF_FILE = "/checkDelta.conf";

    public void executeOn(Project project, SensorContext sensorContext) {
        StringBuilder failMsg = new StringBuilder();

        for (DeltaMeasure deltaMeasure : getDeltaMeasures(sensorContext)) {
            if(deltaMeasure.worseThanThreshold()) {
                failMsg.append(deltaMeasure.logResultsAndReturnMessage(project.getName()));
            }
        }

        if(!failMsg.toString().equals("")) {
            throw new RuntimeException(failMsg.toString());
        }
    }

    private List<DeltaMeasure> getDeltaMeasures(SensorContext sensorContext) {
        InputStream resourceAsStream = getClass().getResourceAsStream(CHECK_DELTA_CONF_FILE);
        ArrayList<DeltaMeasure> deltaMeasures = new ArrayList<DeltaMeasure>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
            String line;
            while((line = br.readLine()) != null) {
                String[] metricAndThreshold = line.split(",");
                Field field = CoreMetrics.class.getDeclaredField(metricAndThreshold[0].toUpperCase());
                field.setAccessible(true);
                Measure measure = sensorContext.getMeasure((Metric) field.get(null));
                deltaMeasures.add(new DeltaMeasure(measure, Double.valueOf(metricAndThreshold[1])));
            }
            return deltaMeasures;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
