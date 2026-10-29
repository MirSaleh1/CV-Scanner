package com.example.cvscanner.batch;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.service.CvParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class CvItemProcessor implements ItemProcessor<File, CvRecord> {
    private final CvParserService cvParserService;

    @Override
    public CvRecord process(File file){
        return cvParserService.parseToCvRecord(file);
    }
}
