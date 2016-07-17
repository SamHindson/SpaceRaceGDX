package com.semdog.spacerace.misc;

/**
 * Tiny class made to hold the text and title of a help section.
 * 
 * @author Sam
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
