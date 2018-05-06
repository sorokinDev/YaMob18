package ru.sorokin.dev.yamob2018.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import ru.sorokin.dev.yamob2018.R;


public class ExtendedViewPager extends ViewPager {

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (getFocusedChild().findViewById(R.id.img) instanceof TouchImageView) {
            return getFocusedChild().findViewById(R.id.img).canScrollHorizontally(direction);
        } else {
            return super.canScrollHorizontally(direction);
        }
    }


}
