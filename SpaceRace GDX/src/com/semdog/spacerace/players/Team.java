package com.semdog.spacerace.players;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;

/**
 * An enum for the Multiplayer Teams.
 *
 * @author Sam
 */

public enum Team {
    PINK(Colors.P_PINK), BLUE(Colors.P_BLUE);

    private Color teamColor;

    Team(Color _teamColor) {
        teamColor = _teamColor;
    }

    public Color getColor() {
        return teamColor;
    }
}
