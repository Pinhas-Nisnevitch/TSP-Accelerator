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
package Main;

/**
 * This class represents a point on the screen using cartesian coordinates. the
 * class hold the real values of a point and a scale factor(for visualization).
 *
 * @author Pinhas Nisnevitch
 */
public class ScaledPoint {

    final private double x;
    final private double y;
    private double scaleX;
    private double scaleY;

    /**
     *
     * @param x The real x value of the point.
     * @param y The real y value of the point.
     */
    protected ScaledPoint(double x, double y) {
        this.x = x;
        this.y = y;
        scaleX = scaleY = 1;
    }

    /**
     *
     * @param x The real x value of the point.
     * @param y The real y value of the point.
     * @param scaleX The scale factor of x.
     * @param scaleY The scale factor of y.
     */
    protected ScaledPoint(double x, double y, double scaleX, double scaleY) {
        this.x = x;
        this.y = y;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**
     *
     * @return the x position on the screen after scaling.
     */
    protected int getX() {
        return (int) (x * scaleX);
    }

    /**
     *
     * @return the y position on the screen after scaling.
     */
    protected int getY() {
        return (int) (y * scaleY);
    }

    /**
     *
     * @return The real x value of the point.
     */
    protected double getRealX() {
        return x;
    }

    /**
     *
     * @return The real y value of the point.
     */
    protected double getRealY() {
        return y;
    }

    /**
     *
     * @param scaleX The scale factor of X.
     */
    protected void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    /**
     *
     * @param scaleY The scale factor of y.
     */
    protected void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }
    
    @Override
    public String toString(){
        return "("+x+", "+y+") - ("+getX()+", "+getY()+")";
    }
}
