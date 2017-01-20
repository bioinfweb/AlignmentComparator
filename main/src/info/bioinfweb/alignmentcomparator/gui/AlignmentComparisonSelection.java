/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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
import info.bioinfweb.alignmentcomparator.document.comments.SingleCommentAnchor;



/**
 * Represents the selection in a {@link AlignmentComparisonPanel}.
 * 
 * @author Ben St&ouml;ver
 */
public class AlignmentComparisonSelection {
  private AlignmentComparisonComponent owner;
  private Comment comment = null;
  
  
	public AlignmentComparisonSelection(AlignmentComparisonComponent owner) {
		super();
		this.owner = owner;
	}


	public AlignmentComparisonComponent getOwner() {
		return owner;
	}


	public int getFirstPos() {
		return getOwner().getFirstAlignmentArea().getSelection().getFirstColumn();
	}
	
	
	public int getLastPos() {
		return getOwner().getFirstAlignmentArea().getSelection().getLastColumn();
	}
	
	
	public boolean isColumnSelected(int columnIndex) {
		return getOwner().getFirstAlignmentArea().getSelection().isSelected(columnIndex, 0);
	}
	
	
	public SingleCommentAnchor getCommentPosition() {
		return new SingleCommentAnchor(getFirstPos(), getLastPos());
	}
	
	
	public boolean isSequenceSelected() {
		return (getOwner().getFirstAlignmentArea() != null) && !getOwner().getFirstAlignmentArea().getSelection().isEmpty();
	}
	
	
	public Comment getComment() {
		return comment;
	}
	
	
	public void setComment(Comment comment) {
		this.comment = comment;
		//getOwner().fireColumnSelectionChanged();  //TODO reimplement
	}
	
	
	public void clearCommentSelection() {
		setComment(null);
	}
	
	
	public boolean isCommentSelected() {
		return getComment() != null;
	}
	
	
	public void clear() {
		comment  = null;  // avoid firing two events
		if (getOwner().getFirstAlignmentArea() != null) {
			getOwner().getFirstAlignmentArea().getSelection().clear();
		}
	}
}
