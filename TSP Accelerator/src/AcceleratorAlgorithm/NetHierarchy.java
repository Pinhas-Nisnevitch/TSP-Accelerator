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

import Main.Cost;
import java.util.ArrayList;
import static java.util.Collections.shuffle;
import java.util.Iterator;
import java.util.Random;

/**
 *
 *
 * =============================================================================
 * "Net Hierarchy" is based on the paper: "Robert Krauthgamer and James R_i.
 * Lee. Navigating nets: Simple algorithms for proximity search. In 15th Annual
 * ACM-SIAM Symposium on Discrete Algorithms, pages 791–801, January 2004."
 * =============================================================================
 *
 * @author Pinhas Nisnevitch
 */
public class NetHierarchy {

    public static int DEPTH_SIZE;

    // The radius at level: "DEPTH_SIZE"
    private double radius;
    // first point is always the same.
    private int radius_secod_point_index;
    private int[][] rep_indices_at_level_i;
    private ArrayList<Representative>[] h_levels;

    /**
     * Build Net Hierarchy from a "complete graph" representation.
     *
     * @param cost_func provides the cost between every 2 points.
     * @param depth_size the depth of the hierarchy.
     */
    public NetHierarchy(Cost cost_func, int depth_size) {
        assert (depth_size > 1);
        DEPTH_SIZE = depth_size;
        build_hierarchy(cost_func);
    }

    /**
     * Build Net Hierarchy from a "complete graph" representation.
     *
     * @param cost_func provides the cost between every 2 points.
     */
    public NetHierarchy(Cost cost_func) {
        // The maximum depth of the hierarchy (2^-10 is small enough)
        DEPTH_SIZE = 10;
        build_hierarchy(cost_func);
    }

    /**
     * Build Net Hierarchy from a "complete graph" representation.
     *
     * @param cost_func cost_func provides the cost between every 2 points.
     */
    private void build_hierarchy(Cost cost_func) {
        // The hierarcy always start from the first point in the set (for convenience reasons)
        final int arbitrary_point_ind = 0;
        h_levels = new ArrayList[DEPTH_SIZE];
        for (int i = 0; i < DEPTH_SIZE; i++) {
            h_levels[i] = new ArrayList<>();
        }
        rep_indices_at_level_i = new int[DEPTH_SIZE][cost_func.getSize()];
        rep_indices_at_level_i[DEPTH_SIZE - 1][arbitrary_point_ind] = 1;

        // stores every point until that point is a "representative" in some level of the hierarchy.
        ArrayList<Integer> not_a_rep = new ArrayList<>();

        // setting the arbitrary point to be the "representative" of all points at level "DEPTH_SIZE"
        int[] representatives = new int[cost_func.getSize()];

        //finds the radius of the the highest level(depth size)
        //int arbitraryPoint = points.get(arbitrary_point_ind);
        for (int i = 1; i < cost_func.getSize(); i++) {
            double tempDist = cost_func.getDistance(arbitrary_point_ind, i);
            if (tempDist > radius) {
                radius = tempDist;
                radius_secod_point_index = i;
            }
        }

        //the arbitrary point dosn't enter to the "not a representative" list
        for (int i = 1; i < cost_func.getSize(); i++) {
            not_a_rep.add(i);
        }

        // the list hold the neighbors of some point from level i at the hierarchy.
        ArrayList<Integer> neighbors_i = new ArrayList<>();

        neighbors_i.add(arbitrary_point_ind);

        h_levels[DEPTH_SIZE - 1].add(new Representative(arbitrary_point_ind, neighbors_i, -1));
        // the radius of the current level.
        double R_i = radius;

        for (int i = DEPTH_SIZE - 2; i > 0; i--) {
            //update the radius of the current level.
            R_i /= 2;

            // every "representative" in level "i+1" is a "representative" in level "i".
            for (Representative rep_level_i_plus_1 : h_levels[i + 1]) {

                int rep_index = rep_level_i_plus_1.getPointId();
                //every point is the "representative" of itself.
                rep_level_i_plus_1.getChildrens().add(rep_index);

                // update the neighbors of the current "representative" in the current level
                neighbors_i = new ArrayList<>();
                neighbors_i.add(rep_index);
                //int currentRep = points.get(rep_index);
                for (Integer neighbor : rep_level_i_plus_1.getNeighbors()) {
                    if (cost_func.getDistance(rep_index, neighbor) <= 4 * R_i) {
                        neighbors_i.add(neighbor);
                    }
                }
                rep_indices_at_level_i[i][rep_index] = h_levels[i].size() + 1;
                h_levels[i].add(new Representative(rep_index, neighbors_i, rep_index));

            }
            shuffle(not_a_rep);
            // finding new "representatives" to the current level
            // (the first point with distance smaller then the 4*radius of the level.)
            Iterator<Integer> candidates = not_a_rep.iterator();

            while (candidates.hasNext()) {
                int rep_candidate = candidates.next();
                //int repCandidatePoint = points.get(rep_candidate);
                int rep = representatives[rep_candidate];
                //list of the potential neighbors of rep
                neighbors_i = new ArrayList<>();

                // every "representative" is the neighbor of itself
                neighbors_i.add(rep_candidate);

                boolean newRep = true;
                ArrayList<Integer> randomPotentialList = new ArrayList<>();

                for (int neighbor : h_levels[i + 1].get(rep_indices_at_level_i[i + 1][rep] - 1).getNeighbors()) {
                    randomPotentialList.addAll(h_levels[i + 1]
                            .get(rep_indices_at_level_i[i + 1][neighbor] - 1).getChildrens());
                }
                shuffle(randomPotentialList);
                Iterator<Integer> potential = randomPotentialList.iterator();

                while (potential.hasNext() && newRep) {

                    Integer rep_at_i = potential.next();

                    if (cost_func.getDistance(rep_candidate, rep_at_i) <= R_i) {
                        newRep = false;
                        representatives[rep_candidate] = rep_at_i;
                    } else {
                        neighbors_i.add(rep_at_i);
                    }
                }

                if (newRep) {
                    ArrayList<Integer> final_neighbors = new ArrayList<>();
                    final_neighbors.add(rep_candidate);
                    for (int neighbor : neighbors_i) {
                        if (cost_func.getDistance(rep_candidate, neighbor) <= 4 * R_i) {
                            // update the neighbors
                            final_neighbors.add(neighbor);
                        }
                    }
                    for (int nei_ind = 1; nei_ind < final_neighbors.size(); nei_ind++) {
                        h_levels[i].get(rep_indices_at_level_i[i][final_neighbors.get(nei_ind)] - 1)
                                .getNeighbors().add(rep_candidate);
                    }
                    rep_indices_at_level_i[i][rep_candidate] = h_levels[i].size() + 1;
                    h_levels[i].add(new Representative(rep_candidate, final_neighbors, rep));

                    // remove the current point from the "not a representative" list
                    candidates.remove();

                    h_levels[i + 1].get(rep_indices_at_level_i[i + 1][rep] - 1).getChildrens().add(rep_candidate);

                }

            }
        }

        // level 0 is a special case (every point is a "representative").
        R_i /= 2;
        // every "representative" in level "1" is a "representative" in level "0".
        for (Representative p_lv1 : h_levels[1]) {

            int rep_index = p_lv1.getPointId();
            p_lv1.getChildrens().add(rep_index);

            // update the neighbors of the current "representative" in level "0".    
            //in level 0 the neigbores are in the range of "3*radius" from each other.
            neighbors_i = new ArrayList<>();
            neighbors_i.add(rep_index);
            for (int neighbor : p_lv1.getNeighbors()) {
                if (cost_func.getDistance(rep_index, neighbor) <= 3 * R_i) {
                    neighbors_i.add(neighbor);
                }
            }

            rep_indices_at_level_i[0][rep_index] = h_levels[0].size() + 1;
            h_levels[0].add(new Representative(rep_index, neighbors_i, rep_index));
        }

        for (int rep_candidate : not_a_rep) {

            int rep = representatives[rep_candidate];
            //list of the potential neighbors of rep
            neighbors_i = new ArrayList<>();
            // every "representative" is the neighbor of itself.
            neighbors_i.add(rep_candidate);

            // update all the new neighbores at level 0
            for (int neighbor : h_levels[1].get(rep_indices_at_level_i[1][rep] - 1).getNeighbors()) {
                for (int potential_neighbor : h_levels[1].get(rep_indices_at_level_i[1][neighbor] - 1).getChildrens()) {
                    if (cost_func.getDistance(rep_candidate, potential_neighbor) <= 3 * R_i) {
                        //update the 2 points as neighbers.
                        neighbors_i.add(potential_neighbor);
                        h_levels[0].get(rep_indices_at_level_i[0][potential_neighbor] - 1).getNeighbors().add(rep_candidate);
                    }
                }
            }

            h_levels[1].get(rep_indices_at_level_i[1][rep] - 1).getChildrens().add(rep_candidate);

            rep_indices_at_level_i[0][rep_candidate] = h_levels[0].size() + 1;
            h_levels[0].add(new Representative(rep_candidate, neighbors_i, rep));

        }
    }

    /**
     *
     * @return The radius of level "DEPTH_SIZE - 1"
     */
    public double getMaxRadius() {
        return radius;
    }

    /**
     *
     * @return The hierarchy by it's levels.
     */
    public ArrayList<Representative>[] getHierarchyLevels() {
        return h_levels;
    }

    /**
     * get a representative in a certain level.
     *
     * @param level the level in the hierarchy.
     * @param index the id of the representative.
     * @return the representative.
     */
    public Representative get(int level, int index) {
        return h_levels[level].get(rep_indices_at_level_i[level][index] - 1);
    }

    /**
     * get the location of a representative in a certain level.
     *
     * @param level the level in the hierarchy.
     * @param index the id of the representative.
     * @return the location of the representative.
     */
    public int getIndex(int level, int index) {
        return rep_indices_at_level_i[level][index] - 1;
    }

    /**
     * =========================================================================
     * THE FIRST POINT THAT CONSTRUCT THE RADIUS IS ALWAYS THE ARBITRARY POINT.
     * =========================================================================
     *
     * @return the id of the second point that construct the radius.
     */
    public int getRadiusIndexPoint() {
        return radius_secod_point_index;
    }

    /**
     * get the amount of times that a point exists as a representative from a
     * certain level to the last level.
     *
     * @param point the point.
     * @param from_level the level to start from.
     * @return the amount of times that a point exists as a representative.
     */
    public int strength_of_point(int point, int from_level) {
        if (from_level < 0);
        from_level = 0;
        int strength = 0;
        for (int i = from_level; i < DEPTH_SIZE; i++) {
            if (getIndex(i, point) != -1) {
                strength++;
            }
        }
        return strength;
    }
}
