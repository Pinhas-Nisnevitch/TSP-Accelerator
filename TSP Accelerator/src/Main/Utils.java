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

import AcceleratorAlgorithm.Accelerator;
import GUI.JPanelObserver;
import static Main.Controller.isRunning;
import TSP_Algorithms.TSP_Algorithm;
import Visualization.Shapes;
import Visualization.VisualizeAccelerator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Pinhas Nisnevitch
 */
public class Utils {
    /**
     * Check if a class inside a jar file is a TSP solver.
     * @param name the name of the class to check.
     * @param path the path to the jar file.
     * @return the name of the algorithm or empty string.
     */
    public static String isTSP_Algorithm(String name, String path) {
        String tspAlgo = null;
        try {
            ArrayList<Integer> testPoints = new ArrayList<>();
            ArrayList<ScaledPoint> testPointsXY = new ArrayList<>();
            Random rand = new Random();
            testPointsXY.add(new ScaledPoint(rand.nextInt(500), rand.nextInt(500)));
            testPointsXY.add(new ScaledPoint(rand.nextInt(500), rand.nextInt(500)));
            testPointsXY.add(new ScaledPoint(rand.nextInt(500), rand.nextInt(500)));
            Cost data = new Cost(testPointsXY, 2);
            testPoints.add(0);
            testPoints.add(1);
            testPoints.add(2);
            double dist = data.getDistance(0, 1)
                    + data.getDistance(0, 2)
                    + data.getDistance(1, 2);
            File file = new File(path);
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            URLClassLoader cl = new URLClassLoader(urls);
            Class<?> cls = cl.loadClass(name.replace("/", "."));
            Constructor con;
            Object TSPobj = null;
            boolean maybe = false;
            for (int i = 0; i < cls.getConstructors().length; i++) {
                try {
                    con = cls.getConstructors()[i];
                    TSPobj = con.newInstance((Object) data, (Object) testPoints);
                    i = cls.getConstructors().length;
                    maybe = true;
                } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
                }
            }
            if (!maybe) {
                return tspAlgo;
            }
            Method method = cls.getMethod(TSP_Algorithm.GET_TOUR_FUNCTION_NAME);
            Method method2 = cls.getMethod(TSP_Algorithm.GET_COST_FUNCTION_NAME);
            Method method3 = cls.getMethod(TSP_Algorithm.GET_ALGORITHM_NAME);
            method.invoke(TSPobj);
            double testCost = (double) method2.invoke(TSPobj);
            if (Math.abs(testCost - dist) < 0.01) {
                tspAlgo = (String) method3.invoke(TSPobj);
            }
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

        }
        return tspAlgo;

    }

    public static double loadTSP_Class(int algoListIndex, ArrayList<String> algoPath, String className, Cost cost_func, ArrayList<Integer> points_indices, TourData tour_data, JPanelObserver panel) {
        double cost = 0;
        try {
            if (!algoPath.isEmpty()) {
                File file = new File(algoPath.get(algoListIndex));
                URL url = file.toURI().toURL();
                URL[] urls = new URL[]{url};
                URLClassLoader cl = new URLClassLoader(urls);
                Class<?> cls = cl.loadClass(className);
                Constructor con;
                Object TSPobj = null;
                for (int i = 0; i < cls.getConstructors().length; i++) {
                    try {
                        con = cls.getConstructors()[i];
                        TSPobj = con.newInstance((Object) cost_func, (Object) points_indices);
                        i = cls.getConstructors().length;
                    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
                    }
                }
                Method method = cls.getMethod(TSP_Algorithm.GET_TOUR_FUNCTION_NAME);
                Method method2 = cls.getMethod(TSP_Algorithm.GET_COST_FUNCTION_NAME);

                tour_data.setTour((int[]) method.invoke(TSPobj));
                cost = (double) method2.invoke(TSPobj);
                panel.repaint();
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | MalformedURLException ex) {

        }
        return cost;
    }

    public static double distanceL2(ScaledPoint p, ScaledPoint q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void rescale(double[] scale, Cost cost_func, JPanelObserver panel) {

        double scaleX, scaleY;
        Dimension appSize = panel.getSize();
        ArrayList<ScaledPoint> pointsXY = cost_func.getPoints();
        for (int i = 0; i < pointsXY.size(); i++) {
            scaleX = ((appSize.width * (pointsXY.get(i).getRealX())) / (scale[0] - scale[2])) / (pointsXY.get(i).getRealX());
            scaleY = ((appSize.height * (pointsXY.get(i).getRealY())) / (scale[1] - scale[3])) / (pointsXY.get(i).getRealY());
            scaleX *= 0.99;
            scaleY *= 0.95;
            pointsXY.get(i).setScaleX(scaleX);
            pointsXY.get(i).setScaleY(scaleY);
        }
    }

    public static void repaintPanel(Graphics g, JPanelObserver panel, BufferedImage background, BufferedImage dotImage, int SIZE_OF_POINT, Cost cost_func, TourData tour_data, ArrayList<Shapes> shapes, JLabel label_cost, JLabel label_time) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        }
        Graphics2D g2Dot;
        Graphics2D g2Line = (Graphics2D) g;
        g2Dot = dotImage.createGraphics();
        g2Dot.drawImage(dotImage, SIZE_OF_POINT, SIZE_OF_POINT, null);
        for (int i = 0; i < cost_func.getSize(); i++) {
            g.drawImage(dotImage, cost_func.getX(i), cost_func.getY(i), panel);
        }
        g2Line.setStroke(new BasicStroke(SIZE_OF_POINT / 5));
        g2Line.setColor(new Color(40, 30, 40));
        int[] tour = tour_data.getTour();
        for (int i = 1; i < tour.length; i++) {
            g2Line.drawLine(cost_func.getX(tour[i - 1]) + SIZE_OF_POINT / 2, cost_func.getY(tour[i - 1]) + SIZE_OF_POINT / 2, cost_func.getX(tour[i]) + SIZE_OF_POINT / 2, cost_func.getY(tour[i]) + SIZE_OF_POINT / 2);
        }
        if (tour.length > 1) {
            g2Line.drawLine(cost_func.getX(tour[0]) + SIZE_OF_POINT / 2, cost_func.getY(tour[0]) + SIZE_OF_POINT / 2, cost_func.getX(tour[tour.length - 1]) + SIZE_OF_POINT / 2, cost_func.getY(tour[tour.length - 1]) + SIZE_OF_POINT / 2);
        }

        double cost = Utils.round(tour_data.getTourCost(), 2);
        String longCost = "" + cost;
        if (longCost.charAt(longCost.length() - 2) == 'E') {
            String newCo = "" + longCost.charAt(0);
            int stopInd = Integer.parseInt(longCost.substring(longCost.length() - 1)) + 2;
            int i = 1;
            while (i < stopInd) {
                if (longCost.charAt(i) != '.') {
                    newCo += longCost.charAt(i);
                }
                i++;
            }
            longCost = newCo;
        }
        label_cost.setText(Constant.LABEL_TOUR_COST.substring(0, Constant.LABEL_TOUR_COST.length() - 1) + longCost);
        label_time.setText(Constant.LABEL_TOUR_TIME.substring(0, Constant.LABEL_TOUR_TIME.length() - 1) + tour_data.getTourTimeInMillisec() + " Sec");
        for (Shapes s : shapes) {
            s.paintShape(g);
        }
    }

    public static void solveTSP(JButton bToggleAccelerator, JButton bVisualizeAlgo, JButton bSolveTSP, JList<String> list_algo_names, ArrayList<String> algorithms_classes, Cost cost_func, double Li, ArrayList<Integer> points_indices, TourData tour_data, JPanelObserver panel, ArrayList<String> algoPath, int hierarchy_depth, double BIGMST, ScaledPoint[] tempXY) {
        try {
            double cost = 0;
            int[] tour = new int[0];

            isRunning = true;
            bVisualizeAlgo.setEnabled(false);
            bToggleAccelerator.setEnabled(false);
            bSolveTSP.setEnabled(false);
            int algoListIndex = list_algo_names.getSelectedIndex();
            if (algoListIndex >= 0) {
                String className = algorithms_classes.get(algoListIndex).replace("/", ".");
                cost_func.setDistance(Li);
                points_indices.clear();
                for (int i = 0; i < cost_func.getSize(); i++) {
                    points_indices.add(i);
                }
                long time;

                if (bToggleAccelerator.getText().equals(Constant.BUTTON_TEXT_ACCELERATOR_OFF)) {
                    time = System.currentTimeMillis();
                    
                    cost = Utils.loadTSP_Class(algoListIndex, algoPath, className, cost_func, points_indices, tour_data, panel);
                    tour = tour_data.getTour();
                } else {
                    time = System.currentTimeMillis();
                    Accelerator acl = new Accelerator(algoPath.get(algoListIndex), className, cost_func, points_indices, hierarchy_depth, BIGMST);
                    tour = acl.getTour();
                    cost = acl.getTourCost();

                }
                time = System.currentTimeMillis() - time;
                double sec = (double) time / 1000.0;
                tour_data.setTour(tour);
                tour_data.setTourCost(cost);
                tour_data.setTourTimeInSec(sec);

            }
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);

            if (cost_func.isEmpty()) {
                ArrayList<ScaledPoint> restored = new ArrayList<>();
                restored.addAll(Arrays.asList(tempXY));
                cost_func.setPoints(restored);
            }

            bVisualizeAlgo.setEnabled(true);
            bSolveTSP.setEnabled(true);
            bToggleAccelerator.setEnabled(true);
            //tabPane.setEnabledAt(1, true);
            isRunning = false;

        }

        if (cost_func.isEmpty()) {
            ArrayList<ScaledPoint> restored = new ArrayList<>();
            restored.addAll(Arrays.asList(tempXY));
            cost_func.setPoints(restored);
        }

        isRunning = false;
        bVisualizeAlgo.setEnabled(true);
        bToggleAccelerator.setEnabled(true);
        bSolveTSP.setEnabled(true);
        
    }

    public static void algortihm_visualization(JButton bVisualizeAlgo, JButton bToggleAccelerator, JButton bSolveTSP, JList<String> list_algo_names, ArrayList<String> algorithms_classes, Cost cost_func, double Li, ArrayList<Integer> points_indices, ArrayList<Shapes> shapes, JPanelObserver panel, int SIZE_OF_POINT, int hierarchy_depth, double BIGMST, ArrayList<String> algoPath, TourData tour_data, ScaledPoint[] tempXY, String save_path) {
        try {
            bVisualizeAlgo.setEnabled(false);
            bToggleAccelerator.setEnabled(false);
            bSolveTSP.setEnabled(false);

            int algoListIndex = list_algo_names.getSelectedIndex();
            if (algoListIndex >= 0) {
                String className = algorithms_classes.get(algoListIndex).replace("/", ".");
                cost_func.setDistance(Li);

                points_indices.clear();
                boolean noScale = true;
                for (int i = 0; i < cost_func.getSize(); i++) {
                    points_indices.add(i);
                    if ((double) cost_func.getX(i) != cost_func.getRealX(i)
                            || (double) cost_func.getY(i) != cost_func.getRealY(i)) {
                        noScale = false;
                    }
                }
                if (noScale || true) {
                    shapes.clear();
                    
                    File save_dir = new File(new File(save_path) ,Constant.SAVE_VISUALIZATION_PATH);
                    if(save_dir.exists()){
                        save_dir.delete();
                    }
                    save_dir.mkdirs();
                    VisualizeAccelerator va = new VisualizeAccelerator(save_dir, panel, shapes, SIZE_OF_POINT, algoPath.get(algoListIndex), className, cost_func, points_indices, hierarchy_depth, BIGMST);
                    va.startVisualization();
                    double sec = (double) va.getAlgoTimeInMillisec() / 1000.0;
                    tour_data.setTourCost(va.getTourCost());
                    tour_data.setTourTimeInSec(sec);

                } else {
                    //JOptionPane.showMessageDialog(textCost, "Unable to display when the points are in scale!", "Step By Step", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            if (cost_func.isEmpty()) {
                ArrayList<ScaledPoint> restored = new ArrayList<>();
                restored.addAll(Arrays.asList(tempXY));
                cost_func.setPoints(restored);
            }
            bVisualizeAlgo.setEnabled(true);
            bSolveTSP.setEnabled(true);
            bToggleAccelerator.setEnabled(true);

            isRunning = false;
           
        }
        if (cost_func.isEmpty()) {
            ArrayList<ScaledPoint> restored = new ArrayList<>();
            restored.addAll(Arrays.asList(tempXY));
            cost_func.setPoints(restored);
        }
        isRunning = false;
        bVisualizeAlgo.setEnabled(true);
        bToggleAccelerator.setEnabled(true);
        bSolveTSP.setEnabled(true);

    }

}
