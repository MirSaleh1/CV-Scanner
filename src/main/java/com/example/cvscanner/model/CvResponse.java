package com.example.cvscanner.model;

import com.example.cvscanner.entity.CvRecord;

import java.util.List;

public record CvResponse(
        Long id,
        String fullName,
        Integer experienceYears,
        String description,
        List<String> skills,
        String preferredJobType,
        String location,
        CvStatus cvStatus
) {
}