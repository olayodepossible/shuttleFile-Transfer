package com.possible.shutterflyfiletransfer.controller;

import com.possible.shutterflyfiletransfer.model.ProducerResponse;
import com.possible.shutterflyfiletransfer.service.FileTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/report-api")
public class FileProducerController {

    private static final String MESSAGE_FORMAT = "Locally Uploaded file: %s is successfully written to Kafka";
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private BinderAwareChannelResolver resolver;
    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    Environment env;

    @PostMapping("/static-destination")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ProducerResponse> delegateToSupplier(@RequestBody String body) {

        streamBridge.send(env.getProperty("destination"), body);
        return new ResponseEntity<>(ProducerResponse.builder()
                .status("Success")
                .message("static destination").build(), HttpStatus.OK);

    }

    @PostMapping("runtime-destination/{destination}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ProducerResponse> dynamicDestination(@PathVariable String destination, @RequestBody MultipartFile file) {
        log.info("Sending...");
        try {
            byte[] fileData = file.getBytes();
            streamBridge.send(destination, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ProducerResponse.builder()
                .status("Success")
                .message(String.format(MESSAGE_FORMAT, file.getOriginalFilename())).build(), HttpStatus.OK);
    }

    @PostMapping("/{topic}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ProducerResponse> dynamicTopic(@PathVariable String topic, @RequestBody MultipartFile file) {
        log.info("receive file...");
        fileTransferService.publishFile(topic, file);
        return new ResponseEntity<>(ProducerResponse.builder()
                .status("Success")
                .message(String.format(MESSAGE_FORMAT, file.getOriginalFilename())).build(), HttpStatus.OK);
    }


    @PostMapping("/{target}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ProducerResponse> send(@RequestBody MultipartFile file, @PathVariable("target") String target){
        try {
            byte[] fileData = file.getBytes();
            resolver.resolveDestination(target).send(new GenericMessage<>(fileData));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(ProducerResponse.builder()
                .status("Success")
                .message(String.format(MESSAGE_FORMAT, file.getOriginalFilename())).build(), HttpStatus.OK);
    }


}
