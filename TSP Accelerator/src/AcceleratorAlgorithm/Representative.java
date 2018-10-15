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
package AcceleratorAlgorithm;

import java.util.ArrayList;

/**
 * This class represents a point in a net hierarchy.
 *
 * @author Pinhas Nisnevitch
 * @see NetHierarchy
 */
public class Representative {

    private final int point_id;
    private final ArrayList<Integer> childrens;
    private final ArrayList<Integer> neighbors;
    private final int previous_rep;

    /**
     *
     * @param point_id the id of the point in the original graph.
     * @param neighbors a list of the point neighbors.
     * @param representative the id of the point representative in the previous
     * level.
     */
    public Representative(int point_id, ArrayList<Integer> neighbors, int representative) {
        this.point_id = point_id;
        this.neighbors = neighbors;
        this.previous_rep = representative;
        childrens = new ArrayList<>();
    }

    /**
     *
     * @return the children's of the point in the current level.
     */
    public ArrayList<Integer> getChildrens() {
        return childrens;
    }

    /**
     *
     * @return the neighbors of the point in current level.
     */
    public ArrayList<Integer> getNeighbors() {
        return neighbors;
    }

    /**
     *
     * @return the representative of the point in the previous level.
     */
    public int getRepresentative() {
        return previous_rep;
    }

    /**
     *
     * @return the id of the point in the original graph.
     */
    public int getPointId() {
        return point_id;
    }

    @Override
    public String toString() {
        String str = "ID: " + point_id + "\n"
                + "Son of: " + previous_rep + "\n"
                + "Neighbors: " + neighbors + "\n"
                + "Sons: " + childrens;
        return str;
    }
}
