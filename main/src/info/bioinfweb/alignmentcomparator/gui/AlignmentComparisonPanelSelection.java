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


import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.CommentPosition;
import info.webinsel.util.Math2;



/**
 * Represents the selection in a {@link AlignmentComparisonPanel}.
 * 
 * @author Ben St&ouml;ver
 */
public class AlignmentComparisonPanelSelection {
	public static final int NO_SELECTION = -1;
	
	
  private AlignmentComparisonPanel owner;
	private int firstPos = NO_SELECTION;
	private int lastPos = NO_SELECTION;
  private Comment comment = null;
  
  
	public AlignmentComparisonPanelSelection(AlignmentComparisonPanel owner) {
		super();
		this.owner = owner;
	}


	public AlignmentComparisonPanel getOwner() {
		return owner;
	}


	public int getFirstPos() {
		return firstPos;
	}
	
	
	public int getLastPos() {
		return lastPos;
	}
	
	
  private void throwInvalidPositionException(int pos) {
  	if (!Math2.isBetween(pos, 1, getOwner().getDocument().getAlignedLength()) && (pos != NO_SELECTION)) {
			throw new IllegalArgumentException("Invalid selection position " + pos + ". The selection borders " +
					"have to be between 1 and the alignment length + (" + getOwner().getDocument().getAlignedLength() + ").");
  	}
  }
	
  
  public void setNewSelection(int firstPos, int lastPos) {
		throwInvalidPositionException(firstPos);
		throwInvalidPositionException(lastPos);
		
  	if (firstPos > lastPos) {
  		this.firstPos = lastPos;
  		this.lastPos = firstPos;
  	}
  	else {
  		this.firstPos = firstPos;
  		this.lastPos = lastPos;
  	}
		getOwner().fireColumnSelectionChanged();
  }
  
	
	public void setFirstPos(int firstPos) {
		throwInvalidPositionException(firstPos);
		
		this.firstPos = firstPos;
		if ((getLastPos() < firstPos) || (firstPos == NO_SELECTION)) {  // also true if lastPos == NO_SELECTION
			lastPos = firstPos;
		}
		getOwner().fireColumnSelectionChanged();
	}
	
	
	public void setLastPos(int lastPos) {
		throwInvalidPositionException(lastPos);
		
		if (lastPos == NO_SELECTION) {
			this.lastPos = getFirstPos();
		}
		else {
			if (getFirstPos() > lastPos) {
				this.lastPos = getFirstPos();
				firstPos = lastPos; 
			}
			else {
				if (getFirstPos() == NO_SELECTION) {
					firstPos = lastPos;
				}
				this.lastPos = lastPos;
			}
		}
		getOwner().fireColumnSelectionChanged();
	}
	
	
	public boolean isColumnSelected(int columnIndex) {
		return Math2.isBetween(columnIndex, getFirstPos(), getLastPos());
	}
	
	
	public CommentPosition getCommentPosition() {
		return new CommentPosition(getFirstPos(), getLastPos());
	}
	
	
	public void clearSequenceSelection() {
		setFirstPos(NO_SELECTION);  // lastPos will be set automatically
		getOwner().fireColumnSelectionChanged();
	}
	
	
	public boolean isSequenceSelected() {
		return getFirstPos() != NO_SELECTION;
	}
	
	
	public Comment getComment() {
		return comment;
	}
	
	
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	
	public void clearCommentSelection() {
		setComment(null);
	}
	
	
	public boolean isCommentSelected() {
		return getComment() != null;
	}
	
	
	public void clear() {
		clearSequenceSelection();
		clearCommentSelection();
	}
}
