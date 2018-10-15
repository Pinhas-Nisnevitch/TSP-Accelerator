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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class consists of static methods for reading and writing "euclidian tsp
 * files"
 *
 * @author Pinhas Nisnevitch
 * @See {
 * @linktourl
 * https://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/}
 */
public class ReadWriteTSPLib {

    // the x and y coordinate of a givien point
    private static double x, y;

    /**
     * writing the points into a .tsp tsp_file
     *
     * @param name the name of the .tsp tsp_file
     * @param path the path to the .tsp tsp_file
     * @param cost_func contains all the points real x and y values
     */
    public static void WriteTspFile(String name, String path, Cost cost_func) {
        try {
            // make sure the name of the tsp_file is without the extension ".tsp"
            if (name.length() > 4 && name.substring(name.length() - 4).equals(".tsp")) {
                name = name.substring(0, name.length() - 4);
            }

            // basic information for the tsplib format
            String content = "NAME : " + name
                    + "\nCOMMENT : Rescale = false"
                    + "\n" + "TYPE : TSP"
                    + "\n" + "DIMENSION : " + cost_func.getSize()
                    + "\n" + "EDGE_WEIGHT_TYPE : EUC_2D"
                    + "\n" + "NODE_COORD_SECTION";

            File tsp_file = new File(path + "/" + name + ".tsp");
            tsp_file.createNewFile();

            // writing all the points into the file
            FileWriter fw = new FileWriter(tsp_file.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(content);
                for (int i = 0; i < cost_func.getSize(); i++) {
                    content = (i + 1) + " " + cost_func.getRealX(i) + " " + cost_func.getRealY(i);
                    bw.write("\n" + content);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ReadWriteTSPLib.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * read an euclidian .tsp file into a list. All not-numerical strings are
     * ignored.
     *
     * @param path the path to the .tsp file
     * @param cost_func contains the points real x and y values.
     * @return array of 3 values: {largest x value in the points list, largest y
     * value in the points list, a flag for scale the points or not }
     */
    public static double[] ReadTspFile(String path, Cost cost_func) {
        ArrayList<ScaledPoint> pointsXY = new ArrayList<>();
        double rescale = 1;

        double maxX = 0;
        double maxY = 0;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        boolean flip = false;
        BufferedReader fi;
        String st;
        try {
            fi = new BufferedReader(new FileReader(new File(path)));
            do {
                st = fi.readLine();
                if (st == null) {
                    break;
                }
                if (st.equals("COMMENT : Rescale = false")) {
                    rescale = 0;
                    continue;
                }
                if (st.contains("EDGE_WEIGHT_TYPE : GEOM")) {
                    flip = true;
                    continue;
                }
                if (!string_to_xy(st)) {
                    continue;
                }
                if (flip) {
                    minX = Math.min(y, minX);
                    maxX = Math.max(y, maxX);
                    maxY = Math.max(x, maxY);
                    minY = Math.min(x, minY);
                    pointsXY.add(new ScaledPoint(y, x));
                } else {
                    minX = Math.min(x, minX);
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                    minY = Math.min(y, minY);
                    pointsXY.add(new ScaledPoint(x, y));
                }
                
                
            } while (true);
            fi.close();
            if (rescale == 1.0) {
                for (int i = 0; i < pointsXY.size(); i++) {
                    pointsXY.set(i, (new ScaledPoint(pointsXY.get(i).getX() - minX, pointsXY.get(i).getY() - minY)));
                }
                maxX -= minX;
                maxY -= minY;

                for (int i = 0; i < pointsXY.size(); i++) {
                    pointsXY.set(i, (new ScaledPoint(pointsXY.get(i).getRealX(), maxY - pointsXY.get(i).getRealY())));
                }
            }
        } catch (IOException ioe) {
        }
        cost_func.setPoints(pointsXY);
        return new double[]{maxX+minX, maxY+minY, minX, minY, rescale};
    }

    /**
     * extract from a line of .tsp file the x and y values of a point if there
     * is a point in that line.
     *
     * @param str the string contains some text or the x and y values of a
     * point.
     * @return true if the string represents a point and false otherwise.
     */
    private static boolean string_to_xy(String str) {
        StringTokenizer toke = new StringTokenizer(str);
        if (toke.countTokens() < 3) {
            return false;
        }
        toke.nextToken();

        try {
            x = Double.parseDouble(toke.nextToken());
        } catch (NumberFormatException nfe) {
            return (false);
        }

        try {
            y = Double.parseDouble(toke.nextToken());
        } catch (NumberFormatException nfe) {
            return (false);
        }
        return (true);
    }
}
