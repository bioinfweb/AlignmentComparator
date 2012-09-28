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
package info.bioinfweb.alignmentcomparator.gui.actions.edit;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;

import javax.swing.Action;
import javax.swing.undo.UndoableEdit;



public abstract class UndoRedoToAction extends DocumentAction {
  protected UndoableEdit edit;

  
	public UndoRedoToAction(MainFrame mainFrame, UndoableEdit edit) {
		super(mainFrame);
		this.edit = edit;
		putValue(Action.NAME, edit.getPresentationName()); 
	}


	@Override
	public void setEnabled() {
		setEnabled(getDocument().getUndoManager().contains(edit));
	}
}
