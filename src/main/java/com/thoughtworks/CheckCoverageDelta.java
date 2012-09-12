package com.thoughtworks;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.resources.Project;

import java.math.BigDecimal;
import java.util.List;

public class CheckCoverageDelta implements PostJob {
    private DatabaseSession session;

    public CheckCoverageDelta(DatabaseSession session) {
        this.session = session;
    }

    public void executeOn(Project project, SensorContext sensorContext) {
        String sql="select value from project_measures as pm \n" +
                "inner join snapshots as s \n" +
                "on pm.snapshot_id=s.id \n" +
                "inner join projects as p\n" +
                "on s.project_id=p.id\n" +
                "inner join metrics as m\n" +
                "on pm.metric_id=m.id\n" +
                "where m.name='coverage'\n" +
                "and p.name='" + project.getName() + "'\n" +
                "and p.root_id is NULL\n" +
                "order by created_at \n" +
                "desc\n" +
                "limit 2;";
        List resultList = session.createNativeQuery(sql).getResultList();
        BigDecimal thisCoverage = (BigDecimal)resultList.get(0);
        BigDecimal lastCoverage = (BigDecimal)resultList.get(1);
        if(thisCoverage.compareTo(lastCoverage) < 0) {
            throw new RuntimeException("Your code coverage decrease by " + lastCoverage.subtract(thisCoverage).toString());
        }
    }
}
