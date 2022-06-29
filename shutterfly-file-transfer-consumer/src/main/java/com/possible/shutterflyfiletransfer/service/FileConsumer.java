package com.possible.shutterflyfiletransfer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

@Service
@Slf4j
public class FileConsumer {

    private static final String UPLOAD_FILE_PATH = "C:\\Users\\olayo\\OneDrive\\Documents\\new_upload\\";

    @Bean
    public Consumer<byte[]> processFile() {
        return FileConsumer::writeBytesToFile;
    }

    private static void writeBytesToFile(byte[] bFile) {
        Path path = Paths.get(UPLOAD_FILE_PATH+"\\"+"test.pdf");

        File file = new File("C:\\Users\\olayo\\Documents", "test2.pdf");

        try(OutputStream os = new FileOutputStream(file) ) {
            Files.write(path, bFile);
            os.write(bFile);
            log.info("Write bytes to file.");
        } catch (Exception e) {
            log.error("An Error occurred", e);
        }

    }
}
