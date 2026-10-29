package com.example.cvscanner.entity;

import com.example.cvscanner.model.CvStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "cv_record")
public class CvRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private Integer experienceYears;
    private String description;
    @ElementCollection
    @CollectionTable(name = "cv_skills", joinColumns = @JoinColumn(name = "cv_record_id"))
    @Column(name = "skill")
    private List<String> skills;
    private String preferredJobType;
    private String location;
    private String storedFileLocation;
    @Enumerated(EnumType.STRING)
    private CvStatus cvStatus;

}
