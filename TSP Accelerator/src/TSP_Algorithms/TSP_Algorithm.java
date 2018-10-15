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
package TSP_Algorithms;

import Main.Cost;
import java.util.ArrayList;

/**
 * This class provides skeletal for every "TSP" algorithm implementation.
 *
 * @author Pinhas Nisnevitch
 */
public abstract class TSP_Algorithm {

    // make sure this is the same as the actual class name!
    private final Cost cost_func;
    private int tour[];
    private double tour_cost;
    private String algo_name;

    /*
    ==========================================================
    MAKE SURE THIS IS THE SAME AS THE ACTUAL FUNCTIONS NAME!!!
    ==========================================================
     */
    public static final String GET_TOUR_FUNCTION_NAME = "getTour";
    public static final String GET_COST_FUNCTION_NAME = "getTourCost";
    public static final String GET_ALGORITHM_NAME = "getAlgorithmName";

    /**
     * The TSP tour should consist of the points indices and not the points
     * values!
     *
     * @param cost_func provides the cost between every 2 points.
     * @param points list of points indices that represents a complete graph.
     * @param algo_name the name of the TSP algorithm.
     */
    public TSP_Algorithm(Cost cost_func, ArrayList<Integer> points, String algo_name) {
        this.cost_func = cost_func;
        tour = new int[points.size()];
        tour_cost = 0;
        this.algo_name = algo_name;
    }
    final public String getAlgorithmName(){
        return algo_name;
    }
    /**
     * @return the cost of the "TSP" tour.
     */
    final public double getTourCost() {
        return tour_cost;
    }

    /**
     * @return the "TSP" tour where the i-th and (i+1)-th elements are edges and
     * also the last and first elements are an edge.
     */
    final public int[] getTour() {
        return tour;
    }

    /**
     * "setTour" must be used in any constructor of a "TSP Algorithm" class.
     *
     * =========================================================================
     * The TSP tour should consist of points indices and not the points values!
     * =========================================================================
     *
     * @param tour A Circular route where the first value and the last value are
     * unequal. e.g. if there is 3 points and the vertices are - 0, 1, 2 the
     * tour will be as follows: {0, 1, 2} where (0, 1), (1, 2) and (2, 0) are
     * edges.
     */
    final public void setTour(int[] tour) {
        this.tour = tour;
        for (int i = 0; i + 1 < tour.length; i++) {
            tour_cost += cost_func.getDistance(tour[i], tour[i + 1]);
        }
        if (tour.length > 1) {
            tour_cost += cost_func.getDistance(tour[tour.length - 1], tour[0]);
        }
    }

}
