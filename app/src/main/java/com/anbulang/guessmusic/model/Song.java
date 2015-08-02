package com.anbulang.guessmusic.model;

/**
 * 歌曲类
 * Created by Chaucer on 2015/7/9.
 */
public class Song {
    // 歌曲名称
    private String songName;
    // 歌曲文件名
    private String fileName;
    // 歌曲名字长度
    private int nameLength;

    public char[] getSongNameChars() {
        return songName.toCharArray();
    }
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
        this.nameLength = songName.length();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNameLength() {
        return nameLength;
    }

}
