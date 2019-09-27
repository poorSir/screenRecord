package com.example.yxtdemo.CWEntity;

/**
 * @Author szh
 * @Date 2019-09-25
 * @Description
 */
public class CWACT {
    private int time;
    private String action;
    private CWLine line;
    private CWSwitch cwSwitch;

    public CWSwitch getCwSwitch() {
        return cwSwitch;
    }

    public void setCwSwitch(CWSwitch cwSwitch) {
        this.cwSwitch = cwSwitch;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public CWLine getLine() {
        return line;
    }

    public void setLine(CWLine line) {
        this.line = line;
    }
}
