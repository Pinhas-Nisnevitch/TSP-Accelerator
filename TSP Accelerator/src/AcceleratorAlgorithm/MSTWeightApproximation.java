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
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class implements approximation to the MST weight in a complete graph.
 *
 * @author Pinhas Nisnevitch
 */
public class MSTWeightApproximation {

    // a factor for the limit calculation
    final static double LIMIT_FACTOR = 3;

    // all the regions with "big" MST weight by their creation order.
    public ArrayList<ArrayList<Integer>> regions;
    // the levels on which a region with "big" MST weight was found respectively to the region list.
    public ArrayList<Integer> groups_exit_level;

    // the radiuses in each level of the net hierarchy.
    public double[] radiuses;
    // mark every point that exist from the graph.
    private final boolean[] is_out;
    private final double mstThreshold;

    /**
     *
     * @param net_hierarchy the net hierarchy
     * @param points list of points indices that represents a complete graph.
     * @param cost_func provides the cost between every 2 points.
     * @param mstThreshold the threshold for area with big MST weight.
     */
    public MSTWeightApproximation(NetHierarchy net_hierarchy, ArrayList<Integer> points, Cost cost_func, double mstThreshold) {
        this.mstThreshold = mstThreshold;
        HashMap<Integer, Double>[] mstApproxGraph = new HashMap[points.size()];

        is_out = new boolean[points.size()];
        regions = new ArrayList<>();
        groups_exit_level = new ArrayList<>();
        // get the radiuses of all levles in the hierarchy
        radiuses = new double[NetHierarchy.DEPTH_SIZE];
        radiuses[NetHierarchy.DEPTH_SIZE - 1] = net_hierarchy.getMaxRadius();
        for (int i = NetHierarchy.DEPTH_SIZE - 2; i >= 0; i--) {
            radiuses[i] = radiuses[i + 1] / 2;
        }

        double[] sumsOfMST = new double[points.size()];
        int num_of_rep_at_level_i = 0;

        for (Representative rep : net_hierarchy.getHierarchyLevels()[0]) {
            HashMap<Integer, Double> neighborsGraph = new HashMap<>();
            int rep_index = rep.getPointId();
            int rep_point = points.get(rep_index);
            Iterator neighbors = rep.getNeighbors().iterator();

            //the first child of every point is itself
            neighbors.next();
            while (neighbors.hasNext()) {
                int neighbor = (int) neighbors.next();
                double dist = cost_func.getDistance(rep_point, neighbor);
                sumsOfMST[num_of_rep_at_level_i] += dist;
                neighborsGraph.put(neighbor, dist);
            }
            mstApproxGraph[rep_index] = neighborsGraph;
            num_of_rep_at_level_i++;
        }

        findAreasWithBigMSTWeight(net_hierarchy, sumsOfMST, num_of_rep_at_level_i, radiuses[0], 0, mstApproxGraph);

        for (int i = 1; i < NetHierarchy.DEPTH_SIZE; i++) {
            double[] sumsOfMST_i = new double[net_hierarchy.getHierarchyLevels()[i].size()];
            HashMap<Integer, Double>[] graph_at_level_i = new HashMap[points.size()];
            num_of_rep_at_level_i = 0;

            double limit = LIMIT_FACTOR * radiuses[i];
            for (Representative rep : net_hierarchy.getHierarchyLevels()[i]) {
                Iterator itr = (Iterator<Integer>) rep.getChildrens().iterator();
                double sum = 0;
                while (itr.hasNext()) {
                    int ind = (int) itr.next();
                    sum += sumsOfMST[net_hierarchy.getIndex(i - 1, ind)];

                }
                sumsOfMST_i[num_of_rep_at_level_i++] = sum;

            }

            for (Representative rep : net_hierarchy.getHierarchyLevels()[i]) {
                int rep_index = rep.getPointId();
                graph_at_level_i[rep_index] = new HashMap<>();
                int rep_point = points.get(rep_index);
                Iterator neighbors = rep.getNeighbors().iterator();
                neighbors.next();
                while (neighbors.hasNext()) {
                    int neighbor = (int) neighbors.next();

                    double rep2neighbor_dist = cost_func.getDistance(rep_point, neighbor);
                    if (rep2neighbor_dist < limit) {

                        double rep2neighbor_path_cost = ShortestPath.BFS_Search(mstApproxGraph, 2 * limit, rep_index, neighbor);

                        if (rep2neighbor_path_cost > 0) {
                            rep2neighbor_dist = -rep2neighbor_path_cost;
                        } else {
                            sumsOfMST_i[net_hierarchy.getIndex(i, rep_index)] += rep2neighbor_dist;
                        }
                        graph_at_level_i[rep_index].put(neighbor, rep2neighbor_dist);

                    }

                }
            }
            mstApproxGraph = graph_at_level_i;

            findAreasWithBigMSTWeight(net_hierarchy, sumsOfMST_i, num_of_rep_at_level_i, radiuses[i], i, mstApproxGraph);

            sumsOfMST = sumsOfMST_i;

        }
        ArrayList<Integer> final_area = new ArrayList<>();

        for (int i = 0; i < is_out.length; i++) {
            if (!is_out[i]) {
                final_area.add(points.get(i));
            }
        }
        if (final_area.size() > 1) {
            regions.add(final_area);
            groups_exit_level.add(NetHierarchy.DEPTH_SIZE - 1);
        }

    }

    /**
     * mark any group of points in a certain level that has "big" MST weight.
     *
     * @param net_hierarchy the net hierarchy.
     * @param sumsOfMST the weight of an area around any point in the graph on a
     * certain level.
     * @param num_of_reps the amount of representatives in a certain level.
     * @param radius the radius of a certain level in the hierarchy.
     * @param level the level of the hierarchy.
     * @param graph_on_level_i a representation of the MST approximation graph
     * on a certain level.
     */
    private void findAreasWithBigMSTWeight(NetHierarchy net_hierarchy, double[] sumsOfMST,
            int num_of_reps, double radius, int level, HashMap<Integer, Double>[] graph_on_level_i) {
        HashSet<Integer> restore = new HashSet<>();
        for (int i = 0; i < num_of_reps; i++) {
            Representative rep = net_hierarchy.getHierarchyLevels()[level].get(i);
            int rep_id = rep.getPointId();
            if (!is_out[rep_id]) {
                double currentSum = 0;

                Iterator<Integer> ne = rep.getNeighbors().iterator();

                while (ne.hasNext()) {
                    currentSum += sumsOfMST[net_hierarchy.getIndex(level, ne.next())];

                }

                if (currentSum >= mstThreshold * radius) {

                    ArrayList<Integer> potential_tsp_area = new ArrayList<>();
                    ne = (Iterator<Integer>) net_hierarchy.get(level, rep_id).getNeighbors().iterator();
                    while (ne.hasNext()) {
                        int ind = (int) ne.next();
                        sumsOfMST[net_hierarchy.getIndex(level, ind)] = 0;
                        if (!is_out[ind]) {
                            markGroups(net_hierarchy, ind, level, potential_tsp_area);
                        }

                    }
                    if (potential_tsp_area.size() > 1) {
                        int stronget_point = -1;
                        int strongets_point_strength = -1;
                        for (int index_of_rep : potential_tsp_area) {
                            int temp_strength = net_hierarchy.strength_of_point(index_of_rep, level);
                            if (temp_strength > strongets_point_strength) {
                                stronget_point = index_of_rep;
                                strongets_point_strength = temp_strength;
                            }

                        }
                        if (stronget_point != -1) {
                            restore.add(stronget_point);
                        }
                        for (int out : potential_tsp_area) {
                            is_out[out] = true;
                            if (graph_on_level_i[out] != null) {
                                Iterator<Integer> connected_verticies = graph_on_level_i[out].keySet().iterator();
                                while (connected_verticies.hasNext()) {
                                    int vertix = connected_verticies.next();
                                    if (graph_on_level_i[vertix] != null) {
                                        graph_on_level_i[vertix].remove(out);
                                        connected_verticies.remove();
                                    }

                                }
                            }
                        }
                        regions.add(potential_tsp_area);
                        groups_exit_level.add(level + 1);

                    }
                }
            }
        }

        restore.stream().filter((rep) -> (rep != -1)).forEachOrdered((rep) -> {
            is_out[rep] = false;
        });

    }

    /**
     * recursively mark an area as a region with "big" MST weight.
     *
     * @param net_hierarchy the net hierarchy.
     * @param rep_id the id of the representative.
     * @param level the level in which the area of the representative is marked.
     * @param area_with_big_MST_weight a list to add the points of the "big" MST
     * weight area.
     */
    private void markGroups(NetHierarchy net_hierarchy, int rep_id, int level,
            ArrayList<Integer> area_with_big_mst_weight) {
        if (level == 0) {
            area_with_big_mst_weight.add(rep_id);
        } else {
            Iterator itr = (Iterator<Integer>) net_hierarchy.get(level, rep_id).getChildrens().iterator();
            while (itr.hasNext()) {
                rep_id = (int) itr.next();
                if (!is_out[rep_id]) {
                    markGroups(net_hierarchy, rep_id, level - 1, area_with_big_mst_weight);
                }
            }
        }
    }

}
