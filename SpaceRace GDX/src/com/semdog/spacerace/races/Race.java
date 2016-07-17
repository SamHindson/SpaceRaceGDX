package com.semdog.spacerace.races;

/**
 * A class that is populated with information about a loaded race, just so everything doesn't have to use JSON Parsing
 * whenever a piece of data is needed.
 * 
 * @author Sam
 */

public class Race {
    private String id, name, description, briefing;
    private String author;

    private float bestTime;
    private float timeLimit;
    private boolean completed = true;

    private String content;

    Race(String name, String author, String description, String briefing, float timeLimit, String content) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.briefing = briefing;
        this.timeLimit = timeLimit;
        this.content = content;
        this.id = computeID();
    }

    /**
     * A method that computes a unique ID for a race so as to make it easier for Best Times to be stored and identified.
     *
     * @return the computed ID
     */
    private String computeID() {
        String result = "";
        result = (author.hashCode() + "" + name.hashCode());

        for (int w = 0; w < 10; w++) {
            result = result.hashCode() + "";
        }
        
        return result;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getTimeLimit() {
        return timeLimit;
    }

    public String getID() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public float getBestTime() {
        return bestTime;
    }

    public void setBestTime(float bestTime) {
        this.bestTime = bestTime;
    }

    public String getBriefing() {
        return briefing;
    }
    
    public boolean isCompleted() {
		return completed;
	}
    
    public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
