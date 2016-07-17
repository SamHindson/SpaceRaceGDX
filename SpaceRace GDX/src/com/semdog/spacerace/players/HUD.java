package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.universe.Universe;

/**
 * The Heads Up Display seen by players. Shows them all sorts of useful
 * information, like their health and ammo situations
 * 
 * @author Sam
 */

public class HUD {
	private Player owner;
	private String title, subtitle;
	private boolean showingMessage, showingStats, showingTimer;

	private boolean initialSpawn;

	private boolean respawning = false;
	private float respawnCounter;

	private float timeLeft;

	private BitmapFont titleFont, subtitleFont, countdownFont, notificationFont, vitalsignsFont;

	private boolean showingNotification, notificationEntering, notificationExiting;
	private String notification;
	private float notificationHeight, notificationTime;

	private boolean showingToast;
	private float toastTime, currentToast;
	private String toast;
	private Color toastColor;

	public HUD(Player owner) {
		this.owner = owner;
		titleFont = FontManager.getFont("mohave-64");
		subtitleFont = FontManager.getFont("mohave-40");
		countdownFont = FontManager.getFont("mohave-48");
		notificationFont = FontManager.getFont("mohave-18");
		vitalsignsFont = FontManager.getFont("mohave-18-bold");
		title = subtitle = "";
	}

	public void update(float dt) {
		// If the player is busy respawning, let the countdown go ahead.
		if (respawning) {
			respawnCounter -= dt;
			if (respawnCounter <= 0) {
				respawning = false;
				showingMessage = false;
				if (initialSpawn) {
					Universe.currentUniverse.setPlayerEnabled(true);
					initialSpawn = false;
				}
				Universe.currentUniverse.respawnPlayer();
			}
		} else {
			// This handles the notifications seen along the bottom left of the
			// hud.
			if (showingNotification) {
				if (notificationEntering) {
					// While the notification is busy entering, increase its
					// height
					notificationHeight += 500 * dt;
					if (notificationHeight >= 25) {
						notificationHeight = 25;
						notificationEntering = false;
					}
				} else if (notificationExiting) {
					// While the notification is busy exiting, decrease its
					// height
					notificationHeight -= 500 * dt;
					if (notificationHeight <= 0) {
						notificationHeight = 0;
						notificationExiting = false;
						showingNotification = false;
					}
				} else {
					// Allow the notification to show for 3s before making it
					// exit
					notificationTime += dt;
					if (notificationTime > 3) {
						notificationExiting = true;
					}
				}
			} else if (showingToast) {
				// Update the toast if it is showing.
				currentToast += dt;
				if (currentToast > toastTime) {
					showingToast = false;
				}
			}
		}
	}

	public void draw(SpriteBatch spriteBatch) {
		// Draws a flashing countdown if the player is respawning
		if (respawning) {
			String time = "[" + (int) (respawnCounter) + "]";
			subtitleFont.setColor(new Color(MathUtils.random(2147483646)));
			subtitleFont.draw(spriteBatch, time, 0, 10 + subtitleFont.getCapHeight(), Gdx.graphics.getWidth(), 1, false);
		}
	
		// Draws the timer
		if (showingTimer) {
			int min = (int) (timeLeft / 60);
			int sec = (int) (timeLeft % 60);
			String time = String.format("%01d:%02d", min, sec);
			if (timeLeft < 10) {
				countdownFont.setColor(Colors.P_RED);
			} else {
				countdownFont.setColor(Color.WHITE);
			}
			countdownFont.draw(spriteBatch, time, 0, Gdx.graphics.getHeight() - 10, Gdx.graphics.getWidth(), 1, false);
		}
	
		// Draws the message
		if (showingMessage) {
			titleFont.draw(spriteBatch, title, 0, 30 + 2 * subtitleFont.getCapHeight() + titleFont.getCapHeight(), Gdx.graphics.getWidth(), 1, false);
			subtitleFont.setColor(Color.WHITE);
			subtitleFont.draw(spriteBatch, subtitle, 0, 20 + 2 * subtitleFont.getCapHeight(), Gdx.graphics.getWidth(), 1, false);
		}
	
		// Retrieves the player's vital signs and draws them as little bars at
		// the bottom right of the screen.
		if (showingStats) {
			// Draws a grey box as a background
			int size = owner.getPrimarySigns().getSigns().values().size();
			spriteBatch.setColor(0.2f, 0.2f, 0.2f, 1f);
			spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245, 0, 245, 15 + 40 * size);
			spriteBatch.setColor(0.5f, 0.5f, 0.5f, 1f);
			spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245, 5, 245, 15 + 40 * size);
			int h = 0;
	
			// Goes through each Vitality and draws it in its correct color and
			// format.
			for (Vitality vitality : owner.getPrimarySigns().getSigns().values()) {
				if (vitality.getValueType() == VitalSigns.Type.CONTINUOUS) {
					spriteBatch.setColor(new Color(vitality.getColor()));
					spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 240, 40 * h + h * 2 + 10, 235.f * (vitality.getValue() / vitality.getMaxValue()), 40);
					spriteBatch.setColor(0, 0, 0, 0.5f);
					spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 240, 40 * h + h * 2 + 10, 235.f * (vitality.getValue() / vitality.getMaxValue()), 5);
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
						spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245 + 5 + j * interval, 40 * h + h * 2 + 10, blockWidth, 40);
						spriteBatch.setColor(0, 0, 0, 0.5f);
						spriteBatch.draw(Art.get("pixel_white"), Gdx.graphics.getWidth() - 245 + 5 + j * interval, 40 * h + h * 2 + 10, blockWidth, 5);
						spriteBatch.setColor(Color.WHITE);
					}
				}
	
				vitalsignsFont.setColor(Colors.UI_WHITE);
				vitalsignsFont.draw(spriteBatch, vitality.getDisplayName(), Gdx.graphics.getWidth() - 240, 40 * h + h * 2 + 10 + vitalsignsFont.getLineHeight() + 10, 235f, 1, false);
				h++;
			}
	
			// The toast and notification must only be drawn if the player's
			// stats are showing.
	
			// Draws the toast
			if (showingToast) {
				subtitleFont.setColor(toastColor);
				subtitleFont.draw(spriteBatch, toast, 0, subtitleFont.getCapHeight() * 1.5f, Gdx.graphics.getWidth(), 1, false);
			}
	
			// Draws the notification
			if (showingNotification) {
				notificationFont.draw(spriteBatch, notification, 10, notificationHeight - 5);
			}
		}
	}

	public void makeToast(String toast, float time, Color toastColor) {
		this.toast = toast;
		this.toastColor = toastColor;
		toastTime = time;
		currentToast = 0;
		showingToast = true;
	}

	public void showNotification(String text) {
		showingNotification = true;
		notificationEntering = true;
		notification = text;
		notificationTime = 0;
	}

	public void showStats() {
		showingStats = true;
	}

	public void showTimer() {
		showingTimer = true;
	}

	public void displayMessage() {
		showingMessage = true;
	}

	public void setCountdownValue(int time) {
		timeLeft = time;
	}

	public void setDead(DamageCause reason, boolean respawnable) {
		showingStats = false;
		respawning = respawnable;
		respawnCounter = 6;
		
		setText(LifeAndDeath.getRandomCondolence(), reason.getDetails());
	}

	public void setText(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}

	public void hideToast() {
		showingToast = false;
		currentToast = toastTime;
	}

	public void hideMessage() {
		showingMessage = false;
	}

	public void hideAll() {
		showingStats = false;
		showingMessage = false;
		showingTimer = false;
	}
}
