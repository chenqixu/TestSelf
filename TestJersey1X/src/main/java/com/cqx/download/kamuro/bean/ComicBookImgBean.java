package com.cqx.download.kamuro.bean;

/**
 * ComicBookImgBean
 *
 * @author chenqixu
 */
public class ComicBookImgBean {
    private String month_name;
    private String book_name;
    private String book_type_name;
    private String book_id;
    private String img_name;
    private String img_md5;

    public ComicBookImgBean() {
    }

    public ComicBookImgBean(String month_name, String book_name, String book_type_name, String book_id, String img_name, String img_md5) {
        this.month_name = month_name;
        this.book_name = book_name;
        this.book_type_name = book_type_name;
        this.book_id = book_id;
        this.img_name = img_name;
        this.img_md5 = img_md5;
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

    public String getBook_type_name() {
        return book_type_name;
    }

    public void setBook_type_name(String book_type_name) {
        this.book_type_name = book_type_name;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public String getImg_md5() {
        return img_md5;
    }

    public void setImg_md5(String img_md5) {
        this.img_md5 = img_md5;
    }
}
