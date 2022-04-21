package com.example.simpleuserbatch1.processor;

import com.example.simpleuserbatch1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Value("${username.prefix: user_}")
    private String usernamePrefix;

    private JobExecution jobExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

    @Override
    public User process(User user) throws Exception {

        String username = usernamePrefix + System.nanoTime();
        log.info("Adding generated username: " + username + " | JobId: " + jobExecution.getJobId());
        user.setUsername(username);
        user.setImportJobId(jobExecution.getJobId());
        return user;
    }

}
