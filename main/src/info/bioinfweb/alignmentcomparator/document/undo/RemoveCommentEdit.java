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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



public class RemoveCommentEdit extends DocumentEdit {
	//TODO Wie soll von GUI erkannt werden, welcher Comment mit der Maus markiert wurde? (Soll Bildschirmposition gespeichert werden oder sollte direkt Positionierungsalgorithmus rekonstruiert werden?)
	

	@Override
	public String getPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redo() throws CannotRedoException {
		// TODO Auto-generated method stub
		super.redo();
	}

	@Override
	public void undo() throws CannotUndoException {
		// TODO Auto-generated method stub
		super.undo();
	}

}
