package com.bestvike.ocr.baidu.entity;

import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 许崇雷 on 2018-10-23.
 */
@Data
public class BaiduOcrResult {
    private List<BaiduOcrWord> ret;
    private String templateSign;
    private int scores;
    private boolean isStructured;
    private String logId;

    private Map<String, String> words;

    private Map<String, String> getWords() {
        if (this.ret == null)
            throw new RuntimeException("百度 ocr 未返回 ret 字段");

        if (this.words == null) {
            this.words = new HashMap<>();
            for (BaiduOcrWord baiduOcrWord : this.ret)
                this.words.put(baiduOcrWord.getWord_name(), baiduOcrWord.getWord());
        }
        return this.words;
    }

    //获取字符串字段的值
    public String getValue(String key) {
        Assert.hasLength(key, "key can not be null");
        Map<String, String> words = this.getWords();
        return words.get(key);
    }

    //获取表格单元格的值
    public String getCell(String tableName, int rowIndex, String columnName) {
        Assert.hasLength(tableName, "tableName can not be null");
        Assert.isTrue(rowIndex >= 0, "rowIndex must greater than zero.");
        Assert.hasLength(columnName, "columnName can not be empty");
        String key = String.format("%s#%d#%s", tableName, rowIndex, columnName);
        return this.getValue(key);
    }

}
