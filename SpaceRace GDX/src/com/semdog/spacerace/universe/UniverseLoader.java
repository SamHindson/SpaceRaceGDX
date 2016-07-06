package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.collectables.Health;
import com.semdog.spacerace.collectables.Toast;
import com.semdog.spacerace.collectables.WeaponPickup;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.vehicles.Needle;
import com.semdog.spacerace.vehicles.RubbishLander;
import com.semdog.spacerace.vehicles.SmallBombarder;

public class UniverseLoader {
    public void load(Universe universe) {
        JsonReader jsonReader = new JsonReader();
        JsonValue data = jsonReader.parse(RaceManager.getCurrentRaceSchematics());

        int index = 0;

        universe.setTimeLimit(data.getFloat("timelimit"));

        while (true) {
            JsonValue value = data.get(index);

            if (value == null)
                break;

            String name = value.name.split("-")[0];

            switch (name) {
                case "planet":
                    float r = value.get(0).getFloat("radius");
                    float x = value.get(0).getFloat("x");
                    float y = value.get(0).getFloat("y");
                    String id = value.get(0).getString("id");

                    universe.createPlanet(id, x, y, r);
                    Gdx.app.log("UniverseLoader", "Planet created.");

                    break;
                case "playerstuff":
                    float dx = value.get(0).getFloat("x");
                    float dy = value.get(0).getFloat("y");

                    universe.spawnPlayer(dx, dy);

                    try {
                        String ssname = value.get(1).get(0).name;
                        float ssvalue = value.get(1).getFloat(0);
                        Player.class.getDeclaredMethod(ssname, float.class).invoke(universe.getPlayer(), ssvalue);
                    } catch (NullPointerException npe) {
                        Gdx.app.error("UniverseLoader", "No extra data. But no problem!");
                    } catch (Exception e) {
                        Gdx.app.error("UniverseLoader", "Something went wrong while processing the JSON.");
                        e.printStackTrace();
                    }

                    break;
                case "runt":
                    float rx = value.get(0).getFloat("x");
                    float ry = value.get(0).getFloat("y");
                    String rid = value.get(0).getString("id");

                    SmallBombarder dude = new SmallBombarder(rx, ry, rid);

                    try {
                        String ssname = value.get(1).get(0).name;
                        float ssvalue = value.get(1).getFloat(0);
                        SmallBombarder.class.getDeclaredMethod(ssname, float.class).invoke(dude, ssvalue);
                    } catch (NullPointerException npe) {
                        Gdx.app.error("UniverseLoader", "No extra data. But no problem!");
                    } catch (Exception e) {
                        Gdx.app.error("UniverseLoader", "Something went wrong while processing the JSON.");
                        e.printStackTrace();
                    }

                    Gdx.app.log("UniverseLoader", "Runt ship created.");

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
                    } catch (NullPointerException npe) {
                        Gdx.app.error("UniverseLoader", "No extra data. But no problem!");
                    } catch (Exception e) {
                        Gdx.app.error("UniverseLoader", "Something went wrong while processing the JSON.");
                        e.printStackTrace();
                    }

                    Gdx.app.log("UniverseLoader", "Needle ship created.");

                    break;
                case "rubbish":
                    float cx = value.get(0).getFloat("x");
                    float cy = value.get(0).getFloat("y");
                    String cid = value.get(0).getString("id");

                    new RubbishLander(cx, cy, cid);
                    Gdx.app.log("UniverseLoader", "Rubbish ship created.");

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
                    Gdx.app.log("UniverseLoader", "Mission criteria loaded.");

                    break;
                case "healthpack":

                    float hx = value.get(0).getFloat("x");
                    float hy = value.get(0).getFloat("y");
                    universe.addCollectable(new Health(hx, hy));

                    break;
                case "toast":

                    float tx = value.get(0).getFloat("x");
                    float ty = value.get(0).getFloat("y");
                    universe.addCollectable(new Toast(tx, ty));

                    break;
                case "weaponpickup":

                    String type = value.name.split("-")[1];

                    float h = value.get(0).getFloat("h");
                    float a = value.get(0).getFloat("a") * MathUtils.degreesToRadians;

                    universe.addCollectable(new WeaponPickup(h, a, type));

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
