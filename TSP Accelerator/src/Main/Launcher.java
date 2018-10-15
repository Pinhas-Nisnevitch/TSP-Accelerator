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

import GUI.TSPAcceleratorGUIFrame;
import java.io.IOException;

/**
 *
 * @author Pinhas Nisnevitch
 */
public class Launcher {

   
    
    public static void main(String[] args) throws IOException {
        Controller controller = new Controller();
        TSPAcceleratorGUIFrame frame = new TSPAcceleratorGUIFrame(controller);
        frame.setVisible(true);
        frame.setTitle("TSP ACCELERATOR");
        frame.setExtendedState(TSPAcceleratorGUIFrame.MAXIMIZED_BOTH);
    }

}
