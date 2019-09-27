package com.example.yxtdemo;

/**
 * @Author szh
 * @Date 2019-09-19
 * @Description
 */
public class ScreenRecordEntity {
    private String type;//0-空板 1-图片
    private String path;
    private boolean canDraw;

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
