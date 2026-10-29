package com.example.cvscanner.repository;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.model.CvStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CvRecordRepository extends JpaRepository<CvRecord, Long>, JpaSpecificationExecutor<CvRecord> {
    @EntityGraph(attributePaths = {"skills"})
    List<CvRecord> findAllByCvStatus(CvStatus cvStatus);
}
