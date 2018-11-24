package com.bestvike.ocr.baidu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 许崇雷 on 2018-10-22.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.baidu")
public class BaiduConfiuration {
    private String appId;
    private String apiKey;
    private String secretKey;
    private  String templateSign;
}
