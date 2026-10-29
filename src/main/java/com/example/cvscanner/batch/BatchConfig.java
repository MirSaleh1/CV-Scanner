package com.example.cvscanner.batch;

import com.example.cvscanner.entity.CvRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public CvReader reader(@Value("#{jobParameters['folderPath']}") String folderPath) {
        return new CvReader(folderPath);
    }

    @Bean
    public Step cvProcessingStep(JobRepository jobRepository,
                                 PlatformTransactionManager platformTransactionManager,
                                 CvReader reader,
                                 CvItemWriter cvItemWriter,
                                 CvItemProcessor cvItemProcessor) {
        return new StepBuilder("cvProcessingStep", jobRepository)
                .<File, CvRecord>chunk(20, platformTransactionManager)
                .reader(reader)
                .processor(cvItemProcessor)
                .writer(cvItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }
    @Bean
    public Job cvProcessingJob(JobRepository jobRepository,Step cvProcessingStep) {
        return new JobBuilder("cvProcessingJob",jobRepository)
                .start(cvProcessingStep)
                .build();
    }

}
