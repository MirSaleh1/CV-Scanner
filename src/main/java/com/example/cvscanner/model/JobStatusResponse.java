package com.example.cvscanner.model;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import java.time.LocalDateTime;

public record JobStatusResponse(
        Long jobId,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long readCount,
        long writeCount,
        long skipCount
) {
    public static JobStatusResponse maptoJobStatusResponse(JobExecution execution) {
        if(execution==null) throw new RuntimeException("Job Execution Not Found");
        long totalRead = 0;
        long totalWrite = 0;
        long totalSkip = 0;

        for (StepExecution stepExecution : execution.getStepExecutions()) {
            totalRead += stepExecution.getReadCount();
            totalWrite += stepExecution.getWriteCount();
            totalSkip += stepExecution.getSkipCount();
        }
        return new JobStatusResponse(execution.getJobId(),
                                    execution.getStatus().name(),
                                    execution.getStartTime(),
                                    execution.getEndTime(),
                                    totalRead,
                                    totalWrite,
                                    totalSkip);
    }
}