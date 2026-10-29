package com.example.cvscanner.batch;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.repository.CvRecordRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CvItemWriter implements ItemWriter<CvRecord> {

    private final CvRecordRepository cvRecordRepository;
    @Override
    public void write(Chunk<? extends CvRecord> chunk) throws Exception {
        cvRecordRepository.saveAll(chunk.getItems());
    }
}
