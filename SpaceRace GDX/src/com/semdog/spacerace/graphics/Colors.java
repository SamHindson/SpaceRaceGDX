package com.semdog.spacerace.graphics;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.misc.Tools;

public class Colors {
	public static final Color PLANETRED = new Color(0xd32f2fff);
	public static final Color PLANETORANGE = new Color(0xE64A19ff);
	public static final Color PLANETYELLOW = new Color(0xFFA000ff);
	public static final Color PLANETGREEN = new Color(0x388E3Cff);
	public static final Color PLANETBLUE = new Color(0x303F9Fff);
	public static final Color PLANETPURPLE = new Color(0x7B1FA2ff);
	public static final Color PLANETGRAY = new Color(0x616161ff);
	public static final Color PLANETWHITE = new Color(0xBDBDBDff);
	public static final Color PLANETPINK = new Color(0xff006eff);
	
	public static Color getRandom() {
		return (Color) Tools.decide(PLANETBLUE, PLANETGRAY, PLANETGREEN, PLANETORANGE, PLANETPURPLE, PLANETRED, PLANETWHITE, PLANETYELLOW);
	}
}
