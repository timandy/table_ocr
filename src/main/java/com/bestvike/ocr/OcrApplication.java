package com.bestvike.ocr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class OcrApplication {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(OcrApplication.class, args);
    }
}
