package com.example.cvscanner.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.springframework.batch.item.ItemReader;
import java.util.List;
import java.util.stream.Stream;

public class CvReader implements ItemReader<File> {

    private final Iterator<File> files;

    public CvReader(String directoryPath) {
        List<File> fileList;
        try (Stream<Path> stream = Files.walk(Paths.get(directoryPath))) {
            fileList=stream.filter(Files::isRegularFile).
                    filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".pdf") || fileName.endsWith(".docx") || fileName.endsWith(".doc");
                    }).map(Path::toFile).toList();
        } catch (IOException e) {
            fileList=List.of();
            throw new RuntimeException(e);
        }
        this.files = fileList.iterator();

    }
    @Override
    public File read() throws Exception {
        if (files!=null && files.hasNext()) {
            return files.next();
        }
        return null;
    }
}
