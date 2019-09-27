package com.example.yxtdemo.CWEntity;

import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-25
 * @Description
 */
public class CWLine {
    private int width;
    private String color;
    private List<List<Integer>> points;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<List<Integer>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Integer>> points) {
        this.points = points;
    }
}
