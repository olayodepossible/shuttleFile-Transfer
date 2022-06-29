package com.possible.shutterflyfiletransfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class FileTransferService {
    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    Environment env;

    @Bean
    public Supplier<String> output(){
        return () -> "Hello from Supplier";
    }

    @Bean
    public Function<Byte[], Message<Byte[]>> destinationAsPayload() {

        return file -> MessageBuilder.withPayload(file).setHeader(Objects.requireNonNull(env.getProperty("destination")), file).build();
    }

    public void publishFile(String topic, MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            streamBridge.send(topic, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
