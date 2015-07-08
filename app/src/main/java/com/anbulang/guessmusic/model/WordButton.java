package com.anbulang.guessmusic.model;

import android.widget.Button;

/**
 * Created by Chaucer on 2015/6/30.
 */
public class WordButton {
    private int index;
    private boolean visiable;
    private String wordString;
    private Button viewButton;

    public WordButton() {
        this.visiable = true;
        this.wordString = "";
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean isVisiable) {
        this.visiable = isVisiable;
    }

    public String getWordString() {
        return wordString;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
    }

    public Button getViewButton() {
        return viewButton;
    }

    public void setViewButton(Button viewButton) {
        this.viewButton = viewButton;
    }
}
