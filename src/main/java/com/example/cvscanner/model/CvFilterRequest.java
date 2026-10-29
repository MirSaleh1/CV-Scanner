package com.example.cvscanner.model;


import java.util.List;

public record CvFilterRequest(
        List<String> skills,
        Integer minExperience,
        String preferredJobType,
        String location,
        CvStatus cvStatus
) {
}