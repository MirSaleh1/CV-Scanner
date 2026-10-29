package com.example.cvscanner.mapper;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.model.CvFilterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",uses = CvResponseMapper.class)
public interface CvFilterResponseMapper {
    @Mapping(target = "cvResponses", source = "content")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "currentPage", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "hasNext", expression = "java(page.hasNext())")
    CvFilterResponse  maptoCvFilterResponse(Page<CvRecord> page);
}
