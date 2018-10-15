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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestPath {

    public static double BFS_Search(HashMap<Integer, Double>[] graph, double limit, int start, int end) {
        HashMap<Integer, Double> distPoint = new HashMap<>();
        HashMap<Integer, Boolean> inQueue = new HashMap<>();
        distPoint.put(start, 0.0);
        distPoint.put(end, limit);
        HashMap<Integer, Double> point = graph[start];
        int pointInd, k;
        double tempSum;
        if (point.containsKey(end)) {
            return Math.abs(point.get(end));
        }
        if (point.isEmpty()) {
            return -1;
        }
        Queue<Integer> q = new LinkedList<>();
        q.add(start);
        inQueue.put(start, true);
        while (!q.isEmpty()) {
            pointInd = q.remove();
            inQueue.remove(pointInd);
            point = graph[pointInd];
            Iterator<Integer> rap = point.keySet().iterator();
            while (rap.hasNext()) {
                k = rap.next();
                tempSum = Math.abs(point.get(k)) + distPoint.get(pointInd);
                if (tempSum > distPoint.get(end)) {
                    continue;
                }
                if (distPoint.containsKey(k)) {
                    if (tempSum < distPoint.get(k)) {
                        distPoint.put(k, tempSum);
                        if (!(inQueue.containsKey(k) || k == end)) {
                            q.add(k);
                            inQueue.put(k, true);
                        }
                    }
                } else {
                    distPoint.put(k, tempSum);
                    if (k != end) {
                        q.add(k);
                        inQueue.put(k, true);
                    }
                }
            }
        }
        if (distPoint.get(end) < limit) {
            return distPoint.get(end);
        } else {
            return -1;
        }
    }

}
