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
import info.bioinfweb.commons.Math2;



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
	
	
	private int startColumn = NO_SELECTION;
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
	
	
  private int secureValidPosition(int pos) {
  	if (pos != NO_SELECTION) {
  		pos = Math.max(1, Math.min(getOwner().getDocument().getAlignedLength(), pos));
  	}
  	return pos;
  }
	
  
  public void setNewSelection(int pos) {
  	pos = secureValidPosition(pos);

  	if (pos == NO_SELECTION) {
  		throw new IllegalArgumentException("This method cannot be called with the value NO_SELECTION.");
  	}
  	else {
  		firstPos = pos;
  		lastPos = pos;
  		startColumn = pos;
			getOwner().fireColumnSelectionChanged();
  	}
  }
  
	
	public void setFirstPos(int firstPos) {
  	firstPos = secureValidPosition(firstPos);
		
		this.firstPos = firstPos;
		if ((getLastPos() < firstPos) || (firstPos == NO_SELECTION)) {  // also true if lastPos == NO_SELECTION
			lastPos = firstPos;
		}
		getOwner().fireColumnSelectionChanged();
	}
	
	
	public void setLastPos(int lastPos) {
  	lastPos = secureValidPosition(lastPos);
		
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
	
	
	public void moveSelectionStart(int columnCount) {
		int pos = 1;
		if (isSequenceSelected()) {
			pos = getFirstPos();
			if ((startColumn != NO_SELECTION) && (startColumn == getFirstPos())) {
				pos = getLastPos();
			}
		}
		startColumn = pos;
		pos += columnCount;
		setNewSelection(pos);  // calls fireColumnSelectionChanged()
	}
		
	
	public void extendSelectionTo(int column) {
		if (startColumn < column) {
			firstPos = startColumn;  // Not using the setter to avoid firing two events
			setLastPos(column);
		}
		else {
			firstPos = column;  // Not using the setter to avoid firing two events
			setLastPos(startColumn);
		}
	}
	

	public void extendSelectionRelatively(int columnCount) {
		if (!isSequenceSelected()) {
			startColumn = 1;
			firstPos = 1;  // setter not used to avoid firing a second event 
			setLastPos(columnCount);  // calls fireColumnSelectionChanged()
		}
		else {
			if ((getLastPos() > startColumn) || (getFirstPos() == startColumn)) {  // second condition must not be checked, if first is true
				setLastPos(getLastPos() + columnCount);  // calls fireColumnSelectionChanged()
			}
			else if (getFirstPos() < startColumn) {
				setFirstPos(getFirstPos() + columnCount);  // calls fireColumnSelectionChanged()
			}
			else {
				setNewSelection(startColumn);  // calls fireColumnSelectionChanged()
			}
		}
	}
		
	
	public CommentPosition getCommentPosition() {
		return new CommentPosition(getFirstPos(), getLastPos());
	}
	
	
	public void clearSequenceSelection() {
		startColumn = NO_SELECTION;
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
		getOwner().fireColumnSelectionChanged();
	}
	
	
	public void clearCommentSelection() {
		setComment(null);
	}
	
	
	public boolean isCommentSelected() {
		return getComment() != null;
	}
	
	
	public void clear() {
		comment  = null;  // avoid firing two events
		clearSequenceSelection();
	}
}
