package com.bestvike.ocr.aliyun.entity;

import lombok.Data;

import java.awt.*;
import java.util.List;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Data
public class AliyunOcrWordInfo {
    private List<AliyunOcrPoint> pos;
    private String word;
    private Rectangle rectangle;

    public Rectangle getRectangle() {
        return this.rectangle == null
                ? (this.rectangle = new Rectangle(this.pos.get(0).getX(), this.pos.get(0).getY(), this.pos.get(1).getX() - this.pos.get(0).getX(), this.pos.get(2).getY() - this.pos.get(0).getY()))
                : this.rectangle;
    }
}
