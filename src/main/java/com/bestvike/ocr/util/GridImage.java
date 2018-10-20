package com.bestvike.ocr.util;

import org.springframework.util.Assert;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许崇雷 on 2018-10-20.
 */
public final class GridImage {
    private static final int MIN_RED = 200;
    private static final int MIN_DISTANCE = 5;
    private final BufferedImage image;
    private final Dimension dimension;
    private Rectangle[][] grid;
    private int gridRowCount;
    private int gridColumnCount;

    /**
     * 构造函数
     */
    public GridImage(BufferedImage image) {
        Assert.notNull(image, "image cannot be null.");
        this.image = image;
        this.dimension = new Dimension(this.image.getWidth(), this.image.getHeight());
        this.init();
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
     * 初始化单元格
     * 1. 必须划分辅助线
     * 2. 辅助线要求:
     * 颜色 红色RGB(255,0,0)
     * 宽度 1px-3px
     * 不开启抗锯齿
     * 必须划到边,不能留空白
     */
    private void init() {
        //识别四个边红色点
        int right = this.dimension.width - 1;
        int bottom = this.dimension.height - 1;

        //识别竖线
        List<Point> topPoints = new ArrayList<>();
        List<Point> bottomPoints = new ArrayList<>();
        int lastTop = 0;
        int lastBottom = 0;
        topPoints.add(new Point(0, 0));
        bottomPoints.add(new Point(0, bottom));
        for (int x = 0; x <= right; x++) {
            Color topColor = new Color(this.image.getRGB(x, 0));
            if (topColor.getRed() > MIN_RED && x - lastTop > MIN_DISTANCE)
                topPoints.add(new Point(lastTop = x, 0));

            Color bottomColor = new Color(this.image.getRGB(x, bottom));
            if (bottomColor.getRed() > MIN_RED && x - lastBottom > MIN_DISTANCE)
                bottomPoints.add(new Point(lastBottom = x, bottom));
        }
        if (topPoints.size() != bottomPoints.size())
            throw new RuntimeException("竖线识别失败");
        topPoints.add(new Point(right, 0));
        bottomPoints.add(new Point(right, bottom));

        //识别横线
        List<Point> leftPoints = new ArrayList<>();
        List<Point> rightPoints = new ArrayList<>();
        int lastLeft = 0;
        int lastRight = 0;
        leftPoints.add(new Point(0, 0));
        rightPoints.add(new Point(right, 0));
        for (int y = 0; y <= bottom; y++) {
            Color leftColor = new Color(this.image.getRGB(0, y));
            if (leftColor.getRed() > MIN_RED && y - lastLeft > MIN_DISTANCE)
                leftPoints.add(new Point(0, lastLeft = y));

            Color rightColor = new Color(this.image.getRGB(right, y));
            if (rightColor.getRed() > MIN_RED && y - lastRight > MIN_DISTANCE)
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
        for (int colIndex = 0; colIndex < colCount; colIndex++) {
            points[0][colIndex] = topPoints.get(colIndex);
            points[rowCount - 1][colIndex] = bottomPoints.get(colIndex);
        }
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            points[rowIndex][0] = leftPoints.get(rowIndex);
            points[rowIndex][colCount - 1] = rightPoints.get(rowIndex);
        }
        for (int rowIndex = 1; rowIndex <= rowCount - 2; rowIndex++) {
            for (int colIndex = 1; colIndex <= colCount - 2; colIndex++)
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
}
