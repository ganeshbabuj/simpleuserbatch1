package com.example.simpleuserbatch1.config;

import javax.sql.DataSource;

import com.example.simpleuserbatch1.listener.ItemCountListener;
import com.example.simpleuserbatch1.listener.ImportJobCompletionNotificationListener;
import com.example.simpleuserbatch1.model.User;
import com.example.simpleuserbatch1.processor.HelloWorldTasklet;
import com.example.simpleuserbatch1.processor.UserItemProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
@Component
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    UserItemProcessor userItemProcessor;

    @Autowired
    ItemCountListener itemCountListener;

    @Autowired
    HelloWorldTasklet helloWorldTasklet;

    @Autowired
    JobLauncher jobLauncher;

    @Value("${user.filename}")
    private String usersFilename;

    private static final String INSERT_SQL = "INSERT INTO user (username, first_name, last_name, import_job_id) VALUES (:username, :firstName, :lastName, :importJobId)";

    @Bean
    public FlatFileItemReader<User> reader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("UserItemReader")
                .resource(new ClassPathResource(usersFilename))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                    setTargetType(User.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<User> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(INSERT_SQL)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step importUserStep(JdbcBatchItemWriter<User> writer) {
        return stepBuilderFactory.get("importUserStep")
                .<User, User> chunk(3)
                .reader(reader())
                .processor(userItemProcessor)
                .writer(writer)
                .listener(itemCountListener)
                .build();
    }

    @Bean
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(helloWorldTasklet)
                .build();
    }


    @Bean
    public Job importUserJob(ImportJobCompletionNotificationListener listener, Step importUserStep , Step helloWorldStep) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importUserStep)
                .next(helloWorldStep)
                .end()
                .build();
    }

    @Bean
    public Job helloWorldJob() {
        return jobBuilderFactory.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .flow(helloWorldStep())
                .end()
                .build();
    }



}

