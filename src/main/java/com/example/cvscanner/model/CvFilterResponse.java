package com.example.cvscanner.model;

import java.util.List;

public record CvFilterResponse (List<CvResponse>cvResponses,
                               long totalElements,
                               int totalPages,
                               int currentPage,
                               int pageSize,
                               boolean hasNext){
}
