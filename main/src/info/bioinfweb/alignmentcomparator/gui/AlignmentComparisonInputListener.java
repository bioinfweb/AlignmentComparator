/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
 * <http://bioinfweb.info/Software>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;



public class AlignmentComparisonInputListener extends MouseAdapter 
    implements MouseListener, KeyListener, MouseWheelListener {

	
	public static final float ZOOM_PER_CLICK = 0.1f;
	
	
	protected AlignmentComparisonPanel owner = null;
	

	public AlignmentComparisonInputListener(AlignmentComparisonPanel owner) {
	  this.owner = owner;	
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if ((e.isMetaDown() && Main.IS_MAC) || (e.isControlDown()&&  !Main.IS_MAC)) {
			owner.setZoom(owner.getZoom() - (float)e.getWheelRotation() * ZOOM_PER_CLICK);
		}
		else {
			owner.getParent().dispatchEvent(e);  // Ereignis zum Scrollen an JScrollPane weiterleiten
		}
	}
}
