package com.example.cvscanner.controller;

import com.example.cvscanner.model.CvFilterRequest;
import com.example.cvscanner.model.CvFilterResponse;
import com.example.cvscanner.model.PagesRequest;
import com.example.cvscanner.service.CvScannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidates", description = "Namizədlərin axtarışı, filterlənməsi və CSV ixracı üçün API-lar")
public class CandidateController {

    private final CvScannerService cvScannerService;

    @GetMapping
    @Operation(summary = "Namizədləri filtrlə və səhifələmə ilə gətir")
    public ResponseEntity<CvFilterResponse> getCandidates(
            @ModelAttribute CvFilterRequest filterRequest,
            @Valid @ModelAttribute PagesRequest pageRequest) {
        CvFilterResponse response = cvScannerService.getCv(filterRequest, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @Operation(summary = "Uğurla emal edilmiş bütün namizədləri CSV faylı kimi yüklə")
    public ResponseEntity<String> exportCandidatesToCsv() {
        String csvData = cvScannerService.exportCvToCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=candidates.csv")
                .body(csvData);
    }
}
