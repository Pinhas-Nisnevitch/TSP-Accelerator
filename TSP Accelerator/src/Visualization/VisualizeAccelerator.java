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

import AcceleratorAlgorithm.Accelerator;
import AcceleratorAlgorithm.MSTWeightApproximation;
import AcceleratorAlgorithm.NetHierarchy;
import GUI.JPanelObserver;
import Main.Controller;
import Main.Cost;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * This class purpose is to visualize the TSP accelerator algorithm.
 *
 * @author Pinhas Nisnevitch
 * @see Accelerator
 */
public class VisualizeAccelerator {

    private VisualizeNetHierarchy visualize_net_hierarcht;
    private VisualizeMSTApproximation visualize_mst_approximation;
    private VisualizeTSPTours visualize_tsp_tours;
    private ArrayList<Color> colorList;
    private final ArrayList<Shapes> shapes;
    private final int smallest_diam;
    private final File save_path;
    private final JPanelObserver panel;
    private final Cost cost_func;
    private final int MAX_COLORS = 80;
    private int num_of_tours;
    private double tourCost;
    private long algo_time;
    
    /**
     * 
     * @param save_path the path to the displayed image.
     * @param panel the panel of the visualization.
     * @param shapes list of shapes - any shape in that list can be displayed.
     * @param smallest_diam the smallest Diameter which allowed on the panel.
     * @param tsp_solver_path the path to the jar file that contains the tsp
     * solver algorithms.
     * @param tsp_solver_name the name of the algorithm (class) which solves the
     * TSP.
     * @param cost_func provides the locations and size of the points.
     * @param points list of points indices that represents a complete graph.
     * @param hierarchy_depth the depth of the net hierarchy.
     */
    public VisualizeAccelerator(File save_path, JPanelObserver panel, ArrayList<Shapes> shapes, int smallest_diam,
            String tsp_solver_path, String tsp_solver_name, Cost cost_func, ArrayList<Integer> points, int hierarchy_depth, double BIGMST) {
        this.shapes = shapes;
        this.smallest_diam = smallest_diam;
        this.save_path = save_path;
        this.panel = panel;
        this.cost_func = cost_func;

        initColorList();
        algo_time = System.currentTimeMillis();
        new Accelerator(tsp_solver_path, tsp_solver_name, cost_func, points, this, hierarchy_depth, BIGMST);
        algo_time = System.currentTimeMillis() - algo_time; 
    }
    
    /**
     * 
     * @return The accelerator algorithm running time in millisecond.
     */
    public long getAlgoTimeInMillisec(){
        return algo_time;
    }
    
    /**
     * visualize the TSP Accelerator algorithm.
     * @throws InterruptedException 
     */
    public void startVisualization() throws InterruptedException {
        if (num_of_tours <= MAX_COLORS) {
            if (Controller.isRunning) {
                visualize_net_hierarcht.timer.start();
                while (visualize_net_hierarcht.timer.isRunning()) {
                    Thread.sleep(50);
                }
            }
            if (Controller.isRunning) {
                visualize_mst_approximation.timer.start();
                while (visualize_mst_approximation.timer.isRunning()) {
                    Thread.sleep(50);
                }
            }
            if (Controller.isRunning) {
                visualize_tsp_tours.timer.start();
                while (visualize_tsp_tours.timer.isRunning()) {
                    Thread.sleep(50);
                }
            }

        } else {
            JOptionPane.showMessageDialog(panel, "Unable to display more than 80 tours!", "Step By Step", JOptionPane.PLAIN_MESSAGE);
        }
    }
    /**
     * Initialization of the visualization of the net hierarchy. 
     * @param net_hierarchy the Net Hierarchy.
     */
    public void initVisualizeNetHierarchy(NetHierarchy net_hierarchy) {
        visualize_net_hierarcht = new VisualizeNetHierarchy(net_hierarchy, cost_func, shapes, panel, colorList, smallest_diam, save_path);
    }
    /**
     * Initialization of the visualization of the MST approximation. 
     * @param mst_approx the MST approximation.
     */
    public void initVisualizeMSTApproximation(MSTWeightApproximation mst_approx) {
        visualize_mst_approximation = new VisualizeMSTApproximation(mst_approx, cost_func, shapes, panel, colorList, smallest_diam, save_path);
    }
    /**
     * Initialization of the visualization of the TSP Tours. 
     * @param tourCost the cost of the TSP tour.
     * @param ordered_tours all the TSP tours by their creation order.
     */
    public void initVisualizeTSPTours(double tourCost, ArrayList<ArrayList<int[]>> ordered_tours) {

        visualize_tsp_tours = new VisualizeTSPTours(ordered_tours, cost_func,
                shapes, panel, colorList, smallest_diam, save_path);
        num_of_tours = ordered_tours.get(0).size();
        this.tourCost = tourCost;
    }
    /**
     * set 80 Colors for the visualization.
     */
    private void initColorList() {
        final Color[] colors = {
            new Color(214, 161, 119),
            new Color(108, 191, 235),
            new Color(236, 233, 118),
            new Color(249, 218, 215),
            new Color(96, 110, 119),
            new Color(250, 132, 182),
            new Color(11, 176, 116),
            new Color(244, 130, 34),
            new Color(255, 180, 237),
            new Color(234, 104, 96),
            new Color(190, 194, 193),
            new Color(224, 241, 251),
            new Color(118, 56, 45),
            new Color(252, 238, 77),
            new Color(245, 159, 138),
            new Color(233, 26, 90),
            new Color(228, 223, 245),
            new Color(22, 114, 113),
            new Color(136, 150, 161),
            new Color(127, 117, 108),
            new Color(151, 189, 228),
            new Color(255, 210, 253),
            new Color(246, 175, 185),
            new Color(244, 154, 34),
            new Color(47, 181, 96),
            new Color(245, 153, 166),
            new Color(251, 227, 215),
            new Color(1, 155, 199),
            new Color(201, 40, 144),
            new Color(198, 166, 79),
            new Color(249, 239, 118),
            new Color(234, 51, 97),
            new Color(220, 154, 120),
            new Color(246, 181, 179),
            new Color(63, 39, 53),
            new Color(184, 163, 72),
            new Color(238, 155, 199),
            new Color(212, 185, 106),
            new Color(198, 187, 185),
            new Color(23, 166, 154),
            new Color(250, 206, 197),
            new Color(56, 111, 142),
            new Color(239, 174, 30),
            new Color(97, 73, 49),
            new Color(2, 157, 159),
            new Color(240, 94, 107),
            new Color(224, 222, 233),
            new Color(143, 149, 145),
            new Color(133, 205, 215),
            new Color(248, 65, 201),
            new Color(251, 102, 74),
            new Color(57, 111, 183),
            new Color(196, 132, 43),
            new Color(151, 181, 241),
            new Color(11, 26, 33),
            new Color(210, 232, 149),
            new Color(24, 102, 104),
            new Color(239, 198, 46),
            new Color(83, 110, 77),
            new Color(39, 22, 38),
            new Color(243, 101, 185),
            new Color(5, 121, 110),
            new Color(29, 65, 117),
            new Color(242, 108, 45),
            new Color(212, 229, 63),
            new Color(252, 228, 194),
            new Color(22, 195, 186),
            new Color(248, 184, 25),
            new Color(166, 71, 43),
            new Color(118, 31, 148),
            new Color(186, 196, 208),
            new Color(232, 236, 175),
            new Color(252, 229, 211),
            new Color(114, 80, 177),
            new Color(130, 41, 64),
            new Color(68, 176, 91),
            new Color(228, 132, 48),
            new Color(206, 40, 116),
            new Color(134, 205, 73),
            new Color(119, 126, 92)
        };

        colorList = new ArrayList<>();
        colorList.addAll(Arrays.asList(colors));
        
    }
    /**
     * 
     * @return the tsp tour cost from the accelerator
     */
    public double getTourCost() {
        return tourCost;
    }
}
