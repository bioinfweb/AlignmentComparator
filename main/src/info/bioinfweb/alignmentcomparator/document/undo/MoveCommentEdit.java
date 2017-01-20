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
package info.bioinfweb.alignmentcomparator.document.undo;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.SingleCommentAnchor;



public class MoveCommentEdit extends CommentEdit {
  private SingleCommentAnchor newPosition = null;
  private SingleCommentAnchor oldPosition = null;

  
	public MoveCommentEdit(Document document, Comment comment, SingleCommentAnchor newPosition) {
		super(document, comment);
		this.newPosition = newPosition;
		oldPosition = getComment().getPosition();
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		getDocument().getComments().move(getComment(), newPosition.getFirstPos(), newPosition.getLastPos());
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getDocument().getComments().move(getComment(), oldPosition.getFirstPos(), oldPosition.getLastPos());
		super.undo();
	}
  
  
	@Override
	public String getPresentationName() {
		return "Move comment from " + oldPosition.getFirstPos() + " to " + newPosition.getFirstPos();
	}
}
