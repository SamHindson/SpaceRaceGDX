package com.semdog.spacerace.net.entities;

import java.util.HashMap;

/**
 * Created by Sam on 2016/08/08.
 */
public class MassMap {
    HashMap<Integer, MassState> massStates;

    MassMap() {
    }

    MassMap(HashMap<Integer, MassState> massStates) {
        this.massStates = massStates;
    }

    public HashMap<Integer, MassState> getMassStates() {
        return massStates;
    }
}
