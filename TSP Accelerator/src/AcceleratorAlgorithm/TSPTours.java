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
import Main.TSPLoader;
import java.util.ArrayList;

/**
 * This class purpose is to solve the TSP on a various of regions in a graph and
 * than to combine them into one tour.
 *
 * @author Pinhas Nisnevitch
 */
public class TSPTours {

    final private ArrayList<Integer> points;
    final private ArrayList<int[]> tours;
    private int[] tour;
    private Cost cost_func;

    /**
     *
     * @param net_hierarchy the net hierarchy.
     * @param mst_approx the MST approximation
     * @param points list of points indices that represents a complete graph.
     * @param cost_func provides the cost between every 2 points.
     * @param tsp_solver_path he path to the jar file that contains the tsp
     * solver algorithms.
     * @param tsp_solver_name the name of the algorithm (class) which solves the
     * TSP.
     * @throws Exception if there is a problem with loading the tsp solver
     * algorithm.
     */
    public TSPTours(NetHierarchy net_hierarchy, MSTWeightApproximation mst_approx, ArrayList<Integer> points,
            Cost cost_func, String tsp_solver_path, String tsp_solver_name) throws Exception {
        this.points = points;
        this.cost_func = cost_func;
        int numOfTours = mst_approx.regions.size();

        // loading the TSP solver.
        TSPLoader tsp_loader = new TSPLoader(tsp_solver_path, tsp_solver_name);

        tours = new ArrayList<>();
        for (int i = 0; i < numOfTours; i++) {
            tours.add(tsp_loader.solve_tsp(cost_func, mst_approx.regions.get(i)));
        }
        // combine all the tsp tours into one.
        mergeAll();

    }

    /**
     *
     * @param net_hierarchy the net hierarchy.
     * @param mst_approx the MST approximation
     * @param points list of points indices that represents a complete graph.
     * @param cost_func provides the cost between every 2 points.
     * @param tsp_solver_path he path to the jar file that contains the tsp
     * solver algorithms.
     * @param tsp_solver_name the name of the algorithm (class) which solves the
     * TSP.
     * @param orderd_tsp_tours a list to add the TSP tours by their creation
     * order.
     * @throws Exception if there is a problem with loading the tsp solver
     * algorithm.
     */
    public TSPTours(NetHierarchy net_hierarchy, MSTWeightApproximation mst_approx, ArrayList<Integer> points,
            Cost cost_func, String tsp_solver_path, String tsp_solver_name, ArrayList<ArrayList<int[]>> orderd_tsp_tours) throws Exception {

        this(net_hierarchy, mst_approx, points, cost_func, tsp_solver_path, tsp_solver_name);
        orderd_tsp_tours.add(0, tours);
        ArrayList<int[]> final_tour = new ArrayList<>();
        final_tour.add(tour);
        orderd_tsp_tours.add(final_tour);
    }

    /**
     * Merge a group of TSP tours into one tour.
     * ========================================================================
     * THIS ALGORITHM IS BASED ON THE FACT THAT ANY TSP TOUR HAS ONE AND ONLY
     * ONE JOINT POINT WITH SOME OTHER TOUR.
     * ========================================================================
     */
    private void mergeAll() {

        ArrayList<Integer> need_shortcuts = new ArrayList<>();
        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        boolean[] connected = new boolean[points.size()];
        int[][] graph = new int[points.size()][2];

        for (int[] tsp_tour : tours) {

            for (int i = 0; i < tsp_tour.length; i++) {
                int point = tsp_tour[i];
                if (connected[point]) {
                    int left_point = tsp_tour[(i + tsp_tour.length - 1) % tsp_tour.length];
                    int right_point = tsp_tour[(i + 1) % tsp_tour.length];

                    need_shortcuts.add(point);
                    left.add(left_point);
                    right.add(right_point);

                } else {
                    graph[point][0] = tsp_tour[(i + tsp_tour.length - 1) % tsp_tour.length];
                    graph[point][1] = tsp_tour[(i + 1) % tsp_tour.length];
                    connected[point] = true;
                }
            }
            while (!need_shortcuts.isEmpty()) {
                do_shortcut(graph, need_shortcuts, left, right);
            }

        }
        // sets the final TSP tour.
        setTour(graph);

    }

    /**
     *
     * @return The number of tours before the merge.
     */
    public int getNumberOfTours() {
        return tours.size();
    }

    /**
     * finds the index of the smallest number in array of 4 elements.
     *
     * @param dists array of 4 numbers.
     * @return the index of the smallest number or -1 in case of non-four
     * elements array.
     */
    private int min4Num(double[] dists) {
        if (dists.length != 4) {
            return -1;
        }
        int min1 = 0;
        int min2 = 2;
        if (dists[1] < dists[0]) {
            min1 = 1;
        }
        if (dists[3] < dists[2]) {
            min2 = 3;
        }
        int min_index = min1;
        if (dists[min2] < dists[min1]) {
            min_index = min2;
        }
        return min_index;
    }

    /**
     * using the triangle inequality in order to create shorter TSP tour.
     *
     * @param graph a representation of the TSP tour.
     * @param need_shortcuts a list of points that needs to use shortcuts.
     * @param left a list of points that are connected to the points in
     * need_shortcuts from one side respectively to "need_shortcuts".
     * @param right a list of points that are connected to the points in
     * need_shortcuts from the other side respectively to "need_shortcuts".
     */
    private void do_shortcut(int[][] graph, ArrayList<Integer> need_shortcuts, ArrayList<Integer> left,
            ArrayList<Integer> right) {
        int connection_point = need_shortcuts.remove(need_shortcuts.size() - 1);
        int v1 = left.remove(left.size() - 1);
        int v2 = right.remove(right.size() - 1);

        int u1 = graph[connection_point][0];
        int u2 = graph[connection_point][1];

        double dists[] = new double[4];
        dists[0] = cost_func.getDistance(u1, v2)
                + cost_func.getDistance(connection_point, u2)
                + cost_func.getDistance(connection_point, v1);

        dists[1] = cost_func.getDistance(u2, v2)
                + cost_func.getDistance(connection_point, u1)
                + cost_func.getDistance(connection_point, v1);

        dists[2] = cost_func.getDistance(u2, v1)
                + cost_func.getDistance(connection_point, u1)
                + cost_func.getDistance(connection_point, v2);

        dists[3] = cost_func.getDistance(u1, v1)
                + cost_func.getDistance(connection_point, u2)
                + cost_func.getDistance(connection_point, v2);
        int minimum = min4Num(dists);

        switch (minimum) {
            case 0: {
                graph[connection_point][0] = v1;
                if (graph[u1][0] == connection_point) {
                    graph[u1][0] = v2;
                } else if (graph[u1][1] == connection_point) {
                    graph[u1][1] = v2;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == u1) {
                            if (left.get(i) == connection_point) {
                                left.set(i, v2);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, v2);
                            }
                            break;
                        }
                    }
                }
                if (graph[v2][0] == connection_point) {
                    graph[v2][0] = u1;
                } else if (graph[v2][1] == connection_point) {
                    graph[v2][1] = u1;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == v2) {
                            if (left.get(i) == connection_point) {
                                left.set(i, u1);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, u1);
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case 1: {
                graph[connection_point][1] = v1;
                if (graph[u2][0] == connection_point) {
                    graph[u2][0] = v2;
                } else if (graph[u2][1] == connection_point) {
                    graph[u2][1] = v2;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == u2) {
                            if (left.get(i) == connection_point) {
                                left.set(i, v2);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, v2);
                            }
                            break;
                        }
                    }
                }

                if (graph[v2][0] == connection_point) {
                    graph[v2][0] = u2;
                } else if (graph[v2][1] == connection_point) {
                    graph[v2][1] = u2;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == v2) {
                            if (left.get(i) == connection_point) {
                                left.set(i, u2);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, u2);
                            }
                            break;
                        }
                    }
                }

                break;
            }
            case 2: {
                graph[connection_point][1] = v2;
                if (graph[u2][0] == connection_point) {
                    graph[u2][0] = v1;
                } else if (graph[u2][1] == connection_point) {
                    graph[u2][1] = v1;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == u2) {
                            if (left.get(i) == connection_point) {
                                left.set(i, v1);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, v1);
                            }
                            break;
                        }
                    }
                }
                if (graph[v1][0] == connection_point) {
                    graph[v1][0] = u2;
                } else if (graph[v1][1] == connection_point) {
                    graph[v1][1] = u2;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == v1) {
                            if (left.get(i) == connection_point) {
                                left.set(i, u2);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, u2);
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case 3: {
                graph[connection_point][0] = v2;
                if (graph[u1][0] == connection_point) {
                    graph[u1][0] = v1;
                } else if (graph[u1][1] == connection_point) {
                    graph[u1][1] = v1;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == u1) {
                            if (left.get(i) == connection_point) {
                                left.set(i, v1);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, v1);
                            }
                            break;
                        }
                    }
                }
                if (graph[v1][0] == connection_point) {
                    graph[v1][0] = u1;
                } else if (graph[v1][1] == connection_point) {
                    graph[v1][1] = u1;
                } else {
                    for (int i = 0; i < need_shortcuts.size(); i++) {
                        if (need_shortcuts.get(i) == v1) {
                            if (left.get(i) == connection_point) {
                                left.set(i, u1);
                            } else if (right.get(i) == connection_point) {
                                right.set(i, u1);
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }

    }

    /**
     * Convert the graph representation of the TSP tour into 1D array
     * representation.
     *
     * @param graph a representation of the TSP tour.
     */
    private void setTour(int[][] graph) {
        tour = new int[points.size()];
        tour[0] = 0;
        int prev1 = 0, prev2 = 0;
        int current;
        int count = 1;
        while (count < tour.length) {
            current = graph[prev1][0];
            if (current == prev2) {
                current = graph[prev1][1];
            }
            tour[count++] = current;
            prev2 = prev1;
            prev1 = current;

        }
    }

    /**
     * @return the merged TSP tour.
     */
    public int[] getTour() {
        return tour;
    }

    /**
     *
     * @return the cost of the merged TSP tour.
     */
    public double getTourCost() {
        double cost = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            cost += cost_func.getDistance(tour[i], tour[i + 1]);
        }
        if (tour.length > 1) {
            cost += cost_func.getDistance(tour[tour.length - 1], tour[0]);
        }
        return cost;
    }

}
