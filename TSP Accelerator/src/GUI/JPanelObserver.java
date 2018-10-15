/*
 * Copyright (C) 2018 Pinhas Nsinevitch
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
package GUI;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Pinhas Nsinevitch
 */
public class JPanelObserver extends JPanel{
    private final List<PaintListener> listeners;
    
    public JPanelObserver(){
        super();
        listeners = new ArrayList<>();
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        listeners.forEach((listener) -> {
            listener.Painted(g);
        });
        
    }
    
    public void addPaintListener(PaintListener listener){
        listeners.add(listener);
    }
    
}
