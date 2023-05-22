package com.bussiness.bi.bigdata.ogg;

/**
 * OggJsonSchema
 *
 * @author chenqixu
 */
public class OggJsonSchema {
    private String title;
    private String description;
    private Definitions definitions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}
