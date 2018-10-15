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
import TSP_Algorithms.TSP_Algorithm;
import Visualization.VisualizeAccelerator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the TSP Accelerator algorithm.
 *
 * @author Pinhas Nisnevitch
 */
public class Accelerator extends TSP_Algorithm {
    private final static String ALGORITHM_NAME = "Accelerator";

    public Accelerator(String tsp_solver_path, String tsp_solver_name, Cost cost_func, ArrayList<Integer> points, int hierarchy_depth, double BIGMST) {
        this(tsp_solver_path, tsp_solver_name, cost_func, points, null, hierarchy_depth, BIGMST);

    }

    /**
     *
     * @param tsp_solver_path the path to the jar file that contains the tsp
     * solver algorithms.
     * @param tsp_solver_name the name of the algorithm (class) which solves the
     * TSP.
     * @param cost_func provides the cost between every 2 points.
     * @param points list of points indices that represents a complete graph.
     * @param v_accelerator if not null set a visualization for the algorithm.
     * @param hierarchy_depth the depth of the net hierarchy.
     * @param mstThreshold the MST threshold
     */
    public Accelerator(String tsp_solver_path, String tsp_solver_name, Cost cost_func, ArrayList<Integer> points, VisualizeAccelerator v_accelerator, int hierarchy_depth, double mstThreshold) {
        super(cost_func, points, ALGORITHM_NAME);
        NetHierarchy net_hierarchy = new NetHierarchy(cost_func, hierarchy_depth);
        MSTWeightApproximation mst_approx = new MSTWeightApproximation(net_hierarchy, points, cost_func, mstThreshold);
        TSPTours tsp_tours = null;

        try {
            if (v_accelerator == null) {
                tsp_tours = new TSPTours(net_hierarchy, mst_approx, points, cost_func, tsp_solver_path, tsp_solver_name);
            } else {
                ArrayList<ArrayList<int[]>> ordered_tours = new ArrayList<>();
                tsp_tours = new TSPTours(net_hierarchy, mst_approx, points, cost_func, tsp_solver_path, tsp_solver_name, ordered_tours);
                v_accelerator.initVisualizeNetHierarchy(net_hierarchy);
                v_accelerator.initVisualizeMSTApproximation(mst_approx);
                v_accelerator.initVisualizeTSPTours(tsp_tours.getTourCost(), ordered_tours);
            }
        } catch (Exception ex) {
            Logger.getLogger(Accelerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tsp_tours != null) {
            setTour(tsp_tours.getTour());
        }
    }

}
