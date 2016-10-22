package view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * description:处理缩放时的崩溃问题
 * author：pz
 * 时间：2016/10/14:22:42
 */
public class PhotoViewViewPager extends ViewPager {
    public PhotoViewViewPager(Context context) {
        super(context);
    }

    public PhotoViewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

    }
}
