package com.bestvike.ocr.aliyun;

import com.bestvike.ocr.aliyun.entity.AliyunOcrRequest;
import com.bestvike.ocr.aliyun.entity.AliyunOcrResponse;
import com.bestvike.ocr.util.RestTemplateUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Component
public final class AliyunApi {
    //服务购买 https://market.aliyun.com/products/57124001/cmapi028554.html?spm=5176.182739.954606.2.69111d8a49wk2V#sku=yuncode2255400000
    private static final String DOMAIN = "https://ocrapi-advanced.taobao.com";
    @Autowired
    private AliyunConfiguration aliyunConfiguration;

    //阿里云 ocr
    public AliyunOcrResponse ocrAdvanced(byte[] imgData) {
        String img = Base64.encodeBase64String(imgData);
        AliyunOcrRequest request = new AliyunOcrRequest();
        request.setImg(img);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + this.aliyunConfiguration.getAppCode());
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        AliyunOcrResponse ocrResponse = RestTemplateUtils.post(DOMAIN + "/ocrservice/advanced", request, headers, AliyunOcrResponse.class);
        if (!ocrResponse.isSuccess())
            throw new RuntimeException("调用阿里云失败:" + ocrResponse.getError_msg());
        return ocrResponse;
    }
}
