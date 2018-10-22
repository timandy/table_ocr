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
    private byte[] getBytes(MultipartFile file) throws IOException {
        if (file == null)
            throw new RuntimeException("图片不能为空");
        byte[] bytes = file.getBytes();
        if (bytes.length == 0)
            throw new RuntimeException("图片长度不能为 0");
        return bytes;
    }

    @GetMapping("/")
    public String index() {
        return "index.htm";
    }

    @PostMapping("/preview")
    public void ocr(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        byte[] bytes = this.getBytes(file);
        this.preview(bytes, response);
    }

    @PostMapping("/excel")
    public void excel(@RequestParam MultipartFile file, HttpServletResponse response) throws IOException {
        byte[] bytes = this.getBytes(file);
        GridImage image = new GridImage(bytes);
        response.setHeader("Content-Disposition", "attachment;filename=demo.xls");
        response.setContentType("application/force-download");//应用程序强制下载
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            image.saveAsExcel(outputStream, "demo");
        }
    }

    @GetMapping("/demo")
    public void ocrDemo(HttpServletResponse response) throws IOException {
        try (InputStream demoStream = new ClassPathResource("static/img/demo_table.jpg").getInputStream()) {
            this.preview(IOUtils.toByteArray(demoStream), response);
        }
    }

    private void preview(byte[] bytes, HttpServletResponse response) throws IOException {
        GridImage image = new GridImage(bytes);
        String html = image.preview();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            IOUtils.write(html, outputStream, "utf-8");
        }
    }
}
