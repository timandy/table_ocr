package com.bestvike.ocr.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 许崇雷 on 2018-10-21.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.aliyun")
public class AliyunConfiguration {
    private String appCode;
}
