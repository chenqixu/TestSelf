package com.cqx.download.kamuro.bean;

/**
 * ComicBookBean
 *
 * @author chenqixu
 */
public class ComicBookBean {
    private String month_name;
    private String book_name;
    private String book_desc;
    private String title_img_name;

    public ComicBookBean() {
    }

    public ComicBookBean(String month_name, String book_name, String book_desc, String title_img_name) {
        this.month_name = month_name;
        this.book_name = book_name;
        this.book_desc = book_desc;
        this.title_img_name = title_img_name;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_desc() {
        return book_desc;
    }

    public void setBook_desc(String book_desc) {
        this.book_desc = book_desc;
    }

    public String getTitle_img_name() {
        return title_img_name;
    }

    public void setTitle_img_name(String title_img_name) {
        this.title_img_name = title_img_name;
    }
}
