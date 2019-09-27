package com.example.yxtdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.yxtdemo.CWEntity.CWLine;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-16
 * @Description
 */
public class SimpleDoodleView extends View {
    private final static String TAG = "SimpleDoodleView";
    private List<DrawPath> drawPaths = new ArrayList<>();
    private Paint mPaint;
    private List<Path> mPathList; // 保存涂鸦轨迹的集合
    private TouchGestureDetector mTouchGestureDetector; // 触摸手势监听
    private float mLastX, mLastY;
    private Path mCurrentPath; // 当前的涂鸦轨迹
    private boolean canDraw;
    private List<Point> points;
    private int strokeWidth=20;
    private int drawColor = Color.RED;

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public SimpleDoodleView(Context context) {
        super(context);
    }

    public SimpleDoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 由手势识别器处理手势
        mTouchGestureDetector = new TouchGestureDetector(getContext(), new TouchGestureDetector.OnTouchGestureListener() {

            @Override
            public void onScrollBegin(MotionEvent e) { // 滑动开始
                Log.d(TAG, "onScrollBegin: ");
                initPaint();
                mCurrentPath = new Path(); // 新的涂鸦
                mPathList = new ArrayList<>();
                mPathList.add(mCurrentPath); // 添加的集合中
                mCurrentPath.moveTo(e.getX(), e.getY());
                mLastX = e.getX();
                mLastY = e.getY();
                points = new ArrayList<>();
                points.add(new Point((int) mLastX, (int) mLastY));
                DrawPath drawPath = new DrawPath();
                drawPath.setmPaint(mPaint);
                drawPath.setmPathList(mPathList);
                drawPaths.add(drawPath);
                invalidate(); // 刷新
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { // 滑动中
                Log.d(TAG, "onScroll: " + e2.getX() + " " + e2.getY());
                mCurrentPath.quadTo(
                        mLastX,
                        mLastY,
                        (e2.getX() + mLastX) / 2,
                        (e2.getY() + mLastY) / 2); // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                mLastX = e2.getX();
                mLastY = e2.getY();
                points.add(new Point((int) mLastX, (int) mLastY));
                invalidate(); // 刷新
                return true;
            }

            @Override
            public void onScrollEnd(MotionEvent e) { // 滑动结束
                Log.d(TAG, "onScrollEnd: ");
                mCurrentPath.quadTo(
                        mLastX,
                        mLastY,
                        (e.getX() + mLastX) / 2,
                        (e.getY() + mLastY) / 2); // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                points.add(new Point((int) mLastX, (int) mLastY));
                int red = (mPaint.getColor() & 0xff0000) >> 16;
                int green = (mPaint.getColor() & 0x00ff00) >> 8;
                int blue = (mPaint.getColor() & 0x0000ff);
                CWFileUtil.writeACTLine(points, (int) mPaint.getStrokeWidth(), red + "," + green + "," + blue + ",1");
                mCurrentPath = null; // 轨迹结束
                invalidate(); // 刷新
            }

        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!canDraw) {
            return true;
        }
        boolean consumed = mTouchGestureDetector.onTouchEvent(event); // 由手势识别器处理手势
        if (!consumed) {
            return super.dispatchTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (DrawPath drawPath : drawPaths) {
            for(int i=0;i<drawPath.getmPathList().size();i++){
                canvas.drawPath(drawPath.getmPathList().get(i), drawPath.getmPaint());
            }
        }
    }

    public void clear() {
        if(drawPaths!=null){
            drawPaths.clear();
        }
        if(points!=null){
            points.clear();
        }
        if(mPathList!=null){
            mPathList.clear();
        }
        invalidate();
    }

    public void setDrawPath(CWLine line) {
        setStrokeWidth(line.getWidth());
        String[] split = line.getColor().split("\\,");
        setDrawColor(Color.rgb(Integer.valueOf(split[0]),Integer.valueOf(split[1]),Integer.valueOf(split[2])));
        initPaint();
        List<List<Integer>> points = line.getPoints();
        mCurrentPath = new Path(); // 新的涂鸦
        mPathList = new ArrayList<>();
        mPathList.add(mCurrentPath); // 添加的集合中
        for(int i=0;i<points.size();i++){
            List<Integer> xyPoints = points.get(i);
            if(i==0){
                mCurrentPath.moveTo(xyPoints.get(0), xyPoints.get(1));
            }else{
                mCurrentPath.quadTo(
                        mLastX,
                        mLastY,
                        (xyPoints.get(0) + mLastX) / 2,
                        (xyPoints.get(1) + mLastY) / 2);
            }
            mLastX = xyPoints.get(0);
            mLastY = xyPoints.get(1);
        }
        DrawPath drawPath = new DrawPath();
        drawPath.setmPaint(mPaint);
        drawPath.setmPathList(mPathList);
        drawPaths.add(drawPath);
       invalidate();
    }

    public void setStrokeWidth(int strokeWidth){
        this.strokeWidth = strokeWidth;
    }
    public void setDrawColor(int color){
       drawColor = color;
    }
    public void initPaint(){
        mPaint = new Paint();
        mPaint.setColor(drawColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    class DrawPath {
        private Paint mPaint;
        private List<Path> mPathList;

        public Paint getmPaint() {
            return mPaint;
        }

        public void setmPaint(Paint mPaint) {
            this.mPaint = mPaint;
        }

        public List<Path> getmPathList() {
            return mPathList;
        }

        public void setmPathList(List<Path> mPathList) {
            this.mPathList = mPathList;
        }
    }
}
