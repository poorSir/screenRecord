package com.example.yxtdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.example.yxtdemo.R;

/**
 * @Author szh
 * @Date 2019-09-19
 * @Description
 */
public class ForbitLayoutManager extends LinearLayoutManager {
    private boolean canScrollHorizon = true;
    private boolean canScrollVertical = true;
    public ForbitLayoutManager(Context context) {
        super(context);
    }

    public ForbitLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ForbitLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCanScrollHorizon(boolean canScrollHorizon) {
        this.canScrollHorizon = canScrollHorizon;
    }

    public void setCanScrollVertical(boolean canScrollVertical) {
        this.canScrollVertical = canScrollVertical;
    }

    @Override
    public boolean canScrollHorizontally() {
        return canScrollHorizon && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return canScrollVertical && super.canScrollVertically();
    }
}
