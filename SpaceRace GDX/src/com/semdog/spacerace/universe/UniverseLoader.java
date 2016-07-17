package com.semdog.spacerace.universe;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.collectables.Fuel;
import com.semdog.spacerace.collectables.Health;
import com.semdog.spacerace.collectables.Toast;
import com.semdog.spacerace.collectables.WeaponPickup;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.vehicles.Bombarder;
import com.semdog.spacerace.vehicles.Needle;
import com.semdog.spacerace.vehicles.RubbishLander;

/**
 * A class that parses the json files found in the "data/stockraces" directory
 * to generate the races exactly as how they were designed.
 *
 * @author Sam
 */

public class UniverseLoader {
    public void load(Universe universe) {
        JsonReader jsonReader = new JsonReader();
        JsonValue data = jsonReader.parse(RaceManager.getCurrentRace().getContent());

        int index = 0;

        universe.setTimeLimit(data.getFloat("timelimit"));
        universe.setSuddenDeath(data.getBoolean("suddendeath"));
        universe.setLockedRotation(data.getBoolean("lockedcamera"));

        while (true) {
            JsonValue value = data.get(index);
            if (value == null) break;
            String name = value.name.split("-")[0];

            switch (name) {
                case "planet":
                    float r = value.get(0).getFloat("radius");
                    float x = value.get(0).getFloat("x");
                    float y = value.get(0).getFloat("y");
                    String id = value.get(0).getString("id");

                    universe.createPlanet(id, x, y, r);
                    break;
                case "playerstuff":
                    float dx = value.get(0).getFloat("x");
                    float dy = value.get(0).getFloat("y");

                    universe.spawnPlayer(dx, dy);

                    try {
                        String ssname = value.get(1).get(0).name;
                        float ssvalue = value.get(1).getFloat(0);
                        Player.class.getDeclaredMethod(ssname, float.class).invoke(universe.getPlayer(), ssvalue);
                    } catch (Exception e) {
                    } // If there is no extra data in the json entry, that's fine.

                    break;
                case "runt":
                    float rx = value.get(0).getFloat("x");
                    float ry = value.get(0).getFloat("y");
                    String rid = value.get(0).getString("id");

                    Bombarder dude = new Bombarder(rx, ry, rid);

                    try {
                        String ssname = value.get(1).get(0).name;
                        float ssvalue = value.get(1).getFloat(0);
                        Bombarder.class.getDeclaredMethod(ssname, float.class).invoke(dude, ssvalue);
                    } catch (Exception e) {
                    } // If there is no extra data in the json entry, that's fine.

                    break;
                case "needle":
                    float nrx = value.get(0).getFloat("x");
                    float nry = value.get(0).getFloat("y");
                    String nrid = value.get(0).getString("id");

                    Needle ndude = new Needle(nrx, nry, nrid);

                    try {
                        String ssname = value.get(1).get(0).name;
                        float ssvalue = value.get(1).getFloat(0);
                        Needle.class.getDeclaredMethod(ssname, float.class).invoke(ndude, ssvalue);
                    } catch (Exception e) {
                    } // If there is no extra data in the json entry, that's fine.

                    break;
                case "rubbish":
                    float cx = value.get(0).getFloat("x");
                    float cy = value.get(0).getFloat("y");
                    String cid = value.get(0).getString("id");
                    new RubbishLander(cx, cy, cid);

                    break;
                case "playercriteria":
                    JsonValue details = value.get(0);

                    for (JsonValue criteria : details) {
                        String criterion = criteria.name;
                        String criterionType = criterion.split("-")[0];
                        String criterionName = criterion.split("-")[1];

                        switch (criterionType) {
                            case "boolean":
                                universe.getGoalChecker().addBoolean(criterionName, details.getBoolean(criterion));
                                break;
                            case "float":
                                universe.getGoalChecker().addFloat(criterionName, details.getFloat(criterion));
                                break;
                            case "string":
                                universe.getGoalChecker().addString(criterionName, details.getString(criterion));
                                break;
                        }
                    }

                    break;
                case "healthpack":

                    float hx = value.get(0).getFloat("x");
                    float hy = value.get(0).getFloat("y");
                    int hPlanet = value.get(0).getInt("planet");
                    universe.addCollectable(new Health(hx, hy), hPlanet);

                    break;
                case "toast":

                    float tx = value.get(0).getFloat("h");
                    float ty = value.get(0).getFloat("a");
                    int tPlanet = value.get(0).getInt("planet");
                    universe.addCollectable(new Toast(tx, ty), tPlanet);

                    break;
                case "weaponpickup":

                    String type = value.name.split("-")[1];
                    float h = value.get(0).getFloat("h");
                    float a = value.get(0).getFloat("a") * MathUtils.degreesToRadians;
                    int wPlanet = value.get(0).getInt("planet");
                    universe.addCollectable(new WeaponPickup(h, a, type), wPlanet);

                    break;
                case "fuel":

                    float fh = value.get(0).getFloat("h");
                    float fa = value.get(0).getFloat("a") * MathUtils.degreesToRadians;
                    int fPlanet = value.get(0).getInt("planet");
                    universe.addCollectable(new Fuel(fh, fa), fPlanet);

                    break;
                case "box":

                    float bx = value.get(0).getFloat("x");
                    float by = value.get(0).getFloat("y");
                    String bid = value.get(0).getString("id");
                    new Box(bx, by, bid);

                    break;
            }

            index++;
        }
    }
}
