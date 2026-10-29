package com.example.cvscanner.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

public record PagesRequest (
       @Min(value = 0, message = "Səhifə nömrəsi 0-dan kiçik ola bilməz.")
       Integer page,
       @Max(value = 100, message = "Səhifə ölçüsü 100-dən çox ola bilməz.")
       Integer size,
       String sortBy,
       String sortDirection) {

       public PagesRequest {
              if (page == null) page = 0;
              if (size == null || size < 1) size = 10;
              if (sortBy == null || sortBy.isBlank()) sortBy = "id";
              if (sortDirection == null || sortDirection.isBlank()) sortDirection = "DESC";
       }
       public Pageable toPageable() {
              Sort sort = sortDirection.equalsIgnoreCase("ASC")
                      ? Sort.by(sortBy).ascending()
                      : Sort.by(sortBy).descending();
              return PageRequest.of(page, size, sort);
       }
}