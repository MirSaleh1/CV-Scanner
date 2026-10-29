package com.example.cvscanner.controller;

import com.example.cvscanner.exception.InvalidFileException;
import com.example.cvscanner.model.JobStatusResponse;
import com.example.cvscanner.service.CvScannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Batch Jobs", description = "CV arxivinin yüklənməsi və Batch prosesinin izlənməsi üçün API-lar")
public class BatchJobController {

    private final CvScannerService cvScannerService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "CV arxivini (.zip) yüklə və emala başla")
    public ResponseEntity<Map<String, Object>> uploadCvArchive(@RequestParam("file") MultipartFile file) throws Exception {
        Long jobId = cvScannerService.uploadAndExtractCvArchive(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "jobId", jobId,
                "message", "CV arxivi uğurla qəbul edildi və Batch emal prosesi başladı"
        ));
    }
    @GetMapping("/{id}/status")
    @Operation(summary = "Batch Job-un cari statusunu əldə et")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable Long id) {
        JobStatusResponse statusResponse = cvScannerService.getJobsstatus(id);
        return ResponseEntity.ok(statusResponse);
    }
}
