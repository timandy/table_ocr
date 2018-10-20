package com.bestvike.ocr.aliyun.entity;

import lombok.Data;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Data
public class AliyunResponse {
    public int error_code;
    public String error_msg;

    public boolean isSuccess() {
        return this.error_code == 0;
    }
}
