package com.bestvike.ocr.controller;

import com.bestvike.ocr.service.OcrService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Controller
public class OcrController {

    @Autowired
    private OcrService ocrService;

    private static InputStream getDemoStream() throws IOException {
        return new ClassPathResource("demo_table.jpg").getInputStream();
    }

    @GetMapping("/ocr/demo")
    public void ocrDemo(HttpServletResponse response) throws IOException {
        try (InputStream inputStream = getDemoStream()) {
            String html = this.ocrService.ocrTable(inputStream);
            final ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.write(html, outputStream, "utf-8");
        }
    }

    @GetMapping("/ocr/preview")
    public void buhuixieqianduan(HttpServletResponse response) throws IOException {
        response.setContentType("image/jpg");
        try (InputStream inputStream = getDemoStream()) {
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }
}
