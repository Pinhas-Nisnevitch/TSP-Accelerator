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

import Main.Cost;
import java.util.ArrayList;
import TSP_Algorithms.TSP_Algorithm;

/**
 * This class implements the Nearest Neighbor method for the Travelling salesman
 * problem (TSP).
 *
 * ===========================================================================
 * The TSP tour should consist of points indices and not the points values!
 * ===========================================================================
 *
 * @author Pinhas Nisnevitch
 * @see TSP_Algorithm
 */
public class NearestNeighbor extends TSP_Algorithm {
    private final static String ALGORITHM_NAME = "Nearest Neighbor";
    /**
     *
     * @param cost_func provides the cost between every 2 points.
     * @param points list of points indices that represents a complete graph.
     */
    public NearestNeighbor(Cost cost_func, ArrayList<Integer> points) {
        super(cost_func, points, ALGORITHM_NAME);

        boolean visted[] = new boolean[points.size()];
        int[] tour = new int[points.size()];

        // start the tour from the first point in the list
        int tour_index = 0;
        int current_tour_size = 0;
        tour[current_tour_size++] = tour_index;
        visted[tour_index] = true;

        // finding the nearest point to the last point that enterd the "tour"
        while (current_tour_size < points.size()) {
            double smallest_cost = Integer.MAX_VALUE;
            for (int i = 1; i < points.size(); i++) {
                if (!visted[i]) {
                    double current_cost = cost_func.getDistance(points.get(tour[current_tour_size - 1]), points.get(i));
                    if (current_cost < smallest_cost) {
                        tour_index = i;
                        smallest_cost = current_cost;
                    }
                }
            }

            tour[current_tour_size++] = tour_index;
            visted[tour_index] = true;
        }

        /* =================================================
         * THIS IS A MUST CALL IN ANY TSP ALGORITHM!!!
         * =================================================
         */
        setTour(tour);
    }
}
