package com.semdog.spacerace.players;

import com.badlogic.gdx.graphics.Color;
import com.semdog.spacerace.graphics.Colors;

/**
 * Created by sam on 2016/05/01.
 * <p>
 * An enum for Teams. That's it
 */

public enum Team {
    PINK(Colors.PLANETPINK), BLUE(Colors.PLANETBLUE);

    Color teamColor;

    Team(Color _teamColor) {
        teamColor = _teamColor;
    }
}
