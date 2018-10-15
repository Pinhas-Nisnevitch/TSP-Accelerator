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

import TSP_Algorithms.TSP_Algorithm;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class purpose is to load a TSP algorithm from a jar file containing
 * ".class" files.
 * ==========================================================================
 * YOU SHOULD NEVER TRY TO LOAD ALGORITHMS FROM AN UNKNOWN SOURCE!
 * ==========================================================================
 *
 * @author Pinhas Nisnevitch
 * @see TSP_Algorithm
 */
public final class TSPLoader {

    private Constructor tsp_constructor;
    private Method get_tour_method;

    /**
     *
     * @param jar_path the path to the jar file containing the tsp solver.
     * @param tsp_class_name the path to the tsp solver name in the jar file.
     * @throws Exception in case of: wrong path, bad implementation of the tsp
     * solver, not a "TSP_Algorithm" class, etc.
     */
    public TSPLoader(String jar_path, String tsp_class_name) throws Exception {
        // load a class from a jar file.
        Class tsp_solver_class = loadClassFromJar(jar_path, tsp_class_name);
        // verify that this class is a tsp solver.
        LoadTSPSolverProperties(tsp_solver_class);
    }

    /**
     * Load a class from a jar file.
     *
     * @param jar_path the path to the jar file.
     * @param class_name the path to the class in the jar file.
     * @return the class.
     * @throws MalformedURLException either no legal protocol could be found in
     * a specification string or the string could not be parsed.
     * @throws ClassNotFoundException no definition for the class with the
     * specified name could be found.
     */
    private Class loadClassFromJar(String jar_path, String class_name) throws MalformedURLException, ClassNotFoundException {
        File file = new File(jar_path);
        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};
        URLClassLoader cl = new URLClassLoader(urls);
        Class<?> cls = cl.loadClass(class_name);
        return cls;
    }

    /**
     * load the tsp solver properties
     *
     * @param tsp_solver_class a class of the "TSP_Algorithm".
     * @throws NoSuchMethodException if not a "TSP_Algorithm" class.
     * @throws IllegalAccessException if not a "TSP_Algorithm" class.
     * @throws IllegalArgumentException if not a "TSP_Algorithm" class.
     * @throws InvocationTargetException if not a "TSP_Algorithm" class.
     * @throws Exception if not a "TSP_Algorithm" class or a bad implementation.
     */
    private void LoadTSPSolverProperties(Class tsp_solver_class) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {

        //create dummy example of a tsp
        ArrayList<ScaledPoint> dummy_points = new ArrayList<>();
        dummy_points.add(new ScaledPoint(0, 0));
        dummy_points.add(new ScaledPoint(1, 1));
        dummy_points.add(new ScaledPoint(0, 2));

        // the cost will be calculate by the L_2 distance.
        Cost dummy_cost = new Cost(dummy_points, 2);

        ArrayList<Integer> dummy_points_indices = new ArrayList<>();
        dummy_points_indices.add(0); // index for the: (0, 0) point.
        dummy_points_indices.add(1); // index for the: (1, 1) point.
        dummy_points_indices.add(2); // index for the: (0, 2) point.

        Object dummyTSPobj = null;
        for (int i = 0; i < tsp_solver_class.getConstructors().length; i++) {
            try {
                tsp_constructor = tsp_solver_class.getConstructors()[i];
                dummyTSPobj = tsp_constructor.newInstance((Object) dummy_cost, (Object) dummy_points_indices);
                i = tsp_solver_class.getConstructors().length;
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
            }
        }
        get_tour_method = tsp_solver_class.getMethod(TSP_Algorithm.GET_TOUR_FUNCTION_NAME);
        Method dummy_method_cost = tsp_solver_class.getMethod(TSP_Algorithm.GET_COST_FUNCTION_NAME);

        if (dummyTSPobj != null) {

            double dummy_tour_cost = (double) dummy_method_cost.invoke(dummyTSPobj);
            double ground_truth = dummy_cost
                    .getDistance(dummy_points_indices.get(dummy_points_indices.size() - 1),
                            dummy_points_indices.get(0));
            for (int i = 0; i < dummy_points_indices.size() - 1; i++) {
                ground_truth += dummy_cost
                        .getDistance(dummy_points_indices.get(i),
                                dummy_points_indices.get(i + 1));
            }
            boolean is_tsp = (dummy_tour_cost == ground_truth);
            int[] areaTour = (int[]) get_tour_method.invoke(dummyTSPobj);
            if (areaTour.length == dummy_points_indices.size() && is_tsp) {
                is_tsp = (areaTour[0] == 0 && areaTour[1] == 1 && areaTour[2] == 2)
                        || (areaTour[0] == 0 && areaTour[1] == 2 && areaTour[2] == 1)
                        || (areaTour[0] == 1 && areaTour[1] == 0 && areaTour[2] == 2)
                        || (areaTour[0] == 1 && areaTour[1] == 2 && areaTour[2] == 0)
                        || (areaTour[0] == 2 && areaTour[1] == 0 && areaTour[2] == 1)
                        || (areaTour[0] == 2 && areaTour[1] == 1 && areaTour[2] == 0);

            } else {
                is_tsp = false;
            }
            if (!is_tsp) {
                throw new Exception("BAD IMPLEMENTATION OF A TSP SOLVER CHECK YOUR CODE!");
            }
        } else {
            throw new Exception("NOT A TSP SOLVER CLASS!");
        }
    }

    /**
     * solve the tsp on a group of points.
     *
     * @param cost_func provides the cost between every 2 points.
     * @param area list of points indices that represents a complete graph. (can be a
     * sub set of the points in cost_func)
     * @return a tsp tour
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public int[] solve_tsp(Cost cost_func, ArrayList<Integer> area) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object TSPobj = tsp_constructor.newInstance((Object) cost_func, (Object) area);
        int[] tsp_tour_indices = (int[]) get_tour_method.invoke(TSPobj);
        int [] tsp_tour = new int[tsp_tour_indices.length];
        for (int i = 0; i < tsp_tour.length; i++) {
            tsp_tour[i] = area.get(tsp_tour_indices[i]);
        }
        return tsp_tour;
    }
    /**
     * get all the names of the tsp solvers in a jar file.
     * @param path_to_jar the path to jar file.
     * @return a list of all the tsp solvers names in the jar.
     * @throws IOException if the path is wrong.
     */
    public static ArrayList<String> getAllTSPAlgorithmsNames(String path_to_jar) throws IOException {
        final String extension = ".class";
        ArrayList<String> algorithms = new ArrayList<>();
        JarFile jar = new JarFile(path_to_jar);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            String algo_name = entries.nextElement().getName();
            if (algo_name.endsWith(extension)) {
                algo_name = algo_name.substring(0, algo_name.length() - extension.length()).replace('/', '.').replace('\\', '.');

                try {
                    new TSPLoader(path_to_jar, algo_name);
                    algorithms.add(algo_name);
                } catch (Exception ex) {
                }
            }
        }

        return algorithms;
    }

}
