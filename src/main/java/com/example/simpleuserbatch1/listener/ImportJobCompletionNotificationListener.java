package com.example.simpleuserbatch1.listener;

import com.example.simpleuserbatch1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImportJobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImportJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("IMPORT JOB COMPLETED!");

            jdbcTemplate.query("SELECT count(*) as count FROM user WHERE import_job_id = " + jobExecution.getJobId(),
                    (rs, row) -> rs.getLong("count")
            ).forEach(count -> log.info("Successfully Processed: {} users", count));

        }
    }
}
