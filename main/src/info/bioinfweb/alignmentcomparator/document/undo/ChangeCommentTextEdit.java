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


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



public class ChangeCommentTextEdit extends CommentEdit {
	private String otherText;
	
	
	public ChangeCommentTextEdit(Document document, String newText,	Comment comment) {
		super(document, comment);
		this.otherText = newText;
	}

	
	private void interchangeTexts() {
		String save = getComment().getText();
		getComment().setText(otherText);
		otherText = save;
	}
	

	@Override
	public void redo() throws CannotRedoException {
		interchangeTexts();
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		interchangeTexts();
		super.undo();
	}

	
	@Override
	public String getPresentationName() {
		return "Change text for comment at column " + getComment().getPosition().getFirstPos();
	}
}
