package com.semdog.spacerace.net;

public class NewPlayer extends NetworkEvent {
	
	public NewPlayer() {

	}
	
	public NewPlayer(VirtualPlayer player) {
		this.player = player;
	}
	
	public VirtualPlayer getPlayer() {
		return player;
	}
	
	public int getId() {
		return id;
	}
	
	private VirtualPlayer player;
	private String name;
	private int team;
	private int id;
	
	public void setId(int id) {
		this.id = id;
		player.setID(id);
	}
}
