package com.semdog.spacerace.players;

/**
 * An enum that details how entities (like players and masses) can be damaged in
 * SpaceRace, with custom death messages displayed on the HUD if the player is
 * involved.
 *
 * @author Sam
 */

public enum DamageCause {
    PLANET("You flew into a Planet."),
    BULLET("You got shot."),
    OWNBULLET("You shot yourself (?)"),
    DEBRIS("You were killed by shrapnel"),
    SHIP("You were involved in a hit-and-run."),
    EXPLOSION("You were caught in an explosion"),
    MASS("You were hit by a UFO"),
    FALLING("You fell out of the sky"),
    OLDAGE("If you're a player and you see this, REPORT IMMEDIATELY. Horrible horrible bug"), // Only masses are meant to die from old age
    RPG("You were exploded by an RPG."),
    DRINKINGFUEL("You drank rocket fuel.");

    private String details;

    DamageCause(String _details) {
        details = _details;
    }

    public String getDetails() {
        return details;
    }
}
