package com.weizh.swipedelete.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.weizh.swipedelete.manager.SwipeLayoutManager;

/**
 * Created by weizh_000 on 2016/8/23.
 */

public class SwipeLayout extends FrameLayout {
    private View contentView;//item内容区域的view
    private View deleteView;//delete区域的view
    private int contentWidth;//内容区域的宽度
    private int deleteWidth;//delete区域的宽度
    private int deleteHeight;//delete区域的高度
    private ViewDragHelper dragHelper;
    private float downX;
    private float downY;
    private SwipeState currentState;//当前状态是默认是关闭状态

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dragHelper = ViewDragHelper.create(this, callback);
        currentState = SwipeState.Close;
    }


    enum SwipeState {
        Open, Close;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = contentView.getMeasuredWidth();
        deleteWidth = deleteView.getMeasuredWidth();
        deleteHeight = deleteView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentView.layout(0, 0, contentWidth, deleteHeight);
        deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == deleteView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left < -deleteWidth) left = -deleteWidth;
                if (left > 0) left = 0;
            } else if (child == deleteView) {
                if (left < contentWidth - deleteWidth) left = contentWidth - deleteWidth;
                if (left > contentWidth) left = contentWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //做伴随移动
            if (changedView == contentView) {
                deleteView.layout(deleteView.getLeft() + dx, 0, deleteView.getRight() + dx, deleteHeight);
            } else if (changedView == deleteView) {
                contentView.layout(contentView.getLeft() + dx, 0, contentView.getRight() + dx, deleteHeight);
            }
            //判断开关逻辑
            if (contentView.getLeft() == 0) {
                //说明state应更改为关闭
                currentState = SwipeState.Close;
                //清空已打开swipelayout记录
                SwipeLayoutManager.getInstance().clearCurrentLayout();
            } else if (contentView.getLeft() == -deleteWidth) {
                //说明state应更改为打开
                currentState = SwipeState.Open;
                //记录已打开swipelayout
                SwipeLayoutManager.getInstance().setCurrentLayout(SwipeLayout.this);
            }

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth / 2) {
                //应该打开
                open();
            } else {
                //应该关闭
                close();
            }
        }
    };

    //打开swipelayout
    private void open() {
        dragHelper.smoothSlideViewTo(contentView, -deleteWidth, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    //关闭swipelayout
    public void close() {
        dragHelper.smoothSlideViewTo(contentView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }
//恢复swipelayout至初始位置
    public void resume() {
        contentView.layout(0,0,contentWidth,deleteHeight);
        deleteView.layout(contentView.getRight(),0,contentView.getRight()+deleteWidth,deleteHeight);
    }



    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让dragHelper决定是否拦截事件
        boolean b = dragHelper.shouldInterceptTouchEvent(ev);
        if (!SwipeLayoutManager.getInstance().isShouldOpen(this)) {
            b = true;//将事件传递给onTouchEvent
            //当前swipelayout不能滑动，先关闭swipelayout
            SwipeLayoutManager.getInstance().closeSwipeLayout();
            //请求父类不要拦截事件
            requestDisallowInterceptTouchEvent(true);
        }
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果有已打开的swipelayout，则先关闭
        if (!SwipeLayoutManager.getInstance().isShouldOpen(this)) {
            //消费掉触摸事件
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float delatX = moveX - downX;
                float delatY = moveY - downY;
                if (Math.abs(delatX) > Math.abs(delatY)) {
                    //禁止listview拦截事件
                    requestDisallowInterceptTouchEvent(true);
                }
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //交给dragHelper处理触摸事件
        dragHelper.processTouchEvent(event);
        return true;
    }
}
