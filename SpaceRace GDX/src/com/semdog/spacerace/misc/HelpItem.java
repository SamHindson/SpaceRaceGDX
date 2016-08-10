package com.semdog.spacerace.misc;

/**
 * Tiny class made to hold the text and title of a help section.
 *
 * @author Sam
 */

public class HelpItem {
    private String title;

    /**
     * We need to have our text split because of the way the help section draws it (word by word), taking note of
     * any color or image markups.
     */
    private String[] split;

    HelpItem(String title, String rawText) {
        this.title = title;
        split = rawText.split(" ");
    }

    public String getTitle() {
        return title;
    }

    public String[] getSplit() {
        return split;
    }
}
