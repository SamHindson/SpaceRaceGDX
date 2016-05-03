package com.semdog.spacerace.players;

public enum DeathCause {
	PLANET("You flew into a Planet."),
	BULLET("You got shot"),
	OWNBULLET("You shot yourself."),
	DEBRIS("Shrapnel, dude."),
	SHIP("You got ridden over by a SPACE SHIP."),
	EXPLOSION("Kabuum, baby");
	
	private String details;
	DeathCause(String _details) {
		details = _details;
	}
	
	public String getDetails() {
		return details;
	}
}
