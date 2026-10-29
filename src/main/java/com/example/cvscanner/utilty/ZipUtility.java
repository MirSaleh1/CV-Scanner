package com.example.cvscanner.utilty;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipUtility {

    public String unzip(Path storageDirectory,Path destinationDirectory) throws IOException {
        Path newDirectory=destinationDirectory.resolve(UUID.randomUUID().toString());
        Files.createDirectories(newDirectory);
        try(ZipInputStream zipInputStream=new ZipInputStream(Files.newInputStream(storageDirectory))){
            ZipEntry zipEntry=zipInputStream.getNextEntry();
            while(zipEntry!=null){
                Path newPath=newDirectory.resolve(zipEntry.getName());

                if (!newPath.normalize().startsWith(newDirectory.normalize())) {
                    throw new IOException("Təhlükəli zip faylı: " + zipEntry.getName());
                }
                if(zipEntry.isDirectory()){
                    Files.createDirectories(newPath);
                }else {
                    if(newPath.getParent()!=null){
                        Files.createDirectories(newPath.getParent());
                    }
                    Files.copy(zipInputStream,newPath);
                }
                zipEntry=zipInputStream.getNextEntry();
            }
        }
        return newDirectory.toString();

    }
}
