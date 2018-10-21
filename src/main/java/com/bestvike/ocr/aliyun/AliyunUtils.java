package com.bestvike.ocr.aliyun;

import com.bestvike.ocr.aliyun.entity.AliyunOcrResponse;
import org.springframework.stereotype.Component;

/**
 * Created by 许崇雷 on 2018-10-21.
 */
@Component
public final class AliyunUtils {
    private static AliyunApi aliyunApi;

    public AliyunUtils(AliyunApi aliyunApiInstance) {
        aliyunApi = aliyunApiInstance;
    }

    //阿里云 ocr
    public static AliyunOcrResponse ocrAdvanced(byte[] imgData) {
        return aliyunApi.ocrAdvanced(imgData);
    }
}
