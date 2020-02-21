package com.cqx.yaoqi;

/**
 * ImageAndNext
 *
 * @author chenqixu
 */
public class ImageAndNext {
    private String image_url;
    private String next_image_url;
    private String image_urlIShttpOrhttps;
    private String next_image_urlIShttpOrhttps;

    public static String getImageUrlName(String image_url) {
        //倒序查找/
        char[] image_urls = image_url.toCharArray();
        for (int i = (image_urls.length - 1); i > -1; i--) {
            if (String.valueOf(image_urls[i]).equals("/")) {
                return image_url.substring(i + 1);
            }
        }
        return null;
    }

    public String toString() {
        return "[image_url]" + image_url + ",[next_image_url]" + next_image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
        if (image_url.startsWith("https")) image_urlIShttpOrhttps = "https";
        else image_urlIShttpOrhttps = "http";
    }

    public String getNext_image_url() {
        return next_image_url;
    }

    public void setNext_image_url(String next_image_url) {
        if (next_image_url.startsWith("/mh")) {
            this.next_image_url = "";
        } else {
            if (next_image_url.length() > 0) {
                this.next_image_url = "https://m.yaoqi99.com/mh/" + next_image_url;
            }
            next_image_urlIShttpOrhttps = "https";
        }
    }

    public String getImage_urlIShttpOrhttps() {
        return image_urlIShttpOrhttps;
    }

    public String getNext_image_urlIShttpOrhttps() {
        return next_image_urlIShttpOrhttps;
    }
}
