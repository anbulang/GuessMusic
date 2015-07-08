package com.anbulang.guessmusic.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Chaucer on 2015/7/2.
 */
public class Util {

    public static View getView(Context context, int layoutId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(layoutId, null);
    }
}
