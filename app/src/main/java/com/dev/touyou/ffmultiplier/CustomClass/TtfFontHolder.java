package com.dev.touyou.ffmultiplier.CustomClass;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by touyou on 2016/11/03.
 */
public class TtfFontHolder {
    private final HashMap<String, Typeface> mFontMap = new HashMap<>();

    private TtfFontHolder() {}

    private static class TtfFontHolderHolder {
        public static final TtfFontHolder INSTANCE = new TtfFontHolder();
    }

    public static TtfFontHolder getInstance() {
        return TtfFontHolderHolder.INSTANCE;
    }

    public Typeface getTypeFace(Context context, String ttfName) {
        if (mFontMap.containsKey(ttfName)) {
            return mFontMap.get(ttfName);
        } else {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), ttfName);
            mFontMap.put(ttfName, tf);
            return tf;
        }
    }
}
