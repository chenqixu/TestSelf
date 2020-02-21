package com.cqx.yaoqi;

/**
 * TitleAndUrl
 *
 * @author chenqixu
 */
public class TitleAndUrl {
    private String title;
    private String title_url;
    private String next_page_url;

    public String toString() {
        return "[title]" + title + ",[title_url]" + title_url + ",[next_page_url]" + next_page_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_url() {
        return title_url;
    }

    public void setTitle_url(String title_url) {
        if (title_url.length() > 0) this.title_url = "https://m.yaoqi99.com" + title_url;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        if (next_page_url.length() > 0) this.next_page_url = "https://m.yaoqi99.com/mh/" + next_page_url;
    }
}
