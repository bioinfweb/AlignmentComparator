/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document.undo;


import info.bioinfweb.alignmentcomparator.document.Document;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



public class RemoveGapEdit extends InsertRemoveGapEdit {
	public RemoveGapEdit(Document document, String alignmentName, int startPos,	int endPos) {
		super(document, alignmentName, startPos, endPos);
	}


	@Override
	public void redo() throws CannotRedoException {
		remove();
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		insert();
		super.undo();
	}

	
	@Override
	public String getPresentationName() {
		return "Remove gap(s) at " + getStartPos() + " in alignment \"" + getAlignmentName() + "\"";
	}
}
