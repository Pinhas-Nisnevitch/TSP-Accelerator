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
 * This class represents a colored line that can be displayed on screen.
 * @author Pinhas Nisnevitch
 * @see Shapes
 */
public class Line implements Shapes {
    // the line is defined by two points (x1, y1) and (x2, y2)
    int x1, y1;
    int x2, y2;
    Color c; // the color of the circle.
    
    /**
     * 
     * @param x1 the x - coordinate of the first point that defined the line.
     * @param y1 the y - coordinate of the first point that defined the line.
     * @param x2 the x - coordinate of the second point that defined the line.
     * @param y2 the y - coordinate of the second point that defined the line.
     * @param c the color of the line.
     */
    public Line(int x1, int y1, int x2, int y2, Color c) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.c = c;
    }

    /**
     * Draw a line on a graphics.
     *
     * @param g The graphics on which the line will be drawn.
     */
    @Override
    public void paintShape(Graphics g) {
        g.setColor(c);
        g.drawLine(x1, y1, x2, y2);
    }
}
