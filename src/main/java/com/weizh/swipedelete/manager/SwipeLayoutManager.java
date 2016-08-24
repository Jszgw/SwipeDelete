package com.weizh.swipedelete.manager;

import com.weizh.swipedelete.widget.SwipeLayout;

/**
 * Created by weizh_000 on 2016/8/24.
 */

public class SwipeLayoutManager {

    private static SwipeLayoutManager swipeLayoutManager = new SwipeLayoutManager();
    private SwipeLayout currentLayout;

    public static SwipeLayoutManager getInstance(){
        return swipeLayoutManager;
    }

    //记录当前打开的是哪一个swipeLayout
    public void setCurrentLayout(SwipeLayout swipeLayout){
        this.currentLayout = swipeLayout;
    }

    //清除记录
    public void clearCurrentLayout(){
        currentLayout=null;
    }

    //关闭记录中的swipelayout
    public void closeSwipeLayout(){
        if (currentLayout!=null){
            currentLayout.close();
        }
    }

    //判断是否可以左右滑动swipelayout
    public boolean isShouldOpen(SwipeLayout swipeLayout){
        if (currentLayout==null){
            //说明无记录，可以打开
            return true;
        }else {
            //说明有记录，
            //记录的是自己，可以左右滑动
            //记录的是别的swipelayout，则不能滑动
            return currentLayout==swipeLayout;
        }
    }

    public void resumeSwipeLayout() {
        if(currentLayout!=null){
            currentLayout.resume();
        }
    }
}
