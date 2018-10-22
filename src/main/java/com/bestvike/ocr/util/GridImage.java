package com.bestvike.ocr.util;

import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.Linq;
import com.bestvike.ocr.aliyun.AliyunUtils;
import com.bestvike.ocr.aliyun.entity.AliyunOcrResponse;
import com.bestvike.ocr.aliyun.entity.AliyunOcrWordInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
public final class GridImage {
    private static final int MIN_RED = 200;
    private static final int MAX_GREEN = 255 - MIN_RED;
    private static final int MAX_BLUE = 255 - MIN_RED;
    private static final int MIN_DISTANCE = 5;
    //图片数据
    private final byte[] buffer;//图片文件二进制数据
    private final String format;//图片扩展名
    private final BufferedImage image;//内存图
    //结果数据
    private Rectangle[][] grid;//图片切分的矩形
    private String[][] table;//识别结果内容
    private int gridRowCount;//行数
    private int gridColumnCount;//列数

    /**
     * 构造函数
     */
    public GridImage(byte[] buffer) throws IOException {
        Assert.notNull(buffer, "buffer cannot be null.");
        Assert.isTrue(buffer.length > 4, "length of buffer must greater than 4.");
        this.buffer = buffer;
        this.format = getImageFormat(buffer);
        this.image = ImageIO.read(new ByteArrayInputStream(buffer));
        this.init();
    }

    /**
     * 获取图片真实扩展名
     */
    private static String getImageFormat(byte[] data) {
        byte[] head = new byte[4];
        System.arraycopy(data, 0, head, 0, head.length);
        String type = Hex.encodeHexString(head).toUpperCase();
        if (type.contains("FFD8FF")) {
            return "jpg";
        } else if (type.contains("89504E47")) {
            return "png";
        } else if (type.contains("424D")) {
            return "bmp";
        }
        throw new RuntimeException("只支持 jpg,png,bmp 格式图片");
    }

    /**
     * 计算两条直线的交点
     *
     * @param firstBegin  L1的点1坐标
     * @param firstEnd    L1的点2坐标
     * @param secondBegin L2的点1坐标
     * @param secondEnd   L2的点2坐标
     * @return 交点坐标
     */
    private static Point getIntersection(Point firstBegin, Point firstEnd, Point secondBegin, Point secondEnd) {
        /*
         * L1，L2都存在斜率的情况：
         * 直线方程L1: ( y - y1 ) / ( y2 - y1 ) = ( x - x1 ) / ( x2 - x1 )
         * => y = [ ( y2 - y1 ) / ( x2 - x1 ) ]( x - x1 ) + y1
         * 令 a = ( y2 - y1 ) / ( x2 - x1 )
         * 有 y = a * x - a * x1 + y1   .........1
         * 直线方程L2: ( y - y3 ) / ( y4 - y3 ) = ( x - x3 ) / ( x4 - x3 )
         * 令 b = ( y4 - y3 ) / ( x4 - x3 )
         * 有 y = b * x - b * x3 + y3 ..........2
         *
         * 如果 a = b，则两直线平等，否则， 联解方程 1,2，得:
         * x = ( a * x1 - b * x3 - y1 + y3 ) / ( a - b )
         * y = a * x - a * x1 + y1
         *
         * L1存在斜率, L2平行Y轴的情况：
         * x = x3
         * y = a * x3 - a * x1 + y1
         *
         * L1 平行Y轴，L2存在斜率的情况：
         * x = x1
         * y = b * x - b * x3 + y3
         *
         * L1与L2都平行Y轴的情况：
         * 如果 x1 = x3，那么L1与L2重合，否则平等
         *
         */
        float a = 0, b = 0;
        int state = 0;
        if (firstBegin.x != firstEnd.x) {
            a = (firstEnd.y - firstBegin.y) / (float) (firstEnd.x - firstBegin.x);
            state |= 1;
        }
        if (secondBegin.x != secondEnd.x) {
            b = (secondEnd.y - secondBegin.y) / (float) (secondEnd.x - secondBegin.x);
            state |= 2;
        }
        switch (state) {
            case 0: //L1与L2都平行Y轴
            {
                if (firstBegin.x == secondBegin.x)
                    throw new RuntimeException("两条直线互相重合，且平行于Y轴，无法计算交点。");
                else
                    throw new RuntimeException("两条直线互相平行，且平行于Y轴，无法计算交点。");
            }
            case 1: //L1存在斜率, L2平行Y轴
            {
                int x = secondBegin.x;
                int y = Math.round((firstBegin.x - x) * (-a) + firstBegin.y);
                return new Point(x, y);
            }
            case 2: //L1 平行Y轴，L2存在斜率
            {
                int x = firstBegin.x;
                int y = Math.round((secondBegin.x - x) * (-b) + secondBegin.y);
                return new Point(x, y);
            }
            case 3: //L1，L2都存在斜率
            {
                if (a == b)
                    throw new RuntimeException("两条直线平行或重合，无法计算交点。");
                int x = Math.round((a * firstBegin.x - b * secondBegin.x - firstBegin.y + secondBegin.y) / (a - b));
                int y = Math.round(a * x - a * firstBegin.x + firstBegin.y);
                return new Point(x, y);
            }
            default:
                throw new RuntimeException("不可能发生的情况");
        }
    }

    /**
     * 获取切分的单元格
     */
    public Rectangle[][] getGrid() {
        return this.grid;
    }

    /**
     * 获取单元格行数
     */
    public int getGridRowCount() {
        return this.gridRowCount;
    }

    /**
     * 获取单元格列数
     */
    public int getGridColumnCount() {
        return this.gridColumnCount;
    }

    /**
     * 初始化单元格
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void init() {
        //识别四个边红色点
        final int right = this.image.getWidth() - 1;
        final int bottom = this.image.getHeight() - 1;

        //识别竖线(从左到右)
        List<Point> topPoints = new ArrayList<>();
        List<Point> bottomPoints = new ArrayList<>();
        int lastTop = 0;
        int lastBottom = 0;
        topPoints.add(new Point(0, 0));
        bottomPoints.add(new Point(0, bottom));
        for (int x = 0; x <= right; x++) {
            Color topColor = new Color(this.image.getRGB(x, 0));
            if (topColor.getRed() > MIN_RED && topColor.getGreen() < MAX_GREEN && topColor.getBlue() < MAX_BLUE && x - lastTop > MIN_DISTANCE)
                topPoints.add(new Point(lastTop = x, 0));

            Color bottomColor = new Color(this.image.getRGB(x, bottom));
            if (bottomColor.getRed() > MIN_RED && bottomColor.getGreen() < MAX_GREEN && bottomColor.getBlue() < MAX_BLUE && x - lastBottom > MIN_DISTANCE)
                bottomPoints.add(new Point(lastBottom = x, bottom));
        }
        if (topPoints.size() != bottomPoints.size())
            throw new RuntimeException("竖线识别失败");
        topPoints.add(new Point(right, 0));
        bottomPoints.add(new Point(right, bottom));

        //识别横线(从上到下)
        List<Point> leftPoints = new ArrayList<>();
        List<Point> rightPoints = new ArrayList<>();
        int lastLeft = 0;
        int lastRight = 0;
        leftPoints.add(new Point(0, 0));
        rightPoints.add(new Point(right, 0));
        for (int y = 0; y <= bottom; y++) {
            Color leftColor = new Color(this.image.getRGB(0, y));
            if (leftColor.getRed() > MIN_RED && leftColor.getGreen() < MAX_GREEN && leftColor.getBlue() < MAX_BLUE && y - lastLeft > MIN_DISTANCE)
                leftPoints.add(new Point(0, lastLeft = y));

            Color rightColor = new Color(this.image.getRGB(right, y));
            if (rightColor.getRed() > MIN_RED && rightColor.getGreen() < MAX_GREEN && rightColor.getBlue() < MAX_BLUE && y - lastRight > MIN_DISTANCE)
                rightPoints.add(new Point(right, lastRight = y));
        }
        if (leftPoints.size() != rightPoints.size())
            throw new RuntimeException("横线识别失败");
        leftPoints.add(new Point(0, bottom));
        rightPoints.add(new Point(right, bottom));

        //计算所有的交点
        int rowCount = leftPoints.size();
        int colCount = topPoints.size();
        Point[][] points = new Point[rowCount][colCount];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            for (int colIndex = 0; colIndex < colCount; colIndex++)
                points[rowIndex][colIndex] = getIntersection(leftPoints.get(rowIndex), rightPoints.get(rowIndex), topPoints.get(colIndex), bottomPoints.get(colIndex));
        }

        //网格划分
        int rcRowCount = rowCount - 1;
        int rcColCount = colCount - 1;
        Rectangle[][] grid = new Rectangle[rcRowCount][rcColCount];
        for (int rowIndex = 0; rowIndex < rcRowCount; rowIndex++) {
            for (int colIndex = 0; colIndex < rcColCount; colIndex++) {
                Point pLT = points[rowIndex][colIndex];
                Point pLB = points[rowIndex + 1][colIndex];
                Point pRT = points[rowIndex][colIndex + 1];
                Point pRB = points[rowIndex + 1][colIndex + 1];

                int minX = Math.min(pLT.x, pLB.x);
                int minY = Math.min(pLT.y, pRT.y);
                int maxX = Math.max(pRT.x, pRB.x);
                int maxY = Math.max(pLB.y, pRB.y);
                grid[rowIndex][colIndex] = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            }
        }
        this.grid = grid;
        this.gridRowCount = rcRowCount;
        this.gridColumnCount = rcColCount;
    }

    /**
     * ocr 识别表格
     *
     * @return 文本
     */
    public String[][] ocr() {
        if (this.table != null)
            return this.table;

        //调用阿里云接口识别
        AliyunOcrResponse response = AliyunUtils.ocrAdvanced(this.buffer);
        IEnumerable<AliyunOcrWordInfo> prism_wordsInfo = Linq.asEnumerable(response.getPrism_wordsInfo());

        //识别结果派分到单元格
        Rectangle[][] grid = this.grid;
        int rowCount = this.gridRowCount;
        int colCount = this.gridColumnCount;
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
                table[fRowIndex][fColIndex] = StringUtils.replaceChars(wordInfo.getWord(), '，', ',');//替换中文逗号
            }
        }
        return this.table = table;
    }

    /**
     * 预览识别结果
     *
     * @return html 内容
     */
    public String preview() {
        int rcRowCount = this.gridRowCount;
        int rcColCount = this.gridColumnCount;
        String[][] table = this.ocr();
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

    /**
     * 另存为 excel
     *
     * @param outputStream 流
     * @param sheetName    表格名
     * @throws IOException 写入流发生异常
     */
    public void saveAsExcel(OutputStream outputStream, String sheetName) throws IOException {
        Assert.notNull(outputStream, "outputStream cannot be null.");
        Assert.notNull(sheetName, "sheetName cannot be null");

        int rcRowCount = this.gridRowCount;
        int rcColCount = this.gridColumnCount;
        String[][] table = this.ocr();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);  //创建table工作薄
        for (int rowIndex = 0; rowIndex < rcRowCount; rowIndex++) {
            HSSFRow row = sheet.createRow(rowIndex);//创建表格行
            for (int colIndex = 0; colIndex < rcColCount; colIndex++) {
                HSSFCell cell = row.createCell(colIndex);//根据表格行创建单元格
                String cellValue = table[rowIndex][colIndex];
                if (StringUtils.isEmpty(cellValue))
                    continue;
                cell.setCellValue(cellValue);
            }
        }
        wb.write(outputStream);
    }
}
