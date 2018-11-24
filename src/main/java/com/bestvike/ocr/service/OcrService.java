package com.bestvike.ocr.service;

import com.bestvike.ocr.baidu.BaiduApi;
import com.bestvike.ocr.baidu.entity.BaiduOcrResult;
import com.bestvike.ocr.baidu.entity.BaiduResponse;
import com.bestvike.ocr.util.HtmlUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 许崇雷 on 2018-10-23.
 */
@Service
public class OcrService {
    @Autowired
    private BaiduApi baiduApi;

    public  String preview(byte[] data){
        final BaiduResponse<BaiduOcrResult> baiduResponse = this.baiduApi.ocr(data);
        final BaiduOcrResult ocrResult = baiduResponse.getData();





        StringBuilder builder = new StringBuilder(1000);
        builder.append("<head><meta charset=\"UTF-8\"><style>body{font-family:微软雅黑;}table{margin-top:10px;border-collapse:collapse;border:1px solid #aaa;}table th{vertical-align:baseline;padding:6px 15px 6px 6px;background-color:#d5d5d5;border:1px solid #aaa;word-break:keep-all;white-space:nowrap;text-align:left;}table td{vertical-align:text-top;padding:6px 15px 6px 6px;background-color:#efefef;border:1px solid #aaa;word-break:break-all;white-space:pre-wrap;}</style></head>");
        builder.append("<body>\n");
        builder.append("<img src='data:image/").append(this.format).append(";base64,").append(Base64.encodeBase64String(this.buffer)).append("' />\n");
        builder.append("<br>\n");
        builder.append("<table border=\"1\" cellPadding=\"5\" cellspacing=\"0\">\n");
        for (int rowIndex = 0; rowIndex < rcRowCount; rowIndex++) {
            builder.append("  <tr>\n");
            for (int colIndex = 0; colIndex < rcColCount; colIndex++) {
                String word = table[rowIndex][colIndex];
                word = word == null ? StringUtils.EMPTY : HtmlUtils.htmlEscape(word);
                builder.append("    <td>").append(word).append("</td>\n");
            }
            builder.append("  </tr>\n");
        }
        builder.append("</table>\n");
        builder.append("</body>");
        return builder.toString();
    }
}
