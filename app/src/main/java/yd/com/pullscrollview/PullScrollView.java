package yd.com.pullscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * Created by 256 on 2016/8/17.
 */
public class PullScrollView extends RelativeLayout {

    private Context context;

    private Scroller mScroller ;//view滑动计算器

    //getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。
    // 如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
    private int mTouchSlop ;

    private ViewGroup bottomView ;
    private ScrollView contentView ;

    private int startY ;
    private PullState state = PullState.REST ;
    private int bottomHeight = 0 ;

    enum PullState{
        REST , ON_REFRESH
    }



    public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        init(context);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context);
    }

    public PullScrollView(Context context) {
        super(context);
        this.context=context;
        init(context);
    }



    private void init(Context context){

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mScroller = new Scroller(context, new DecelerateInterpolator());
    }





    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getTopPosition();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 2) {
            throw new RuntimeException("子View只能有两个");
        }
        bottomView = (ViewGroup) getChildAt(0);
        contentView = (ScrollView) getChildAt(1);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (getScrollY() < 0 ) {
            return true ;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int delayY = moveY - startY ;
                Log.i("Test", delayY + " =  " + mTouchSlop) ;
                if (getTopPosition() && delayY > mTouchSlop) {
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    return true ;
                }
                break ;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int delayY = (int) (event.getY() - startY) ;
                if (getTopPosition() && getScrollY() <= 0 ) {
                    pullMove((int) (-delayY * 0.8));
                }
                startY = (int) event.getY();

                return true ;
            case MotionEvent.ACTION_UP:
                int scrollY = getScrollY();
                if(scrollY < 0){
                    returnView();
                }
                break;
        }
        return true ;
    }




    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }


    private void returnView(){
        restView(-getScrollY());
    }


    private void restView(int dy){
        mScroller.startScroll(0, getScrollY(), 0, dy , 340);
        postInvalidate();
    }



    private void pullMove(int delay){
        Log.e("delay", "delay :" + delay);
        if (getScrollY() <= 0 && (getScrollY() + delay) <= 0 ) {
            scrollBy(0, delay);

        }else {
            scrollTo(0, 0);
        }
    }

    /**
     * 判断
     * @return 判断contentView的偏移量，可以用log打印出出来，我在写demo的时候 ：contentView.getScrollY() = 0
     */
    private boolean getTopPosition(){
        if (contentView.getScrollY() <= 0 ) {
            return true ;
        }
        return false ;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        bottomHeight = getBottomViewHeight() ;
        Log.i("Test", l + "ceshi" + " t="+t + " r"+r + " b=" + b + " height= "   + bottomHeight);
        bottomView.layout(l, - bottomHeight, r, t);
        contentView.layout(l, 0, r, b);
    }


    /**
     * 获得head头部的高度
     * @return
     */
    private int getBottomViewHeight(){
        return bottomView.getMeasuredHeight() ;
    }


}
