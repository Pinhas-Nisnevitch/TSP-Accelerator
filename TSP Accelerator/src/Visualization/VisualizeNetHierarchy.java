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
import AcceleratorAlgorithm.NetHierarchy;
import AcceleratorAlgorithm.Representative;
import Main.Controller;
import Main.Cost;
import java.awt.Graphics;
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
 * This class purpose is to visualize the Net Hierarchy.
 *
 * @author Pinhas Nisnevitch
 * @see NetHierarchy
 */
public class VisualizeNetHierarchy {

    private final int delay = 100;
    public Timer timer;

    /**
     *
     * @param net_hierarchy the net hierarchy.
     * @param cost_func provides the locations and size of the points.
     * @param shapes list of shapes - any shape in that list can be displayed.
     * @param panel the panel of the visualization.
     * @param colorList the color of each tour.
     * @param smallest_diam the smallest Diameter which allowed on the panel.
     * @param save_path the path to the displayed image.
     */
    public VisualizeNetHierarchy(NetHierarchy net_hierarchy, Cost cost_func,
            final ArrayList<Shapes> shapes, final JPanel panel, final ArrayList<Color> colorList,
            int smallest_diam, File save_path) {

        final ArrayList<Shapes> orderd_shapes = new ArrayList<>();
        final ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Integer> indexs = new ArrayList<>();
        int radius = (int) net_hierarchy.getMaxRadius();

        // add the radius
        orderd_shapes.add(new Line(cost_func.getX(0) + (int) (smallest_diam / 2), cost_func.getY(0) + (int) (smallest_diam / 2),
                cost_func.getX(net_hierarchy.getRadiusIndexPoint()) + (int) (smallest_diam / 2), cost_func.getY(net_hierarchy.getRadiusIndexPoint()) + (int) (smallest_diam / 2), Color.red));
        sizes.add(1);

        // add the first represintation point at hierarchy_level "NetHierarchy.DEPTH_SIZE - 1"
        orderd_shapes.add(new CircleWithRadius(cost_func.getX(0) - radius,
                cost_func.getY(0) - radius, (radius + smallest_diam / 2) * 2, colorList.get(NetHierarchy.DEPTH_SIZE - 1), smallest_diam));
        sizes.add(1);

        // add all representations from all hierarchy levels.
        for (int i = NetHierarchy.DEPTH_SIZE - 2; i >= 0; i--) {
            radius /= 2;
            for (Representative rep : net_hierarchy.getHierarchyLevels()[i]) {
                if (rep.getPointId() != -1 && !indexs.contains(rep.getPointId())) {
                    indexs.add(rep.getPointId());
                    orderd_shapes.add(new CircleWithRadius(cost_func.getX(rep.getPointId()) - radius,
                            cost_func.getY(rep.getPointId()) - radius, (radius + smallest_diam / 2) * 2, colorList.get(NetHierarchy.DEPTH_SIZE - i), smallest_diam));
                }

            }
            sizes.add(indexs.size());

            if (indexs.size() == cost_func.getSize()) {
                i = -1;
            }
            indexs.clear();
        }

        ActionListener animate = new ActionListener() {
            int shape_counter, hierarchy_level;

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (orderd_shapes.size() > 0 && Controller.isRunning) {
                    if (shape_counter == sizes.get(hierarchy_level)) {
                        for (int i = 0; i < shape_counter; i++) {
                            orderd_shapes.remove(0);
                            shapes.remove(0);
                        }
                        shape_counter = 0;
                        hierarchy_level++;
                    } else {
                        while ((shape_counter < sizes.get(hierarchy_level))) {
                            shapes.add(orderd_shapes.get(shape_counter++));
                        }
                        panel.repaint();
                        //Controller.dont_save)) {
                            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                            Graphics g = image.getGraphics();
                            panel.paint(g);
                            try {
                                ImageIO.write(image, "jpg", new File(save_path, "hierarchy_" + hierarchy_level + ".jpg"));
                            } catch (IOException ex) {
                                Logger.getLogger(VisualizeNetHierarchy.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
