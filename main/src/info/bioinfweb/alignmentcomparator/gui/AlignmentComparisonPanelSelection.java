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



/**
 * Represents the selection in a {@link AlignmentComparisonPanel}.
 * 
 * @author Ben St&ouml;ver
 */
public class AlignmentComparisonPanelSelection {
	public static final int NO_SELECTION = -1;
	
	
	private int firstPos = NO_SELECTION;
	private int lastPos = NO_SELECTION;
  private Comment comment = null;
  
  
	public int getFirstPos() {
		return firstPos;
	}
	
	
	public void setFirstPos(int firstPos) {
		this.firstPos = firstPos;
		if ((getLastPos() < firstPos) || (lastPos == NO_SELECTION)) {  // also true if lastPos == NO_SELECTION
			setLastPos(firstPos);
		}
	}
	
	
	public int getLastPos() {
		return lastPos;
	}
	
	
	public void setLastPos(int lastPos) {
		if (lastPos == NO_SELECTION) {
			this.lastPos = getFirstPos();
		}
		else {
			this.lastPos = lastPos;
		}
	}
	
	
	public CommentPosition getCommentPosition() {
		return new CommentPosition(getFirstPos(), getLastPos());
	}
	
	
	public void clearSequenceSelection() {
		setFirstPos(NO_SELECTION);  // lastPos will be set automatically
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
