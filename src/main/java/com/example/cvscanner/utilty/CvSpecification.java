package com.example.cvscanner.utilty;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.model.CvFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public interface CvSpecification {
    static Specification<CvRecord> toSpecification(CvFilterRequest cvFilterRequest) {
        return (root, query, cb) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (cvFilterRequest.preferredJobType() != null && !cvFilterRequest.preferredJobType().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("preferredJobType")), "%" + cvFilterRequest.preferredJobType().toLowerCase() + "%"));
            }
            if (cvFilterRequest.minExperience() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experienceYears"), cvFilterRequest.minExperience()));
            }
            if (cvFilterRequest.location() != null && !cvFilterRequest.location().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + cvFilterRequest.location().toLowerCase() + "%"));
            }
            if (cvFilterRequest.skills() != null && !cvFilterRequest.skills().isEmpty()) {
                predicates.add(root.join("skills").in(cvFilterRequest.skills()));
                query.distinct(true);
            }
            if (cvFilterRequest.cvStatus() != null) {
                predicates.add(cb.equal(root.get("cvStatus"), cvFilterRequest.cvStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
