package com.semdog.spacerace.net;

public class PlayerState extends NetworkEvent {
	public static final int SETPOS = 0;
	public static final int ANIMSTATE = 1;
	public static final int ENVSTATE = 2;
	public static final int LIFE = 3;
	public static final int BULLETKILL = 4;
	
	private int category;
	private float[] information;

	public PlayerState() {
		
	}
	
	public PlayerState(int id, int category, float...information) {
		this.category = category;
		this.information = information;
		this.id = id;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setInformation(float[] information) {
		this.information = information;
	}

	public int getCategory() {
		return category;
	}

	public float[] getInformation() {
		return information;
	}
}
