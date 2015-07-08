package com.anbulang.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.anbulang.guessmusic.R;
import com.anbulang.guessmusic.model.IWordButtonClickListener;
import com.anbulang.guessmusic.model.WordButton;
import com.anbulang.guessmusic.util.Util;

import java.util.ArrayList;

/**
 * Created by Chaucer on 2015/6/30.
 */
public class MyGridView extends GridView {

    public static final int COUNTS_WORDS = 24;

    // Log
    protected static final String TAG = "MyGridView";

    // The list of storing wordButtons
    private ArrayList<WordButton> mWordButtonList;
    // Grid adapter
    private MyGridAdapter mMyGridAdapter;
    // Context
    private Context mContext;
    // Animation
    private Animation mScaleAnimation;
    // WordButton listener
    private IWordButtonClickListener mWordButtonClickListener;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWordButtonList = new ArrayList<>();
        mMyGridAdapter = new MyGridAdapter();
        this.mContext = context;
        this.setAdapter(mMyGridAdapter);
    }

    public void updateData(ArrayList<WordButton> list) {
        mWordButtonList = list;
        // Reset the data source
        setAdapter(mMyGridAdapter);
    }

    public void setOnWordButtonClickListener(IWordButtonClickListener listener) {
        mWordButtonClickListener = listener;
    }

    /**
     * Inner Class
     * Grid Adapter
     */
    class MyGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mWordButtonList.size();
        }

        @Override
        public Object getItem(int pos) {
            return mWordButtonList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(final int pos, View v, ViewGroup viewGroup) {
            final WordButton holder;

            if (v == null) {
                v = Util.getView(mContext, R.layout.mygridview_item);
                holder = mWordButtonList.get(pos);
                // 加载动画
                mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
                // 设置动画的延迟时间
                mScaleAnimation.setStartOffset(pos * 100);
                holder.setIndex(pos);
                holder.setViewButton((Button) v.findViewById(R.id.item_btn));
                holder.getViewButton().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWordButtonClickListener.onWordButtonOnClick(holder);
                    }
                });
                v.setTag(holder);
            } else {
                holder = (WordButton) v.getTag();
            }
            holder.getViewButton().setText(holder.getWordString());
            // 播放动画
            v.startAnimation(mScaleAnimation);
            return v;
        }
    }
}
