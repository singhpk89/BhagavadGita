/**
 * 
 */
package com.sunilsahoo.bhagavadgita.beans;

public class Quote implements Item {
    private static final long serialVersionUID = -5025851577882139237L;
    private int id;
    private String body;
    private int is_favourite;
    private int chapterNo;
    private String chapterTitle;
    private String textId;
    private String slokaSanskrit;
    private String slokaEnglish;

    /**
     * @param id
     * @param body
     * @param is_favourist
     */
    /*public Quote(int id, String body, int is_favourite, int chapterNo) {
        this.id = id;
        this.body = body;
        this.is_favourite = is_favourite;
        this.chapterNo = chapterNo;
    }*/
    
    public Quote() {
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

    public int isFavourite() {
        return is_favourite;
    }

    /**
     * @param is_favourist
     *            the is_favourist to set
     */
    public void setFavourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    public int getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(int chapterId) {
        this.chapterNo = chapterId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public String getSlokaSanskrit() {
        return slokaSanskrit;
    }

    public void setSlokaSanskrit(String slokaSanskrit) {
        this.slokaSanskrit = slokaSanskrit;
    }

    public String getSlokaEnglish() {
        return slokaEnglish;
    }

    public void setSlokaEnglish(String slokaEnglish) {
        this.slokaEnglish = slokaEnglish;
    }
}
