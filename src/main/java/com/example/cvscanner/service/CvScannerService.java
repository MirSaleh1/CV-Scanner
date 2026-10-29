package com.example.cvscanner.service;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.exception.InvalidFileException;
import com.example.cvscanner.mapper.CvFilterResponseMapper;
import com.example.cvscanner.model.*;
import com.example.cvscanner.repository.CvRecordRepository;
import com.example.cvscanner.utilty.CvSpecification;
import com.example.cvscanner.utilty.ZipUtility;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service

public class CvScannerService {

    private final CvRecordRepository cvRecordRepository;
    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "cv_uploads");
    private final ZipUtility zipUtility;
    private final Path storageDirectory;
    private final JobLauncher jobLauncher;
    private final Job cvProcessingJob;
    private final JobExplorer jobExplorer;
    private final CvFilterResponseMapper cvFilterResponseMapper;

    public CvScannerService(@Value("${file.upload-dir}") String uploadDir,
                            CvRecordRepository cvRecordRepository,ZipUtility zipUtility,
                            JobLauncher jobLauncher,Job job,JobExplorer jobExplorer,
                            CvFilterResponseMapper cvFilterResponseMapper) throws IOException {
        this.cvRecordRepository = cvRecordRepository;
        this.storageDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.zipUtility = zipUtility;
        this.jobLauncher = jobLauncher;
        this.cvProcessingJob = job;
        this.jobExplorer = jobExplorer;
        this.cvFilterResponseMapper = cvFilterResponseMapper;

        if (!Files.exists(this.storageDirectory)) {
            Files.createDirectories(this.storageDirectory);
        }
    }
    public Long uploadAndExtractCvArchive(MultipartFile file) throws IOException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        validateZipFile(file);
        if(!Files.exists(tempDir)){
            Files.createDirectories(tempDir);
        }
        String unicalName= UUID.randomUUID().toString()+ "_" +file.getOriginalFilename();
        Path tempFile=tempDir.resolve(unicalName);
        Files.copy(file.getInputStream(),tempFile);
        String filesPath=zipUtility.unzip(tempFile,storageDirectory);
        Files.deleteIfExists(tempFile);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("folderPath",filesPath)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

            JobExecution execution=jobLauncher.run(cvProcessingJob,jobParameters);

            return execution.getId();
    }
    public JobStatusResponse getJobsstatus(Long id){
        JobExecution execution=jobExplorer.getJobExecution(id);
        if (execution == null)throw new RuntimeException("Job tapılmadı: ID = " + id);
        return JobStatusResponse.maptoJobStatusResponse(execution);
    }
    public CvFilterResponse getCv(CvFilterRequest cvFilterRequest, PagesRequest pageRequest){
        Specification<CvRecord> cvRecords= CvSpecification.toSpecification(cvFilterRequest);
        Page<CvRecord> pages=cvRecordRepository.findAll(cvRecords, pageRequest.toPageable());
        return cvFilterResponseMapper.maptoCvFilterResponse(pages);
    }
    public String exportCvToCsv() {
        List<CvRecord> cvs = cvRecordRepository.findAllByCvStatus(CvStatus.PARSED_SUCCESS);
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Full Name,Experience Years,Description,Skills,Preferred Job Type,Location,CV Status\n");
        for (CvRecord cvRecord : cvs) {
            csvBuilder.append(cvRecord.getId()).append(",")
                    .append(cvRecord.getFullName() != null ? cvRecord.getFullName().replace(",", " ") : "").append(",")
                    .append(cvRecord.getExperienceYears() != null ? cvRecord.getExperienceYears() : "").append(",")
                    .append(cvRecord.getDescription() != null ? cvRecord.getDescription().replace(",", " ").
                            replace("\n", " ").replace("\r", " ") : "").append(",")
                    .append(cvRecord.getSkills() != null ? String.join(";", cvRecord.getSkills()) : "").append(",")
                    .append(cvRecord.getPreferredJobType() != null ? cvRecord.getPreferredJobType() : "").append(",")
                    .append(cvRecord.getLocation() != null ? cvRecord.getLocation().replace(",", " ") : "").append(",")
                    .append(cvRecord.getCvStatus()).append("\n");
        }
        return csvBuilder.toString();
    }
    private void validateZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Fayl boş ola bilməz!");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            throw new InvalidFileException("Yalnız .zip formatında arxiv faylı qəbul olunur!");
        }
    }
}
