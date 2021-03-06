package com.example.simpleuserbatch1.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelloWorldTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Long jobExecutionId = stepContribution.getStepExecution().getJobExecutionId();
        log.info("Hello World from Job# {}", jobExecutionId);
        return RepeatStatus.FINISHED;
    }

}
