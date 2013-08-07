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
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.webinsel.util.Math2;
import info.webinsel.util.SystemUtils;

import java.awt.event.ActionEvent;
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
		if (e.getButton() == MouseEvent.BUTTON1) {
			int commentY = Math2.roundUp(getOwner().sequenceBlockHeight());
  		if (e.getY() <= commentY) {
				isSelectingColumn = true;
				getOwner().getSelection().setNewSelection(getOwner().columnByPaintX(e.getX()));
			}
			else {
				getOwner().getSelection().setComment(getOwner().getCommentPositioner().getCommentByMousePosition(
						getOwner().getDocument(), getOwner(),	
						0, commentY + AlignmentComparisonPanel.COMMENTS_DISTANCE * getOwner().getZoom(), e.getX(), e.getY()));  
				    // If null is returned the comment selection is cleared

				if (e.getClickCount() > 1) {
					DocumentAction action = (DocumentAction)Main.getInstance().getMainFrame().getActionManagement().get(
							"edit.changeCommentText");
					if (action.isEnabled()) {
						action.actionPerformed(new ActionEvent(this, 0, ""));
					}
				}
			}
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
		if ((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown()&&  !SystemUtils.IS_OS_MAC)) {
			owner.setZoom(owner.getZoom() - (float)e.getWheelRotation() * ZOOM_PER_CLICK);
		}
		else {
			owner.getParent().dispatchEvent(e);  // Ereignis zum Scrollen an JScrollPane weiterleiten
		}
	}
}
