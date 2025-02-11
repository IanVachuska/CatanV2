package com.mycatan;

import java.awt.*;

public interface IDrawable {
    /**
     * <p>Draw the Tile and its aggregates.</p>
     * @param g2d the {@code Graphics2D} object
     */
    void draw(Graphics2D g2d);
}
