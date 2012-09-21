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
package info.bioinfweb.alignmentcomparator.document.undo;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



public class AddCommentEdit extends DocumentEdit {
	private Comment comment;
	
	
	public AddCommentEdit(Document document, Comment comment) {
		super(document);
		this.comment = comment;
	}


	@Override
	public void redo() throws CannotRedoException {
		getDocument().getComments().add(comment);
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		getDocument().getComments().remove(comment);
		super.undo();
	}

	
	@Override
	public String getPresentationName() {
		return "Add comment at column " + comment.getPosition().getFirstPos();
	}
}
