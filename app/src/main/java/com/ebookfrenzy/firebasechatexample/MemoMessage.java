package com.ebookfrenzy.firebasechatexample;

public class MemoMessage {

    private String text;        // 메시지
    private String name;        // 이름
    private String date;        // 날짜
    private String photoUrl;  // 프로필 사진 경로

    public MemoMessage() {
    }

    public MemoMessage(String text, String name, String date,String photoUrl) {
        this.text = text;
        this.name = name;
        this.date = date;
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
