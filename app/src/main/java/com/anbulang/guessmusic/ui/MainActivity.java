package com.anbulang.guessmusic.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anbulang.guessmusic.R;
import com.anbulang.guessmusic.model.IWordButtonClickListener;
import com.anbulang.guessmusic.model.WordButton;
import com.anbulang.guessmusic.myui.MyGridView;
import com.anbulang.guessmusic.util.Util;

import java.util.ArrayList;


public class MainActivity extends Activity implements IWordButtonClickListener{

    // Log
    private static final String TAG = "MainActivity";

    // drawable 元素
    private ImageView mRecordView;
    private ImageView mPlayBarView;
    private ImageButton mPlayButton;

    // 播放相关动画
    private Animation mRecordAnim;
    private LinearInterpolator mRecordLin;
    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;
    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    // 判断是否正在播放的标识符
    private boolean isPlay = false;

    // 文字框容器
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mSelectedWords;

    // 自定义网格布局
    private MyGridView mMyGridView;

    // 已选择文字框布局
    private LinearLayout mSelectedWordsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化文字相关
        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        mMyGridView.setOnWordButtonClickListener(this);
        mSelectedWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

        // 播放按钮，并设置点击监听事件
        mPlayButton = (ImageButton) findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayButton();
            }
        });

        // 获得各个元素
        mRecordView = (ImageView) findViewById(R.id.recordView1);
        mPlayBarView = (ImageView) findViewById(R.id.recordView2);

        // 创建动画对象
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);  // 动画结束后不恢复到初始状态
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRecordView.startAnimation(mRecordAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mRecordAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mRecordLin = new LinearInterpolator();
        mRecordAnim.setInterpolator(mRecordLin);
        mRecordAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayBarView.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_r_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPlay = false;
                mPlayButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        
        // 初始化游戏数据
        initCurrentStageDate();
    }

    /**
     * 初始化游戏数据
     */
    private void initCurrentStageDate() {
        // 初始化已选择文字框
        mSelectedWords = initWordSelect();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
        for (int i = 0; i < mSelectedWords.size(); i++) {
            mSelectedWordsContainer.addView(mSelectedWords.get(i).getViewButton(), params);
        }
        
        // 获得所有文字数据
        mAllWords = initAllWords();
        
        // 更新数据
        mMyGridView.updateData(mAllWords);

    }

    /**
     * 初始化所有文字，从这些文字中选择歌曲名称
     * @return 所有文字的List
     */
    private ArrayList<WordButton> initAllWords() {
        ArrayList<WordButton> allWords = new ArrayList<>();

        // 获得所有待选文字
        // ......................

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            WordButton wordButton = new WordButton();
            wordButton.setWordString("囧");
            allWords.add(wordButton);
        }

        return allWords;
    }

    /**
     * 初始化文字选择框，初始时未选择任何文字
     * @return  被选择得WordButton List
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            View view = Util.getView(MainActivity.this, R.layout.mygridview_item);

            WordButton holder = new WordButton();
            holder.setViewButton((Button) view.findViewById(R.id.item_btn));
            holder.getViewButton().setTextColor(Color.WHITE);
            holder.getViewButton().setText("");
            holder.setVisiable(false);
            holder.getViewButton().setBackgroundResource(R.drawable.game_wordblank);
            data.add(holder);
        }
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mRecordView.clearAnimation();

        super.onPause();
    }

    private void handlePlayButton() {
        if (mPlayBarView != null) {
            if (!isPlay) {
                isPlay = true;
                mPlayBarView.startAnimation(mBarInAnim);
                mPlayButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onWordButtonOnClick(WordButton wordButton) {
        Toast.makeText(this, wordButton.getIndex() + "", Toast.LENGTH_SHORT).show();
    }
}