package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by sam on 2016/05/01.
 * <p>
 * The Heads Up Display seen by players. Shows them all sorts of useful
 * information, such as if they are busy bleeding out
 */

public class HUD implements Disposable {
    private Player owner;
    private String title, subtitle;
    private boolean showingMessage = false;
    private boolean showingStats, showingTimer;

    private boolean initialSpawn;

    private boolean respawning = false, respawnable = false;
    private float respawnCounter;

    @SuppressWarnings("unused")
    private boolean countdownActive;
    private float timeLeft;

    private BitmapFont titleFont, subtitleFont, countdownFont, notificationFont;
    private float titleX, titleY, subtitleX, subtitleY, respawnX, respawnY;

    private boolean showingNotification, notificationEntering, notificationExiting;
    private String notification;
    private float notificationHeight, notificationTime;

    private boolean showingToast;
    private float toastTime;
    private String toast;

    public HUD(Player owner) {
        this.owner = owner;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Mohave.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        titleFont = generator.generateFont(parameter);
        parameter.size = 40;
        subtitleFont = generator.generateFont(parameter);
        parameter.size = 45;
        countdownFont = generator.generateFont(parameter);
        parameter.size = 18;
        notificationFont = generator.generateFont(parameter);
        generator.dispose();

        title = subtitle = "";
    }

    public void update(float dt) {
        if (respawning) {
            respawnCounter -= dt;

            if (respawnCounter <= 0) {
                respawning = false;
                showingMessage = false;

                if (initialSpawn) {
                    Universe.currentUniverse.setPlayerEnabled(true);
                    initialSpawn = false;
                }

                if (respawnable)
                    Universe.currentUniverse.respawnPlayer();
            }
        } else {
            if (showingNotification) {
                if (notificationEntering) {
                    notificationHeight += 500 * dt;

                    if (notificationHeight >= 25) {
                        notificationHeight = 25;
                        notificationEntering = false;
                    }
                } else if (notificationExiting) {
                    notificationHeight -= 500 * dt;

                    if (notificationHeight <= 0) {
                        notificationHeight = 0;
                        notificationExiting = false;
                        showingNotification = false;
                    }
                } else {
                    notificationTime += dt;

                    if (notificationTime > 3) {
                        notificationExiting = true;
                    }
                }
            } else if (showingToast) {
                toastTime += dt;
                if (toastTime > 0.5f) {
                    showingToast = false;
                }
            }
        }
    }

    public void showNotification(String text) {
        showingNotification = true;
        notificationEntering = true;

        notification = text;

        notificationTime = 0;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (respawning) {
            subtitleFont.setColor(new Color(MathUtils.random(2147483646)));
            subtitleFont.draw(spriteBatch, "[" + (int) (respawnCounter) + "]", respawnX, respawnY);
        }

        if (showingTimer) {
            int min = (int) (timeLeft / 60);
            int sec = (int) (timeLeft % 60);
            String time = String.format("%01d:%02d", min, sec);

            if (timeLeft < 10) {
                countdownFont.setColor(Colors.P_RED);
            }
            countdownFont.draw(spriteBatch, time,
                    Gdx.graphics.getWidth() / 2 - 24, Gdx.graphics.getHeight() - 10);
            if (timeLeft < 10) {
                countdownFont.setColor(Color.WHITE);
            }
        }

        if (owner.isBoarding()) {
            subtitleFont.setColor(new Color((int) (MathUtils.random() * Integer.MAX_VALUE)));
            subtitleFont.draw(spriteBatch, "PRESS [E] TO BOARD", Gdx.graphics.getWidth() / 2.f - 169.0f,
                    Gdx.graphics.getHeight() * 0.4f - 10.5f);
        }

        if (showingMessage) {
            titleFont.draw(spriteBatch, title, titleX, titleY);
            subtitleFont.setColor(Color.WHITE);
            subtitleFont.draw(spriteBatch, subtitle, subtitleX, subtitleY);
        }

        if (showingStats) {
            int size = owner.getPrimarySigns().getSigns().values().size();
            spriteBatch.setColor(0, 0, 0, 0.5f);
            spriteBatch.draw(Art.get("pixel_gray"), Gdx.graphics.getWidth() - 245, 0, 245, 15 + 40 * size);
            spriteBatch.setColor(Color.WHITE);
            spriteBatch.draw(Art.get("pixel_gray"), Gdx.graphics.getWidth() - 245, 5, 245, 15 + 40 * size);
            int h = 0;
            for (Vitality vitality : owner.getPrimarySigns().getSigns().values()) {
                if (vitality.getValueType() == VitalSigns.Type.CONTINUOUS) {
                    spriteBatch.setColor(new Color(vitality.getColor()));
                    spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 240, 40 * h + h * 2 + 10,
                            235.f * (vitality.getValue() / vitality.getMaxValue()), 40);
                    spriteBatch.setColor(0, 0, 0, 0.5f);
                    spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 240, 40 * h + h * 2 + 10,
                            235.f * (vitality.getValue() / vitality.getMaxValue()), 5);
                    spriteBatch.setColor(Color.WHITE);

                } else {
                    float maxParts = vitality.getMaxValue();
                    float partCount = vitality.getValue();
                    float interblockSpaces = 2 * (maxParts - 1);
                    float blockTotal = 235 - interblockSpaces;
                    float blockWidth = blockTotal / maxParts;
                    float interval = blockWidth + 2;

                    for (int j = 0; j < partCount; j++) {
                        spriteBatch.setColor(new Color(vitality.getColor()));
                        spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245 + 5 + j * interval,
                                40 * h + h * 2 + 10, blockWidth, 40);
                        spriteBatch.setColor(0, 0, 0, 0.5f);
                        spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245 + 5 + j * interval,
                                40 * h + h * 2 + 10, blockWidth, 5);
                        spriteBatch.setColor(Color.WHITE);
                    }
                }
                h++;
            }

            if (showingToast) {
                subtitleFont.setColor(Colors.UI_BLUE);
                subtitleFont.draw(spriteBatch, toast, 0, subtitleFont.getCapHeight() * 1.5f, Gdx.graphics.getWidth(), 1, false);
            }

            if (showingNotification) {
                notificationFont.draw(spriteBatch, notification, 10, notificationHeight - 5);
            }
        }
    }

    public void setCountdownValue(int time) {
        timeLeft = time;
    }

    public void setDead(DamageCause reason, boolean respawnable) {
        this.respawnable = respawnable;

        showingStats = false;

        if (respawnable) {
            respawning = true;
            respawnCounter = 6;
        }

        setText(LifeAndDeath.getRandomCondolence(), reason.getDetails());
    }

    public void displayMessage() {
        showingMessage = true;
    }

    public void hideMessage() {
        showingMessage = false;
    }

    public void setText(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;

        GlyphLayout titGlyph = new GlyphLayout(titleFont, title);
        titleX = Gdx.graphics.getWidth() / 2 - titGlyph.width / 2.f;
        titleY = Gdx.graphics.getHeight() * 0.22f - titGlyph.height / 2.f;

        GlyphLayout subGlyph = new GlyphLayout(subtitleFont, subtitle);
        subtitleX = Gdx.graphics.getWidth() / 2 - subGlyph.width / 2.f;
        subtitleY = Gdx.graphics.getHeight() * 0.1f - subGlyph.height / 2.f + 20;

        GlyphLayout resGlyph = new GlyphLayout(subtitleFont, "[0]");
        respawnX = Gdx.graphics.getWidth() / 2 - resGlyph.width / 2.f;
        respawnY = Gdx.graphics.getHeight() * 0.070f - resGlyph.height / 2.f;
    }

    public void hideAll() {
        showingStats = false;
        showingMessage = false;
        showingTimer = false;
    }

    public void showStats() {
        showingStats = true;
    }

    public void showTimer() {
        showingTimer = true;
    }

    @Override
    public void dispose() {
        // TODO figure out what to dispose here
        //titleFont.dispose();
        //subtitleFont.dispose();
        //countdownFont.dispose();
    }

    public void makeToast(String toast) {
        this.toast = toast;
        toastTime = 0;
        showingToast = true;
    }
}
