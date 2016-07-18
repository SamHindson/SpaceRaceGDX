package com.semdog.spacerace.ui;

/**
 * An interface which classes must implement if they want to keep up to date
 * with what's busy happening with their ListView.
 */

public interface ListViewListener {
    void itemSelected(int index);
}
