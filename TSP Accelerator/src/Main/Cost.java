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

import java.util.ArrayList;

/**
 * This class holds a list of ScaledPoints and provides a cost between each two
 * points.
 *
 * @author Pinhas Nisnevitch
 * @see ScaledPoint
 */
public final class Cost {

    private ArrayList<ScaledPoint> points;
    private double Li;

    /**
     *
     * @param points a list of scaled points.
     * @param Li The l_i distance (e.g. L1, L2 etc.)
     */
    public Cost(ArrayList<ScaledPoint> points, double Li) {
        setData(points, Li);
    }

    /**
     * Sets the points and the distance function.
     *
     * @param points a list of scaled points.
     * @param Li The l_i distance (e.g. L1, L2 etc.)
     */
    protected void setData(ArrayList<ScaledPoint> points, double Li) {
        this.points = points;
        this.Li = Li;
    }

    /**
     * Sets the points
     *
     * @param points a list of scaled points.
     */
    protected void setPoints(ArrayList<ScaledPoint> points) {
        this.points = points;
    }

    /**
     *
     * @return The list of ScaledPoints
     */
    protected ArrayList<ScaledPoint> getPoints() {
        return points;
    }

    /**
     * Calculate the distance between two points using the Li - distance.
     *
     * @param i The index of the first point
     * @param j The index of the second point
     * @return The distance between the points i and j.
     */
    public double getDistance(int i, int j) {
        double val = Integer.MAX_VALUE;
        if (i != j) {
            double apsLi = 1 / Li;
            val = Math.pow(Math.pow(Math.abs(points.get(i).getRealX() - points.get(j).getRealX()), Li)
                    + Math.pow(Math.abs(points.get(i).getRealY() - points.get(j).getRealY()), Li), apsLi);
        }
        return val;
    }

    /**
     *
     * @return the size of the list of ScalePoints.
     */
    public int getSize() {
        return points.size();
    }

    /**
     * Get the displayed x value of a point (scaled)
     *
     * @param i the index of the point in the list.
     * @return the x value of the point at index: i
     */
    public int getX(int i) {
        return points.get(i).getX();
    }

    /**
     * Get the displayed y value of a point (scaled)
     *
     * @param i the index of the point in the list.
     * @return the y value of the point at index: i
     */
    public int getY(int i) {
        return points.get(i).getY();
    }

    /**
     * Get the real x value of a point (not scaled)
     *
     * @param i the index of the point in the list.
     * @return the x value of the point at index: i
     */
    protected double getRealX(int i) {
        return points.get(i).getRealX();
    }

    /**
     * Get the real y value of a point (not scaled)
     *
     * @param i the index of the point in the list.
     * @return the y value of the point at index: i
     */
    protected double getRealY(int i) {
        return points.get(i).getRealY();
    }
    /**
     * Set the distance function
     * @param Li the distance function using the l_Li distance
     */
    protected void setDistance(double Li) {
        this.Li = Li;
    }
    /**
     * Remove all the points from the list.
     */
    protected void clear() {
        points.clear();
    }
    /**
     * Check if the list is empty.
     * @return true if the list is empty and false otherwise.
     */
    protected boolean isEmpty() {
        return points.isEmpty();
    }
    /**
     * Add a point to the list.
     * @param p a scaled point.
     */
    protected void add(ScaledPoint p) {
        points.add(p);
    }
    /**
     * Get the scaled point at index: i
     * @param i the index of the point in the list.
     * @return the scaled point at index i.
     */
    protected ScaledPoint get(int i) {
        return points.get(i);
    }
    /**
     * Remove from the list the point at index: i.
     * @param i the index of the point in the list.
     */
    protected void remove(int i) {
        points.remove(i);
    }
}
