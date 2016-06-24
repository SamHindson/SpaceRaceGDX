package com.semdog.spacerace.players;

public enum DamageCause {
	PLANET("You flew into a Planet."),
	BULLET("You got shot"),
	OWNBULLET("You shot yourself (?)"),
	DEBRIS("The shrapnel gotya"),
	SHIP("You were involved in a hit-and-run."),
	EXPLOSION("Kaboom, baby"),
	MASS("You were hit by a UFO"), 
	FALLING("You fell out of the sky");
	
	private String details;
	DamageCause(String _details) {
		details = _details;
	}
	
	public String getDetails() {
		return details;
	}
}
