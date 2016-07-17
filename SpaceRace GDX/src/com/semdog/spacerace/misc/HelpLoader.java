package com.semdog.spacerace.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * A class created to load up the help text from the file system.
 *
 * @author Sam
 */

public class HelpLoader {
    private Array<HelpItem> items;
    private String[] titles;

    public HelpLoader() {
        items = new Array<>();

        // Creates a massive string from the help.srh file and splits it using
        // the plus sign as a delimiter.
        // TODO: Can be optimized
        String allHelp = Gdx.files.internal("assets/help/help.srh").readString();
        String[] split = allHelp.split("[+]");

        // Every odd line is a section's title, and the one after it is its
        // description.
        for (int q = 0; q < split.length; q += 2) {
            String title = split[q];
            String raw = split[q + 1];
            items.add(new HelpItem(title, raw));
        }

        titles = new String[items.size];

        for (int z = 0; z < titles.length; z++) {
            titles[z] = items.get(z).getTitle();
        }
    }

    public HelpItem getItem(int i) {
        return items.get(i);
    }

    public String[] getTitles() {
        return titles;
    }
}
