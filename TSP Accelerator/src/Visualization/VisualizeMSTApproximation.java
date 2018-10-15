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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import AcceleratorAlgorithm.MSTWeightApproximation;
import Main.Controller;
import Main.Cost;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * This class purpose is to visualize the MST approximation.
 *
 * @author Pinhas Nisnevitch
 * @see Accelerator
 */
public class VisualizeMSTApproximation {

    private final int delay = 100;
    public Timer timer;
    

    /**
     *
     * @param mst_approx the approximation of a Minimum Spanning Tree.
     * @param cost_func provides the locations and size of the points.
     * @param shapes list of shapes - any shape in that list can be displayed.
     * @param panel the panel of the visualization.
     * @param colorList the color of each tour.
     * @param smallest_diam the smallest Diameter which allowed on the panel.
     * @param save_path the path to the displayed image.
     */
    public VisualizeMSTApproximation(MSTWeightApproximation mst_approx, Cost cost_func,
            final ArrayList<Shapes> shapes, final JPanel panel,
            final ArrayList<Color> colorList, int smallest_diam, File save_path) {
        
        final int numOfTours = mst_approx.regions.size();
        ArrayList<Shapes> orderd_shapes[] = new ArrayList[numOfTours];
        // array on which every number represents a type of shape.
        int[] radius_level = new int[cost_func.getSize()];
        for (int i = 0; i < numOfTours; i++) {
            orderd_shapes[i] = new ArrayList<>();
            for (int j = 0; j < mst_approx.regions.get(i).size(); j++) {

                radius_level[mst_approx.regions.get(i).get(j)]++;
                if (radius_level[mst_approx.regions.get(i).get(j)] == 1) {
                    orderd_shapes[i].add(new FilledCircle(cost_func.getX(mst_approx.regions.get(i).get(j)),
                            cost_func.getY(mst_approx.regions.get(i).get(j)), smallest_diam, colorList.get(i)));
                } else {
                    int radius = smallest_diam + (int) mst_approx.radiuses[mst_approx.groups_exit_level.get(i)];
                    orderd_shapes[i].add(new Circle(cost_func.getX(mst_approx.regions.get(i).get(j)) + (int) (smallest_diam / 2) - radius / 2,
                            cost_func.getY(mst_approx.regions.get(i).get(j)) + (int) (smallest_diam / 2) - radius / 2, radius, colorList.get(i)));
                }
            }

        }

        ActionListener animate = new ActionListener() {
            int show = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {
                // visualization of tours regions one by one.
                if (Controller.isRunning && show < numOfTours) {
                    shapes.addAll(orderd_shapes[show]);
                    show++;
                    panel.repaint();

                    BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics g = image.getGraphics();
                    panel.paint(g);
                    if (show == numOfTours){//Controller.dont_save)) {
                        try {
                            // save only the last time (all regions).
                            ImageIO.write(image, "jpg", new File(save_path, "MSTApprox.jpg"));
                        } catch (IOException ex) {
                            Logger.getLogger(VisualizeNetHierarchy.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    timer.stop();
                }
            }
        };
        timer = new Timer(delay, animate);
        
    }

}
