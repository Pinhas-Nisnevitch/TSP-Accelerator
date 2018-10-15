/*
 * Copyright (C) 2018 Pinhas Nisnevitch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Visualization;

import java.awt.Color;
import java.awt.Graphics;

/**
 * This class represents a colored circle with a radius that can be displayed on
 * screen.
 *
 * @author Pinhas Nisnevitch
 * @See Shapes
 */
public class CircleWithRadius implements Shapes {

    int x, y; // the location of the circle.
    int radius; // the radius of the circle.
    int smallest_diam; //the smallest Diameter which allowed on the graphics.
    Color c; // the color of the circle.
    

    /**
     *
     * @param x the x coordinate of the circle.
     * @param y the y coordinate of the circle.
     * @param radius the radius of the circle.
     * @param c the color of the circle.
     * @param smallest_diam the smallest Diameter which allowed on the graphics.
     */
    public CircleWithRadius(int x, int y, int radius, Color c, int smallest_diam) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.c = c;
        this.smallest_diam = smallest_diam;
    }

    /**
     * Draw a circle and a radius on a graphics.
     *
     * @param g The graphics on which the circle and radius will be drawn.
     */
    @Override
    public void paintShape(Graphics g) {
        g.setColor(c);
        g.fillOval(x - (int)(smallest_diam/2) + radius / 2, y - (int)(smallest_diam/2) + radius / 2, smallest_diam, smallest_diam);
        g.drawOval(x, y, radius, radius);
        g.drawLine(x + radius / 2, y + radius / 2, x + radius / 2, y + radius);
    }
}
