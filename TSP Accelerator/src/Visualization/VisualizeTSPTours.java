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

import Main.Controller;
import Main.Cost;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * This class purpose is to visualize the TSP Tours.
 *
 * @author Pinhas Nisnevitch
 * @see Accelerator
 */
public class VisualizeTSPTours {

    private final int delay = 200;
    public Timer timer;

    /**
     *
     * @param tsp_tours_by_levels TSP tours.
     * @param cost_func provides the locations and size of the points.
     * @param shapes list of shapes - any shape in that list can be displayed.
     * @param panel the panel of the visualization.
     * @param colorList the color of each tour.
     * @param smallest_diam the smallest Diameter which allowed on the panel.
     * @param save_path the path to the displayed image.
     */
    public VisualizeTSPTours(final ArrayList<ArrayList<int[]>> tsp_tours_by_levels, final Cost cost_func,
            final ArrayList<Shapes> shapes, final JPanel panel, final ArrayList<Color> colorList, int smallest_diam, File save_path) {

        ActionListener animate = new ActionListener() {

            int tours_counter, save_ind;
            boolean display = true;
            boolean repeat_once = true;

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (Controller.isRunning && tours_counter < tsp_tours_by_levels.size()) {
                    if (display) {
                        for (int i = 0; i < tsp_tours_by_levels.get(tours_counter).size(); i++) {
                            if (tsp_tours_by_levels.get(tours_counter).get(i) != null) {
                                for (int j = 1; j < tsp_tours_by_levels.get(tours_counter).get(i).length; j++) {
                                    shapes.add(new Line(cost_func.getX(tsp_tours_by_levels.get(tours_counter).get(i)[j - 1]) + (int) (smallest_diam / 2),
                                            cost_func.getY(tsp_tours_by_levels.get(tours_counter).get(i)[j - 1]) + (int) (smallest_diam / 2),
                                            cost_func.getX(tsp_tours_by_levels.get(tours_counter).get(i)[j]) + (int) (smallest_diam / 2),
                                            cost_func.getY(tsp_tours_by_levels.get(tours_counter).get(i)[j]) + (int) (smallest_diam / 2), colorList.get(i)));
                                }
                                shapes.add(new Line(cost_func.getX(tsp_tours_by_levels.get(tours_counter).get(i)[0]) + (int) (smallest_diam / 2),
                                        cost_func.getY(tsp_tours_by_levels.get(tours_counter).get(i)[0]) + (int) (smallest_diam / 2),
                                        cost_func.getX(tsp_tours_by_levels.get(tours_counter).get(i)[tsp_tours_by_levels.get(tours_counter).get(i).length - 1]) + (int) (smallest_diam / 2),
                                        cost_func.getY(tsp_tours_by_levels.get(tours_counter).get(i)[tsp_tours_by_levels.get(tours_counter).get(i).length - 1]) + (int) (smallest_diam / 2), colorList.get(i)));
                            }
                        }
                        tours_counter++;
                    } else {
                        shapes.clear();
                        if (repeat_once) {
                            repeat_once = false;
                            tours_counter--;
                        }
                    }
                    timer.setDelay(delay);
                    display = !display;
                    if (!display) {
                        panel.repaint();
                        //Controller.dont_save)) {
                            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                            Graphics g = image.getGraphics();
                            panel.paint(g);
                            try {
                                ImageIO.write(image, "jpg", new File(save_path, "tours_" + save_ind + ".jpg"));
                            } catch (IOException ex) {
                                Logger.getLogger(VisualizeNetHierarchy.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            save_ind++;
                        //}

                    }

                } else {
                    timer.stop();
                }
            }
        };
        timer = new Timer(delay, animate);
  
    }

}
