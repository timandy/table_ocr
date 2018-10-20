package com.bestvike.ocr.aliyun.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliyunOcrResponse extends AliyunResponse {
    private String sid;
    private String prism_version;
    private int prism_wnum;
    private List<AliyunOcrWordInfo> prism_wordsInfo;

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && this.prism_wordsInfo != null;
    }
}
