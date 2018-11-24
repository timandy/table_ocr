package com.bestvike.ocr.baidu;

import com.baidu.aip.ocr.AipOcr;
import com.bestvike.ocr.baidu.entity.BaiduOcrResult;
import com.bestvike.ocr.baidu.entity.BaiduResponse;
import com.bestvike.ocr.reflect.GenericType;
import com.bestvike.ocr.util.JsonSerializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by 许崇雷 on 2018-10-22.
 */
@Component
public final class BaiduApi {
    private final BaiduConfiuration baiduConfiuration;
    private final AipOcr client;


    @Autowired
    public BaiduApi(BaiduConfiuration baiduConfiuration) {
        this.baiduConfiuration = baiduConfiuration;
        this.client = new AipOcr(baiduConfiuration.getAppId(), baiduConfiuration.getApiKey(), baiduConfiuration.getSecretKey());

    }

    //ocr 识别
    public BaiduResponse<BaiduOcrResult> ocr(byte[] img) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        // 参数为本地图片二进制数组
        JSONObject jsonObject = this.client.custom(img, this.baiduConfiuration.getTemplateSign(), options);
        if (jsonObject == null)
            throw new RuntimeException("百度 ocr 返回 null");
        String json = jsonObject.toString();
        BaiduResponse<BaiduOcrResult> ocrResponse = JsonSerializer.deserialize(json, new GenericType<BaiduResponse<BaiduOcrResult>>() {
        });
        if (!ocrResponse.isSuccess())
            throw new RuntimeException("百度 ocr 错误:" + ocrResponse.getError_msg());
        return ocrResponse;
    }
}
