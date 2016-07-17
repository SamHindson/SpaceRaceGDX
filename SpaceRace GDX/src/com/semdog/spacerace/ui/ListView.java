package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;

/**
 * A UI element which displays things in a list. Much easier said than done.
 *
 * @author Sam
 */

public class ListView {
    private float x, y, width, height;

    private int maxItems;
    private int page = 0;
    private int pageCount;

    private Button up, down;
    private Button[] items;
    private boolean[] extra;

    private String[] titles;
    private ListViewListener listener;
    private Color borderColor, itemColor;

    public ListView(float x, float y, float width, float height, Color borderColor, Color itemColor, int maxItems) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxItems = maxItems;
        this.itemColor = itemColor;
        this.borderColor = borderColor;

        up = new Button("^", true, x + width / 2, y + height - 20, width, 40, () -> {
            if (page != 0)
                page--;

            recomposeItems();
        });
        up.setColors(borderColor, Color.WHITE);
        down = new Button("V", true, x + width / 2, y + 20, width, 40, () -> {
            if (page != pageCount - 1)
                page++;

            recomposeItems();
        });
        down.setColors(borderColor, Color.WHITE);

        items = new Button[maxItems];
        pageCount = items.length / maxItems + 1;
    }

    public void update(float dt) {
        up.update(dt);
        down.update(dt);

        for (Button item : items) {
            if (item != null)
                item.update(dt);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(borderColor);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.BLACK);
        batch.draw(Art.get("pixel_white"), x + 5, y + 5, width - 10, height - 10);

        for (Button item : items)
            if (item != null)
                item.draw(batch);


        up.draw(batch);
        down.draw(batch);
    }

    /**
     * Rearranges the buttons
     */
    private void recomposeItems() {
        int usableSpace = (int) height - 80 + 4;
        int buttonHeight = usableSpace / maxItems;
        items = new Button[maxItems];
        for (int k = 0; k < maxItems; k++) {
            int titleIndex = (k + maxItems * page);
            if (titleIndex > titles.length - 1)
                return;
            Button button = new Button(titles[k + maxItems * page], true, x + width / 2, y + height - 40 - (buttonHeight / 2) - (k * buttonHeight), width - 10, buttonHeight, () -> itemChosen(titleIndex));
            button.setColors(extra[k + maxItems * page] ? new Color(itemColor).mul(0.75f) : itemColor, Color.WHITE);
            items[k] = button;
        }
    }

    private void itemChosen(int index) {
        listener.itemSelected(index);
    }

    public void setTitles(String[] titles, boolean[] extra) {
        this.titles = titles;
        this.extra = extra;
        recomposeItems();
    }

    public void setListener(ListViewListener listener) {
        this.listener = listener;
    }
}
