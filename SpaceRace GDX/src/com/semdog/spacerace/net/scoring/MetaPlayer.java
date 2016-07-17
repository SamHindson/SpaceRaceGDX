package com.semdog.spacerace.net.scoring;

import com.semdog.spacerace.players.Team;

public class MetaPlayer {
	private int score;
	private int id;
	private Team team;
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}

	public void setTeam(int team2) {
		team = team2 == 1 ? Team.PINK : Team.BLUE;
	}
	
	public void addPoint() {
		score++;
	}
	
	public void subtractPoint() {
		score--;
	}
}
