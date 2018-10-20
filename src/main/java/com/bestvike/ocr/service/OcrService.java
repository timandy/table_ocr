package com.bestvike.ocr.service;


import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.Linq;
import com.bestvike.ocr.aliyun.AliyunOcr;
import com.bestvike.ocr.aliyun.entity.AliyunOcrResponse;
import com.bestvike.ocr.aliyun.entity.AliyunOcrWordInfo;
import com.bestvike.ocr.util.GridImage;
import com.bestvike.ocr.util.HtmlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
@Service
public final class OcrService {

    //转换为 html 以便显示
    private static String toHtml(String[][] table, int rowCount, int colCount) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append("<body>\n");
        builder.append("<img src='/ocr/preview' />\n");
        builder.append("<br>\n");
        builder.append("<table border=\"1\" cellPadding=\"5\" cellspacing=\"0\">\n");
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            builder.append("  <tr>\n");
            for (int colIndex = 0; colIndex < colCount; colIndex++) {
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

    //识别表格
    public String ocrTable(InputStream inputStream) throws IOException {
        //图片识别为单元格
        byte[] bytes = IOUtils.toByteArray(inputStream);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
        GridImage gridImage = new GridImage(bufferedImage);

        //调用阿里云接口识别
        AliyunOcrResponse response = AliyunOcr.ocr(bytes);
        IEnumerable<AliyunOcrWordInfo> prism_wordsInfo = Linq.asEnumerable(response.getPrism_wordsInfo());

        //识别结果派分到单元格(单纯写入 excel 可以省略 table 中间变量直接写入 excel)
        Rectangle[][] grid = gridImage.getGrid();
        int rowCount = gridImage.getGridRowCount();
        int colCount = gridImage.getGridColumnCount();
        String[][] table = new String[rowCount][colCount];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            for (int colIndex = 0; colIndex < colCount; colIndex++) {
                final int fRowIndex = rowIndex;
                final int fColIndex = colIndex;
                final AliyunOcrWordInfo wordInfo = prism_wordsInfo
                        .where(a -> grid[fRowIndex][fColIndex].contains(a.getRectangle()))
                        .maxByNull(a -> a.getRectangle().width * a.getRectangle().height);//取面积最大的
                if (wordInfo == null)
                    continue;
                table[fRowIndex][fColIndex] = StringUtils.replaceChars(wordInfo.getWord(), '，', ',');
            }
        }

        return toHtml(table, rowCount, colCount);
    }
}
