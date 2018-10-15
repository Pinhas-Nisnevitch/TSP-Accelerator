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

import GUI.JPanelObserver;
import GUI.PaintListener;
import static Main.ReadWriteTSPLib.ReadTspFile;
import Visualization.Shapes;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Pinhas Nisnevitch
 */
public class Controller {

    // GUI components
    private JPanelObserver panel;
    private JButton bSolveTSP;
    private JButton bStop;
    private JButton bToggleAccelerator;
    private JButton bVisualizeAlgo;
    private JList<String> list_algo_names;
    private JTextField text_field_distance;
    private JTextField text_field_mst;
    private JTextField text_field_net_depth;
    private JRadioButton radio_b_create;
    private JRadioButton radio_b_delete;
    private JRadioButton radio_b_move;
    private JLabel label_cost;
    private JLabel label_time;
    private JMenuItem item_load_algorithms;
    private JMenuItem item_load_tsp;
    private JMenuItem item_save_tsp;
    private JMenuItem item_clear;
    private JMenuItem item_about;
    private JMenuItem item_preferences;

    private final ArrayList<Integer> points_indices;
    private static Cost cost_func;
    private static ArrayList<Shapes> shapes;
    private final int SIZE_OF_POINT = 15;
    private int draw_point_state;
    private final BufferedImage pointImage;
    private final ArrayList<String> algoPath;
    private final ArrayList<String> algorithms_classes;
    private final ArrayList<String> algorithms_names;
    public Double mstThreshold = Double.parseDouble(Constant.TEXT_FIELD_MST_WEIGHT);
    private Double Li = Double.parseDouble(Constant.TEXT_FIELD_DISTANCE_FUNCTION);
    private Integer hierarchy_depth = Integer.parseInt(Constant.TEXT_FIELD_NET_HIERARCHY_DEPTH);
    private Thread t;
    public static boolean isRunning;
    private boolean compareTestRun;
    private TourData tour_data;
    private String accCostStr, algoCostStr;
    private ScaledPoint[] tempXY;
    private boolean heirarchyDone;
    private String save_path = System.getProperty("user.dir");
    //private final String[] buildinAlgo = {NearestNeighbor.GET_ALGORITHM_NAME};

    public Controller() throws IOException {
        points_indices = new ArrayList<>();
        algorithms_classes = new ArrayList<>();
        algorithms_names = new ArrayList<>();
        cost_func = new Cost(new ArrayList<>(), Li);
        algoPath = new ArrayList<>();
        shapes = new ArrayList<>();
        draw_point_state = -1;
        tempXY = new ScaledPoint[0];
        pointImage = ImageIO.read(getClass().getResource("/resources/point.png"));
        tour_data = new TourData();
    }

    public void initComponents(JMenuItem item_load_algorithms, JMenuItem item_load_tsp, JMenuItem item_save_tsp, JMenuItem item_preferences, JMenuItem item_clear, JMenuItem item_about, JPanelObserver panel, JButton bSolveTSP, JButton bStop, JButton bToggleAccelerator,
            JButton bVisualizeAlgo, JList<String> list_algo_names, JTextField text_field_distance, JTextField text_field_mst,
            JTextField text_field_net_depth, JRadioButton radio_b_create, JRadioButton radio_b_delete, JRadioButton radio_b_move,
            JLabel label_cost, JLabel label_time) {
        this.item_load_algorithms = item_load_algorithms;
        this.item_load_tsp = item_load_tsp;
        this.item_save_tsp = item_save_tsp;
        this.item_preferences = item_preferences;
        this.item_clear = item_clear;
        this.item_about = item_about;
        this.panel = panel;
        this.bSolveTSP = bSolveTSP;
        this.bStop = bStop;
        this.bToggleAccelerator = bToggleAccelerator;
        this.bVisualizeAlgo = bVisualizeAlgo;
        this.list_algo_names = list_algo_names;
        this.text_field_distance = text_field_distance;
        this.text_field_mst = text_field_mst;
        this.text_field_net_depth = text_field_net_depth;
        this.radio_b_create = radio_b_create;
        this.radio_b_delete = radio_b_delete;
        this.radio_b_move = radio_b_move;
        this.label_cost = label_cost;
        this.label_time = label_time;
        setPanel();
        setButtonsListeners();
        setMenusButtonsListeners();
        //setList();
    }

    /*
    private void setList() {

        list_algo_names.setModel(new javax.swing.AbstractListModel() {

            @Override
            public int getSize() {
                return buildinAlgo.length;
            }

            @Override
            public Object getElementAt(int i) {
                return buildinAlgo[i];
            }
        });
        list_algo_names.setSelectedIndex(0);

    }
     */
    private void setPanel() {
        panel.addPaintListener(new PaintListener() {
            @Override
            public void Painted(Graphics g) {
                Utils.repaintPanel(g, panel, null, pointImage, SIZE_OF_POINT, cost_func, tour_data, shapes, label_cost, label_time);
            }

        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draw_point_state != -1 && e.getX() >= 0 && e.getY() >= 0
                        && e.getX() < panel.getWidth() - SIZE_OF_POINT
                        && e.getY() < panel.getHeight() - SIZE_OF_POINT) {
                    cost_func.getPoints().set(draw_point_state, new ScaledPoint(e.getX(), e.getY()));
                    shapes.clear();
                    tour_data.setTour(new int[0]);
                    tour_data.setTourCost(0);
                    panel.scrollRectToVisible(new Rectangle(e.getX(), e.getY(), 1, 1));
                }
                panel.repaint();
            }

        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (!isRunning) {
                    int radio_button_selection = getRadioButtonsState();
                    if (radio_button_selection == 0) {
                        int x = me.getX(), y = me.getY();
                        if (x < 0) {
                            x = 0;
                        } else if (x >= panel.getWidth() - SIZE_OF_POINT) {
                            x = (int) (panel.getWidth() - (SIZE_OF_POINT + 1));
                        }
                        if (y < 0) {
                            y = 0;
                        } else if (y >= panel.getHeight() - SIZE_OF_POINT) {
                            y = (int) (panel.getHeight() - (SIZE_OF_POINT + 1));
                        }
                        cost_func.add(new ScaledPoint(x, y));
                        shapes.clear();
                        tour_data.setTour(new int[0]);
                        tour_data.setTourCost(0);
                        panel.repaint();

                    } else {
                        double radius = SIZE_OF_POINT / 2;
                        double dist;
                        for (int i = 0; i < cost_func.getSize(); i++) {
                            int newX = (me.getX() - SIZE_OF_POINT / 2);
                            int newY = (me.getY() - SIZE_OF_POINT / 2);
                            dist = Utils.distanceL2(cost_func.get(i), new ScaledPoint(newX, newY));
                            if (dist <= radius) {
                                if (radio_button_selection == 1) {
                                    draw_point_state = i;
                                } else if (radio_button_selection == 2) {
                                    cost_func.remove(i);
                                    tour_data.setTour(new int[0]);
                                    tour_data.setTourCost(0);
                                    panel.repaint();
                                }
                                i = cost_func.getSize();
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                super.mouseReleased(arg0);
                draw_point_state = -1;
            }

        });
    }

    private int getRadioButtonsState() {
        int radio_buttons_selection = 0;
        if (radio_b_move.isSelected()) {
            radio_buttons_selection = 1;
        } else if (radio_b_delete.isSelected()) {
            radio_buttons_selection = 2;
        }
        return radio_buttons_selection;
    }

    private double getInputDistance() {

        if (text_field_distance.getText().length() == 0 || text_field_distance.getText().equals(".")
                || Double.parseDouble(text_field_distance.getText()) == 0) {
            text_field_distance.setText(Constant.TEXT_FIELD_DISTANCE_FUNCTION);
        }
        return Double.parseDouble(text_field_distance.getText());

    }

    private double getInputMstThreshold() {
        if (text_field_mst.getText().length() == 0 || text_field_mst.getText().equals(".")) {
            text_field_mst.setText(Constant.TEXT_FIELD_MST_WEIGHT);
        }
        return Double.parseDouble(text_field_mst.getText());
    }

    private int getInputHierarchyDepth() {
        if (text_field_net_depth.getText().length() == 0 || Integer.parseInt(text_field_net_depth.getText()) == 0) {
            text_field_net_depth.setText(Constant.TEXT_FIELD_NET_HIERARCHY_DEPTH);
        }
        return Integer.parseInt(text_field_net_depth.getText());

    }

    private void setButtonsListeners() {
        bSolveTSP.addActionListener((ActionEvent e) -> {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    bStop.doClick();
                    Li = getInputDistance();
                    mstThreshold = getInputMstThreshold();
                    hierarchy_depth = getInputHierarchyDepth();
                    Utils.solveTSP(bToggleAccelerator, bVisualizeAlgo, bSolveTSP, list_algo_names, algorithms_classes, cost_func, Li, points_indices, tour_data, panel, algoPath, hierarchy_depth, mstThreshold, tempXY);
                    panel.repaint();
                }
            };
            if (t == null || (t != null && !t.isAlive())) {
                t = new Thread(r);
                t.setDaemon(true);
            }
            if (!t.isAlive()) {
                t.start();
            }
        });
        bStop.addActionListener((ActionEvent e) -> {
            if (isRunning || !shapes.isEmpty()
                    || tour_data.getTour().length > 0) {
                points_indices.clear();
                if (!heirarchyDone && isRunning) {
                    tempXY = cost_func.getPoints().toArray(new ScaledPoint[0]);
                    cost_func.clear();
                }
                isRunning = false;
                tour_data.setTour(new int[0]);
                tour_data.setTourCost(0);
                tour_data.setTourTimeInSec(0);
                shapes.clear();
                panel.repaint();
            }
        });
        bToggleAccelerator.addActionListener((ActionEvent e) -> {
            boolean accelerator_off = bToggleAccelerator.getText().equals(Constant.BUTTON_TEXT_ACCELERATOR_ON);
            if (accelerator_off) {
                bToggleAccelerator.setText(Constant.BUTTON_TEXT_ACCELERATOR_OFF);
                bToggleAccelerator.setForeground(Constant.COLOR_ACCELERATOR_OFF);
                label_cost.setForeground(Constant.COLOR_ACCELERATOR_OFF);
                label_time.setForeground(Constant.COLOR_ACCELERATOR_OFF);
            } else {
                bToggleAccelerator.setText(Constant.BUTTON_TEXT_ACCELERATOR_ON);
                bToggleAccelerator.setForeground(Constant.COLOR_ACCELERATOR_ON);
                label_cost.setForeground(Constant.COLOR_ACCELERATOR_ON);
                label_time.setForeground(Constant.COLOR_ACCELERATOR_ON);
            }
        });
        bVisualizeAlgo.addActionListener((ActionEvent e) -> {
            Runnable r;
            r = new Runnable() {
                @Override
                public void run() {
                    bStop.doClick();
                    Li = getInputDistance();
                    mstThreshold = getInputMstThreshold();
                    hierarchy_depth = getInputHierarchyDepth();
                    isRunning = true;
                    //tabPane.setEnabledAt(1, false);
                    if (cost_func.getSize() > 0) {
                        Utils.algortihm_visualization(bVisualizeAlgo, bToggleAccelerator, bSolveTSP, list_algo_names, algorithms_classes, cost_func, Li, points_indices, shapes, panel, SIZE_OF_POINT, hierarchy_depth, mstThreshold, algoPath, tour_data, tempXY, save_path);
                    }

                    //tabPane.setEnabledAt(1, true);
                    isRunning = false;

                    panel.repaint();
                }
            };
            if (t == null || (t != null && !t.isAlive())) {
                t = new Thread(r);
                t.setDaemon(true);
            }
            if (!t.isAlive()) {
                t.start();
            }
        });

        
    }

    private void setMenusButtonsListeners() {
        item_load_algorithms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser1 = new JFileChooser();
                jFileChooser1.setFileFilter(new FileNameExtensionFilter("jar files (*.jar)", "jar"));
                jFileChooser1.setAcceptAllFileFilterUsed(false);
                int result = jFileChooser1.showOpenDialog(item_load_algorithms);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        String path = jFileChooser1.getSelectedFile().getAbsolutePath();
                        JarFile jar = new JarFile(path);
                        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                        while (entries.hasMoreElements()) {
                            String name = entries.nextElement().getName();
                            if (name.length() > 6 && name.substring(name.length() - 6).equals(".class")) {
                                name = name.substring(0, name.length() - 6);
                                if (!algorithms_classes.contains(name)) {
                                    String algo_name = Utils.isTSP_Algorithm(name, path);
                                    if (algo_name != null) {
                                        algorithms_classes.add(name);
                                        algorithms_names.add(algo_name);
                                        algoPath.add(path);
                                    }
                                }
                            }
                        }

                        list_algo_names.setModel(new javax.swing.AbstractListModel() {
                            
                            String[] strings = algorithms_classes.toArray(new String[0]);
                            /*
                            String[] strings = new String[algorithms_classes.size()];
                                    //new String[buildinAlgo.length + algorithms_classes.size()];
                            int count = 0;

                            {
                                for (String tsp_algo : algorithms_classes) {
                                    strings[count++] = tsp_algo;
                                }
                                /*
                                for (String tsp_algo : buildinAlgo) {
                                    strings[count++] = tsp_algo;
                                }
                                 

                            }
                            */

                            @Override
                            public int getSize() {
                                return strings.length;
                            }

                            @Override
                            public Object getElementAt(int i) {
                                //i = (panel.getHeight() + 3) / 24;
                                
                                //list_algo_names.setSelectedIndex(i);
                                String str = strings[i];
                                int ind = str.indexOf("/") + 1;
                                return str.substring(ind);
                            }
                        });

                    } catch (IOException ex) {
                        Logger.getLogger(Controller.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    int algoListIndex = list_algo_names.getSelectedIndex();
                    if (algoListIndex < 0 && !algorithms_classes.isEmpty()) {
                        list_algo_names.setSelectedIndex(0);
                    }

                }
            }
        });
        item_save_tsp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cost_func.getSize() > 0) {
                    JFileChooser file_chooser = new JFileChooser();
                    file_chooser.setFileFilter(new FileNameExtensionFilter("Euclidean TSP files (*.tsp)", "tsp"));
                    file_chooser.setAcceptAllFileFilterUsed(false);
                    int result = file_chooser.showSaveDialog(panel);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        try {
                            String path = file_chooser.getSelectedFile().getAbsolutePath();
                            if (path.length() <= 4 || (path.length() > 4
                                    && !path.substring(path.length() - 4).equals(".tsp"))) {
                                path += ".tsp";
                            }
                            File f = new File(path);
                            int overwrite = JOptionPane.OK_OPTION;
                            if (f.exists()) {
                                overwrite = JOptionPane.showConfirmDialog(panel, f.getName() + " already exists."
                                        + "\nDo you want to replace it?", "Confirme Save As", JOptionPane.YES_NO_OPTION);
                            }
                            if (overwrite == JOptionPane.OK_OPTION) {
                                ReadWriteTSPLib.WriteTspFile(file_chooser.getSelectedFile().getName(), file_chooser.getSelectedFile().getParent(), cost_func);

                            }
                        } catch (HeadlessException ex) {
                            Logger.getLogger(Controller.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "There is no points to save..");
                }
            }
        });
        item_load_tsp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser file_chooser = new JFileChooser();
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().endsWith(".tsp")
                                || f.isDirectory();

                    }

                    @Override
                    public String getDescription() {
                        return "Euclidean TSP files (*.tsp)";
                    }
                };

                file_chooser.setFileFilter(filter);
                file_chooser.setAcceptAllFileFilterUsed(false);

                int result = file_chooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        clear();
                        String path = file_chooser.getSelectedFile().getAbsolutePath();
                        if (path.length() <= 4 || (path.length() > 4
                                && !path.substring(path.length() - 4).equals(".tsp"))) {
                            path += ".tsp";
                        }
                        File fileName = new File(path);
                        file_chooser.setCurrentDirectory(fileName);
                        double scaleSize[] = ReadTspFile(fileName.getPath(), cost_func);
                        if (scaleSize[4] == 1.0) {
                            Utils.rescale(scaleSize, cost_func, panel);
                        }
                        algoCostStr = accCostStr = "";
                        panel.repaint();

                    } catch (Exception ex) {
                        Logger.getLogger(Controller.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        item_preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] buttons = {"Cancel", "Change Path"};
                int selection = JOptionPane.showOptionDialog(panel, "Save visualization path: " + save_path + "\n\nDo you whant to change location ?", Constant.BUTTON_TEXT_PREFERENCES,
                        JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, buttons, buttons[1]);
                if (selection == 1) {
                    JFileChooser jfc = new JFileChooser(save_path);
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    //select file and display
                    if (jfc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                        java.io.File f = jfc.getSelectedFile();
                        save_path = f.getAbsolutePath();
                    }
                }

            }
        });
        item_clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }

        });
        item_about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panel, "TSP Accelerator Version 2.0\n       ", "TSP Accelerator", 1);
            }
        });

    }

    private void clear() {
        points_indices.clear();
        tempXY = new ScaledPoint[0];
        cost_func.clear();
        shapes.clear();
        tour_data.setTour(new int[0]);
        tour_data.setTourCost(0);
        tour_data.setTourTimeInSec(0);
        label_cost.setText(Constant.LABEL_TOUR_COST);
        label_time.setText(Constant.LABEL_TOUR_TIME);
        panel.repaint();
    }
}
