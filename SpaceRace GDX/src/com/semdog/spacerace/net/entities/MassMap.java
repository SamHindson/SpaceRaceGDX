package com.semdog.spacerace.net.entities;

import java.util.HashMap;

/**
 * A class which holds the information about masses on a server.
 *
 * @author Sam
 */

public class MassMap {
    private HashMap<Integer, MassState> massStates;

    //  Default no-parameter constructor for Kryo deserialization.
    MassMap() {
    }

    MassMap(HashMap<Integer, MassState> massStates) {
        this.massStates = massStates;
    }

    public HashMap<Integer, MassState> getMassStates() {
        return massStates;
    }
}
