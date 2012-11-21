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
	private int startColumn = NOT_SELECTING;
	

	public AlignmentComparisonInputListener(AlignmentComparisonPanel owner) {
	  this.owner = owner;	
	}
	
	
	public AlignmentComparisonPanel getOwner() {
		return owner;
	}

	
	private boolean isSelectingColumn() {
		return startColumn != NOT_SELECTING;
	}
	
	
	private void stopSelectingColumn() {
		startColumn = NOT_SELECTING;
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
	public void mousePressed(MouseEvent e) {
		//TODO y Bereich prüfen
		
		startColumn = getOwner().columnByPaintX(e.getX());
		getOwner().getSelection().setNewSelection(startColumn, startColumn);
	}

	
	private void extendSelection(int paintX) {
		int column = getOwner().columnByPaintX(paintX);
		if (column < startColumn) {
			getOwner().getSelection().setFirstPos(column);
		}
		else {
			getOwner().getSelection().setLastPos(column);  // Automatic mechanism in setLastPos() is not sufficient to replace this method, because first and last position are ordered after the first call of setLastPos().  
		}
	}
	

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isSelectingColumn()) {
			extendSelection(e.getX());
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if (isSelectingColumn()) {
			extendSelection(e.getX());
			stopSelectingColumn();
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
