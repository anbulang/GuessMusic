package com.anbulang.guessmusic.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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
import com.anbulang.guessmusic.data.Const;
import com.anbulang.guessmusic.model.IWordButtonClickListener;
import com.anbulang.guessmusic.model.Song;
import com.anbulang.guessmusic.model.WordButton;
import com.anbulang.guessmusic.myui.MyGridView;
import com.anbulang.guessmusic.util.Logger;
import com.anbulang.guessmusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity implements IWordButtonClickListener{

    // Logger
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

    // 当前歌曲
    private Song mCurrentSong;
    // 当前关的索引
    private int mCurrentStageIndex = -1;

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
        // 初始化歌曲信息
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

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
     * 初始化当前关的歌曲信息
     * @param i 当前关索引
     * @return  歌曲信息
     */
    private Song loadStageSongInfo(int i) {
        Song song = new Song();
        song.setFileName(Const.SONG_INFO[i][Const.INDEX_FILE_NAME]);
        song.setSongName(Const.SONG_INFO[i][Const.INDEX_SONG_NAME]);
        return song;
    }

    /**
     * 初始化所有文字，从这些文字中选择歌曲名称
     * @return 所有文字的List
     */
    private ArrayList<WordButton> initAllWords() {
        ArrayList<WordButton> allWords = new ArrayList<>();

        // 获得所有待选文字
        String[] words = generateWords();
        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            WordButton wordButton = new WordButton();
            wordButton.setWordString(words[i]);
            allWords.add(wordButton);
        }

        return allWords;
    }

    /**
     * 生成所有待选文字，生成规则：
     * （1）先把歌曲名放到数组前边
     * （2）随机生成剩余文字
     * （3）打乱文字顺序
     * @return 生成的随机文字数组
     */
    private String[] generateWords() {
        Random random = new Random();
        String[] words = new String[MyGridView.COUNTS_WORDS];
        // 先把歌曲名放到数组前边
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            words[i] = mCurrentSong.getSongNameChars()[i] + "";
        }
        // 随机生成剩余文字
        for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
            try {
                words[i] = getRandomWord();
            } catch (UnsupportedEncodingException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
        // 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
        // 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
        // 这样能够确保每个元素在每个位置的概率都是1/n。
        for (int i = MyGridView.COUNTS_WORDS - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = words[index];
            words[index] = words[i];
            words[i] = temp;
        }
        return words;
    }

    /**
     * 随机生成一个中文字符
     * @return 单个中文字符串
     */
    private String getRandomWord() throws UnsupportedEncodingException {
        int highPart;
        int lowPart;
        Random random = new Random();
        highPart = 176 + random.nextInt(39);
        lowPart = 161 + random.nextInt(93);
        byte[] wordCode = new byte[2];
        wordCode[0] = Integer.valueOf(highPart).byteValue();
        wordCode[1] = Integer.valueOf(lowPart).byteValue();
        Logger.i(TAG, wordCode[0] + " " + wordCode[1]);
        return new String(wordCode, "GBK");
    }

    /**
     * 初始化文字选择框，初始时未选择任何文字
     * @return  被选择得WordButton List
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<>();
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            View view = Util.getView(MainActivity.this, R.layout.mygridview_item);

            final WordButton holder = new WordButton();
            holder.setViewButton((Button) view.findViewById(R.id.item_btn));
            holder.getViewButton().setTextColor(Color.WHITE);
            holder.getViewButton().setText("");
            holder.setVisiable(false);
            holder.getViewButton().setBackgroundResource(R.drawable.game_wordblank);
            // 为待选框添加点击监听事件
            holder.getViewButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearSelectedWord(holder);
                }

                private void clearSelectedWord(WordButton holder) {
                    holder.getViewButton().setText("");
                    holder.setVisiable(false);
                    holder.setWordString("");
                    // 设置原文字可见
                    setButtonVisible(mAllWords.get(holder.getIndex()), View.VISIBLE);
                }
            });
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
    public void onWordButtonClick(WordButton wordButton) {
        //Toast.makeText(this, wordButton.getIndex() + "", Toast.LENGTH_SHORT).show();
        // 点击文字，文字到待选框，文字隐藏
        for (int i = 0; i < mSelectedWords.size(); i++) {
            WordButton tempWordButton = mSelectedWords.get(i);
            final String wordString = tempWordButton.getWordString();
            if (wordString == null || wordString.equals("")) {
                tempWordButton.getViewButton().setText(wordButton.getWordString());
                tempWordButton.setWordString(wordButton.getWordString());
                tempWordButton.setIndex(wordButton.getIndex());
                tempWordButton.setVisiable(true);
                // 被点击文字隐藏
                setButtonVisible(wordButton, View.INVISIBLE);
                break;
            }
        }
    }

    /**
     * 被点击文字隐藏/显示
     * @param wordButton 被点击文字model
     * @param visibility 可见性 View.VISIBLE / View.INVISIBLE
     */
    private void setButtonVisible(WordButton wordButton, int visibility) {
        wordButton.getViewButton().setVisibility(visibility);
        switch (visibility) {
            case View.INVISIBLE:
                wordButton.setVisiable(false);
                break;
            case View.VISIBLE:
                wordButton.setVisiable(true);
                break;
            default:
                throw new RuntimeException("Error visibility!");
        }
    }
}