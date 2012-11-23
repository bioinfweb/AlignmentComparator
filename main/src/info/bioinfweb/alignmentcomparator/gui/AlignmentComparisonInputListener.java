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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;



public class AlignmentComparisonInputListener extends MouseAdapter 
    implements MouseListener, KeyListener, MouseWheelListener {

	
	public static final float ZOOM_PER_CLICK = 0.1f;
	public static final int NOT_SELECTING = -1;
	
	
	private AlignmentComparisonPanel owner = null;
	private boolean isSelectingColumn = false;
	

	public AlignmentComparisonInputListener(AlignmentComparisonPanel owner) {
	  this.owner = owner;	
	}
	
	
	public AlignmentComparisonPanel getOwner() {
		return owner;
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		  case KeyEvent.VK_RIGHT: case KeyEvent.VK_NUMPAD6:
		  	if (e.isShiftDown()) {
		  		getOwner().getSelection().extendSelectionRelatively(1);
		  	}
		  	else {
		  		getOwner().getSelection().moveSelectionStart(1);
		  	}
			  break;			
		  case KeyEvent.VK_LEFT: case KeyEvent.VK_NUMPAD4:
		  	if (e.isShiftDown()) {
		  		getOwner().getSelection().extendSelectionRelatively(-1);
		  	}
		  	else {
  		  	getOwner().getSelection().moveSelectionStart(-1);
		  	}
			  break;			
		}
	}

	
	@Override
	public void keyReleased(KeyEvent e) {}

	
	@Override
	public void keyTyped(KeyEvent e) {}

	
	@Override
	public void mousePressed(MouseEvent e) {
		//TODO y Bereich prüfen
		if (e.getButton() == MouseEvent.BUTTON1) {
			isSelectingColumn = true;
			getOwner().getSelection().setNewSelection(getOwner().columnByPaintX(e.getX()));
		}
	}

	
	private void extendSelection(int paintX) {
		getOwner().getSelection().extendSelectionTo(getOwner().columnByPaintX(paintX));
	}
	

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isSelectingColumn) {
			extendSelection(e.getX());
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if (isSelectingColumn) {
			extendSelection(e.getX());
			isSelectingColumn = false;
		}
		else if (e.getButton() == MouseEvent.BUTTON3) {
			getOwner().getSelection().clear();
		}
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
