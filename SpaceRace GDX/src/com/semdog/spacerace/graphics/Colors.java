package com.semdog.spacerace.graphics;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.misc.Tools;

/**
 * TODO phase this out
 * @author Sam
 */

public class Colors {
	public static final Color P_RED = new Color(0xd32f2fff);
	public static final Color P_ORANGE = new Color(0xE64A19ff);
	public static final Color P_YELLOW = new Color(0xFFA000ff);
	public static final Color P_GREEN = new Color(0x388E3Cff);
	public static final Color P_BLUE = new Color(0x434DB0ff);
	public static final Color P_PURPLE = new Color(0x7B1FA2ff);
	public static final Color P_GRAY = new Color(0x616161ff);
	public static final Color P_WHITE = new Color(0xBDBDBDff);
	public static final Color P_PINK = new Color(0xff006eff);
	
	public static final Color V_FUEL = new Color(0x3FDD4DFF);
	public static final Color V_SHIPHEALTH = new Color(0xFF4479FF);
	public static final Color V_SHIPAMMO = new Color(0xFAC05EFF);
	public static final Color V_PLAYERHEALTH = new Color(0xD62F61FF);
	public static final Color V_SMGAMMO = new Color(0x8075FFFF);
	public static final Color V_CARBINEAMMO = new Color(0x0DBEB2FF);
	public static final Color V_SHOTGUNAMMO = new Color(0xFAFAFAFF);
	public static final Color V_PLAYERGRENADES = new Color(0xFFB626FF);
	public static final Color V_BOOST = new Color(0x46B1C9FF);
	
	public static Color getRandom() {
		return (Color) Tools.decide(P_BLUE, P_GRAY, P_GREEN, P_ORANGE, P_PURPLE, P_RED, P_WHITE, P_YELLOW);
	}
}
