package com.dev.touyou.ffmultiplier.CustomClass;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.dev.touyou.ffmultiplier.R;

/**
 * Created by touyou on 2016/11/03.
 */
public class CustomButton extends Button {
    private String mFont = "FuturaExtended.ttf";

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFont(context, attrs);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public CustomButton(Context context) {
        super(context);
        init();
    }

    private void getFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        mFont = a.getString(R.styleable.CustomButton_font);
        a.recycle();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TtfFontHolder.getInstance().getTypeFace(getContext(), mFont));
        }
    }
}
