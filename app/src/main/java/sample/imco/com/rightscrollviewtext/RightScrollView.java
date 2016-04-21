package sample.imco.com.rightscrollviewtext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

/**
 * Created by mqh on 3/8/16.
 */
public class RightScrollView extends FrameLayout {
    private RightScrollView.OnRightScrollListener mRightScrollListener;
    private boolean mIsRightScrolling;
    private int mLastMotionX;
    private int mLastMotionY;
    private int mTouchSlop;
    private boolean mInterceptVertical;
    private boolean mChildTouch;
    private boolean mRightScrollable;
//    private boolean isRound;
    private Path mPath;
    private int mWidth;
    private int mHeight;


    public RightScrollView(Context context) {
        this(context, (AttributeSet)null);
    }

    public RightScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsRightScrolling = false;
        this.mInterceptVertical = false;
        this.mChildTouch = false;
        this.mRightScrollable = true;
//        this.isRound = false;
//        IwdsAssert.dieIf(this, !IwdsCompatibilityChecker.getInstance().check(), "Compatibility check failed.");
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        this.isRound = HardwareList.IsCircularScreen();
    }

    protected void dispatchDraw(Canvas canvas) {
        this.mHeight = this.getMeasuredHeight();
        this.mWidth = this.getMeasuredWidth();
        int saveCount = canvas.getSaveCount();
        canvas.save();
//        if(!this.isRound) {
            canvas.clipRect(0, 0, this.mWidth, this.mHeight);
//        } else {
//            if(this.mPath == null) {
//                this.mPath = new Path();
//                this.mPath.addCircle((float)this.mWidth / 2.0F, (float)this.mHeight / 2.0F, (float)Math.min((double)((float)this.mWidth / 2.0F), (double)this.mHeight / 2.0D) + 2.0F, Path.Direction.CCW);
//                this.mPath.close();
//            }
//
//            canvas.clipPath(this.mPath, Region.Op.REPLACE);
//        }

        canvas.drawColor(Color.BLUE);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void setContentView(int resId) {
        View view = inflate(this.getContext(), resId, (ViewGroup)null);
        this.setContentView(view);
    }

    public void setContentView(View view) {
        this.setContentView(view, (LayoutParams)null);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(this.getChildCount() > 0) {
            this.removeAllViews();
        }

        if(params == null) {
            params = new android.widget.FrameLayout.LayoutParams(-1, -1);
        }

        this.addView(view, (LayoutParams) params);
    }


    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!this.mRightScrollable) {
            return false;
        } else if(this.mRightScrollListener == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            int action = ev.getAction();
            if(action == 2 && this.mIsRightScrolling) {
                return true;
            } else {
                switch(action & 255) {
                    case 0:
                        this.mLastMotionX = (int)ev.getX();
                        this.mLastMotionY = (int)ev.getY();
                        this.mChildTouch = false;
                        break;
                    case 2:
                        if(this.mChildTouch) {
                            return false;
                        }

                        int deltaX = (int)ev.getX() - this.mLastMotionX;
                        this.mIsRightScrolling = deltaX > this.mTouchSlop;
                        if(!this.mIsRightScrolling) {
                            return false;
                        }

                        int deltaY = (int)Math.abs(ev.getY() - (float)this.mLastMotionY);
                        if(!this.mInterceptVertical && deltaY >= Math.abs(deltaX)) {
                            this.mChildTouch = true;
                            this.mIsRightScrolling = false;
                            return false;
                        }
                }

                return this.mIsRightScrolling;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(!this.mRightScrollable) {
            return false;
        } else {
            int action = event.getAction();
            int tran;
            switch(action & 255) {
                case 0:
                    this.mLastMotionX = (int)event.getX();
                    this.mChildTouch = false;
                    break;
                case 1:
                case 3:
                    if(this.mIsRightScrolling) {
                        tran = this.getScrollX();
                        if(tran < -this.getWidth() / 2) {
                            this.animateTo(-1.0F);
                        } else {
                            this.scrollBack();
                        }
                    }

                    this.mIsRightScrolling = false;
                    this.mChildTouch = false;
                    break;
                case 2:
                    if(this.mChildTouch) {
                        return true;
                    }

                    int deltaX = (int)event.getX() - this.mLastMotionX;
                    if(!this.mIsRightScrolling) {
                        this.mIsRightScrolling = deltaX > this.mTouchSlop;
                    }

                    deltaX = Math.max(0, deltaX);
                    if(this.mIsRightScrolling) {
                        this.scrollTo(-deltaX, 0);
                        tran = this.getWidth();
                        this.setAlpha((float)(tran - deltaX) / (float)tran);
                    }
            }

            return true;
        }
    }

    private void scrollBack() {
        this.animateTo(0.0F);
    }

    private void animateTo(float to) {
        int scroll = this.getScrollX();
        float start = (float)scroll / (float)this.getWidth();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{start, to});
        animator.setDuration((long)((int)(400.0F * Math.abs(start - to))));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float)animation.getAnimatedValue()).floatValue();
                RightScrollView.this.scrollTo((int)((float)RightScrollView.this.getWidth() * value), 0);
                RightScrollView.this.setAlpha(1.0F + value);
            }
        });
        if(to == -1.0F) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if(RightScrollView.this.mRightScrollListener != null) {
                        RightScrollView.this.mRightScrollListener.onRightScroll();
                    }

                }
            });
        }

        animator.start();
    }

    public void setOnRightScrollListener(RightScrollView.OnRightScrollListener l) {
        this.mRightScrollListener = l;
    }

    public void performRightScroll() {
        this.animateTo(-1.0F);
    }

    public void interceptVertical() {
        this.mInterceptVertical = true;
    }

    public void unInterceptVertical() {
        this.mInterceptVertical = false;
    }

    public boolean isInterceptVertical() {
        return this.mInterceptVertical;
    }

    public void enableRightScroll() {
        this.mRightScrollable = true;
    }

    public void disableRightScroll() {
        this.mRightScrollable = false;
    }

    public boolean isRightScrollable() {
        return this.mRightScrollable;
    }


    public void setBackground(Drawable background) {
        this.setBackgroundDrawable(background);
    }

    public void setBackgroundColor(int color) {
        this.setBackgroundDrawable(new ColorDrawable(color));
    }

    public void setBackgroundResource(int resid) {
        this.setBackgroundDrawable(this.getResources().getDrawable(resid));
    }


    public interface OnRightScrollListener {
        void onRightScroll();
    }
}
