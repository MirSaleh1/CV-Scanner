package com.example.cvscanner.mapper;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.model.CvResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CvResponseMapper {
    CvResponse toCvResponse(CvRecord cvRecord);
}
