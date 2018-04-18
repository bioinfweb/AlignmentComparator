/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/AlignmentComparator>
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
import info.bioinfweb.alignmentcomparator.document.comment.Comment;
import info.bioinfweb.alignmentcomparator.document.comment.CommentAnchor;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



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


	private AlignmentArea getFocusedOrFirstArea() {
		//TODO A better implementation would be to store the area that has the focus before, of none currently has it.
		//     Something like a focus listener would be necessary for this.
		//     Such a behavior could be implemented MultipleAlignmentsContainer.getFocusedAlignmentArea() in the future.
		
		AlignmentArea result = getOwner().getFocusedAlignmentArea();  // Returns null if the focus is outside of the MultipleAlignmentsContainer.
		if (result == null) {
			result = getOwner().getFirstAlignmentArea();
		}
		return result;
	}
	
	
	public int getFirstPos() {
		return getFocusedOrFirstArea().getSelection().getFirstColumn();
	}
	
	
	public int getLastPos() {
		return getFocusedOrFirstArea().getSelection().getLastColumn();
	}
	
	
	public boolean isColumnSelected(int columnIndex) {
		return getFocusedOrFirstArea().getSelection().isSelected(columnIndex, 0);
	}
	
	
	public boolean isSequenceSelected() {
		return (getOwner().getFirstAlignmentArea() != null) && !getOwner().getFirstAlignmentArea().getSelection().isEmpty();
	}
	
	
	public Comment getComment() {
		return comment;
	}


	public void setComment(Comment comment) {
		if (this.comment != comment) {
			this.comment = comment;
			Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();
			Main.getInstance().getMainFrame().getComparisonComponent().getCommentArea().repaint();  //TODO Is this sufficient/the optimal way?
		}
	}


	public boolean isCommentSelected() {
		return getComment() != null;
	}
	
	
	public CommentAnchor createCommentAnchor() {
		return new CommentAnchor(getFirstPos(), getLastPos());
	}
	
	
	public void clearComment() {
		setComment(null);
	}
	
	
	public void clear() {
		if (getOwner().getFirstAlignmentArea() != null) {
			getOwner().getFirstAlignmentArea().getSelection().clear();
		}
		clearComment();
	}
}
