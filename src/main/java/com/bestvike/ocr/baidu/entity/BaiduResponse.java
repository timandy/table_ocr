package com.bestvike.ocr.baidu.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by 许崇雷 on 2018-10-23.
 */
@Data
public class BaiduResponse<T> {
    private String error_code;
    private String error_msg;
    private T data;

    //是否成功
    public boolean isSuccess() {
        return StringUtils.equals(this.error_code, "0");
    }
}
