package com.example.administrator.dragviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class DragViewGrop extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private View mMenuView;//菜单内容
    private View mMainView;//主页面内容
    private int mWidhth;// 当前控件的宽度

    public DragViewGrop(Context context) {
        super(context);
        initView();
    }

    public DragViewGrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DragViewGrop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        //判断和何时开始检查触摸事件
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return mMainView == child;
//            return true;
        }

        //处理水平滑动
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }

        //处理垂直滑动
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return 0;
        }

        //拖动结束后调用
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.e("onViewReleased","移动距离   "+mMainView.getLeft());
            //手指抬起后缓慢移动到指定位置
            if (mMainView.getLeft() < mWidhth/2) {
                //拖动的距离太短,关闭菜单 相当于scroller
                viewDragHelper.smoothSlideViewTo(mMainView, 0, 0);
                ViewCompat.postInvalidateOnAnimation(DragViewGrop.this);
            } else {
                viewDragHelper.smoothSlideViewTo(mMainView, (int)(mWidhth*0.8), 0);
                ViewCompat.postInvalidateOnAnimation(DragViewGrop.this);
            }
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidhth = getMeasuredWidth();
    }

    //拦截事件交给viewDragHelper处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    //将触摸事件传递给viewDragHelper
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //布局加载完后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount()<2){
            throw new IllegalStateException("bug, 必须含有两个view");
        }
        mMainView = getChildAt(1);
        mMenuView = getChildAt(0);
    }
}
