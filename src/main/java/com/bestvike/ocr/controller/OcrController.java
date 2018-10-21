package com.bestvike.ocr.controller;

import com.bestvike.ocr.util.GridImage;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Controller
public class OcrController {
    @GetMapping("/")
    public String index() {
        return "index.htm";
    }

    @PostMapping("/")
    public void ocr(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (file == null)
            throw new RuntimeException("图片不能为空");
        this.doOcr(file.getBytes(), response);
    }

    @GetMapping("/demo")
    public void ocrDemo(HttpServletResponse response) throws IOException {
        final InputStream demoStream = new ClassPathResource("static/img/demo_table.jpg").getInputStream();
        this.doOcr(IOUtils.toByteArray(demoStream), response);
    }

    private void doOcr(byte[] bytes, HttpServletResponse response) throws IOException {
        if (bytes.length == 0)
            throw new RuntimeException("图片长度不能为 0");
        GridImage image = new GridImage(bytes);
        String html = image.preview();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            IOUtils.write(html, outputStream, "utf-8");
        }
    }
}
