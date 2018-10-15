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

/**
 *
 * @author Pinhas Nisnevitch
 */
public class TourData {
    private double time;
    private double cost;
    private int [] tour;
    
    public TourData(){
        tour = new int[0];
        time = 0;
        cost = 0;
    }
    
    public void setTourTimeInSec(double time){
        this.time = time;
    }
    
    public void setTourCost(double cost){
        this.cost = cost;
    }
    
    public void setTour(int [] tour){
        this.tour = tour;
    }
    
    public double getTourTimeInMillisec(){
        return time;
    }
    
    public double getTourCost(){
        return cost;
    }
    
    public int[] getTour(){
        return tour;
    }
    
    
}
