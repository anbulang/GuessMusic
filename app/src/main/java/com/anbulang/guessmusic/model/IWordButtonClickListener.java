package com.anbulang.guessmusic.model;

/**
 * 观察者模式接口
 * Created by Chaucer on 2015/7/7.
 */
public interface IWordButtonClickListener {

    /**
     * WordButton 点击响应事件
     * @param wordButton 被点击按钮模型
     */
    void onWordButtonClick(WordButton wordButton);

}
