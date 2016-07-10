package com.semdog.spacerace.misc;

/**
 * Created by Sam on 2016/07/08.
 */
public class HelpItem {
    private String title, rawText;
    private String[] split;

    public HelpItem(String title, String rawText) {
        this.title = title;
        this.rawText = rawText;
        split = rawText.split(" ");
    }

    public String getRawText() {
        return rawText;
    }

    public String getTitle() {
        return title;
    }

    public String[] getSplit() {
        return split;
    }
}
