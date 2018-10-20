package com.bestvike.ocr.aliyun;

import com.bestvike.ocr.aliyun.entity.AliyunOcrRequest;
import com.bestvike.ocr.aliyun.entity.AliyunOcrResponse;
import com.bestvike.ocr.util.RestTemplateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
public final class AliyunOcr {
    //服务购买  https://market.aliyun.com/products/57124001/cmapi028554.html?spm=5176.182739.954606.2.69111d8a49wk2V#sku=yuncode2255400000
    private static final String DOMAIN = "https://ocrapi-advanced.taobao.com";
    private static final String appCode = "56905a5c43ff4d07a44d301a9d5bce46";//个人的 appcode 请勿用于生产

    public static AliyunOcrResponse ocr(byte[] imgData) {
        String img = Base64Utils.encodeToString(imgData);
        AliyunOcrRequest request = new AliyunOcrRequest();
        request.setImg(img);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + appCode);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        AliyunOcrResponse ocrResponse = RestTemplateUtils.post(DOMAIN + "/ocrservice/advanced", request, headers, AliyunOcrResponse.class);
        if (!ocrResponse.isSuccess())
            throw new RuntimeException("调用阿里云失败:" + ocrResponse.getError_msg());
        return ocrResponse;
    }
}
