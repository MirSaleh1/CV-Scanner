package com.example.cvscanner.service;

import com.example.cvscanner.entity.CvRecord;
import com.example.cvscanner.model.CvStatus;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvParserService {

    private final Tika tika = new Tika();
    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "Java", "Spring Boot", "Spring", "Python", "Docker", "Kubernetes",
            "PostgreSQL", "MySQL", "React", "Angular", "JavaScript", "TypeScript",
            "AWS", "Git", "Microservices", "REST API", "Linux", "Kafka"
    );

    public CvRecord parseToCvRecord(File file) {
        CvRecord cvRecord = new CvRecord();
        cvRecord.setStoredFileLocation(file.getAbsolutePath());
        try {
            String content=tika.parseToString(file);
            if(content==null||content.isBlank()){
                cvRecord.setCvStatus(CvStatus.PARSE_FAILED);
                return cvRecord;
            }
            cvRecord.setFullName(extractFullName(content,file.getName()));
            cvRecord.setSkills(extractSkills(content));
            cvRecord.setPreferredJobType(extractJobType(content));
            cvRecord.setExperienceYears(extractExperience(content));
            cvRecord.setDescription(extractSummary(content));
            cvRecord.setLocation(extractLocation(content));
            cvRecord.setCvStatus(CvStatus.PARSED_SUCCESS);
        } catch (TikaException e) {
            cvRecord.setCvStatus(CvStatus.PARSE_FAILED);
        } catch (IOException e) {
            cvRecord.setCvStatus(CvStatus.PARSE_FAILED);
        }
        return cvRecord;
    }

    private String extractFullName(String content,String fileName) {
        String[] lines = content.split("\\r?\\n");
        for(String line : lines) {
            line = line.trim();
            if(!line.isBlank() && line.length()<50 && line.matches("^[a-zA-Z\\s]+$")){
                return line;
            }
        }
        return fileName.replace("(?i)\\.(pdf|docx|doc)", "").replace("_", " ");
    }
    private List<String> extractSkills(String content) {
        List<String> skills = new ArrayList<>();
        String lowerContent = content.toLowerCase();
        for(String skill : KNOWN_SKILLS){
            if (lowerContent.contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }
        return skills;
    }
    private String extractJobType(String content) {
        String lowerContent = content.toLowerCase();
        if (lowerContent.contains("remote")) return "Remote";
        if (lowerContent.contains("hybrid")) return "Hybrid";
        if (lowerContent.contains("on-site") || lowerContent.contains("onsite")) return "On-site";
        return "Not Specified";
    }
    private Integer extractExperience(String content) {
        Pattern pattern = Pattern.compile("(?i)\\b(\\d{1,2})\\+?\\s*(?:years?|yrs|il)\\b");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    private String extractSummary(String content) {
        Pattern pattern = Pattern.compile("(?i)(?:summary|about me|profile|haqqında)\\s*[:\\-]?\\s*(.+?)(?=\\n[A-Z]{3,}|\\Z)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No summary provided";
    }
    private String extractLocation(String content) {
        List<String> cities = Arrays.asList("Baku", "London", "Berlin", "New York", "Istanbul", "Remote");
        for (String city : cities) {
            if (content.toLowerCase().contains(city.toLowerCase())) {
                return city;
            }
        }
        return "Unknown";
    }
}
