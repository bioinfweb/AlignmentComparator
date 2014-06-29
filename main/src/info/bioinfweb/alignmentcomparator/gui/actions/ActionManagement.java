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
package info.bioinfweb.alignmentcomparator.gui.actions;


import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.undo.UndoableEdit;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.AddCommentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.ChangeCommentTextAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.InsertGapFirstAlignmentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.InsertGapSecondAlignmentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.MoveCommentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.RedoAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.RedoToAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.RemoveCommentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.RemoveGapFirstAlignmentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.RemoveGapSecondAlignmentAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.UndoAction;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.UndoToAction;
import info.bioinfweb.alignmentcomparator.gui.actions.file.CompareAlignmentsAction;
import info.bioinfweb.alignmentcomparator.gui.actions.file.OpenResultsAction;
import info.bioinfweb.alignmentcomparator.gui.actions.file.SaveAction;
import info.bioinfweb.alignmentcomparator.gui.actions.file.SaveAsAction;
import info.bioinfweb.alignmentcomparator.gui.actions.help.AboutAction;
import info.bioinfweb.alignmentcomparator.gui.actions.help.WebsiteAction;
import info.bioinfweb.commons.swing.AbstractUndoActionManagement;
import info.bioinfweb.commons.swing.AccessibleUndoManager;



public class ActionManagement extends AbstractUndoActionManagement {
	private MainFrame mainFrame = null;
	
	
	public ActionManagement(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		fillMap();
	}


	/**
	 * All <code>Action</code> objects used in XMLFormatCreator are added to the <code>HashMap</code>
	 * in this method. New actions should be added here as well.
	 */
	protected void fillMap() {
		put("file.compareAlignments", new CompareAlignmentsAction(mainFrame));
		put("file.openResults", new OpenResultsAction(mainFrame));
		put("file.save", new SaveAction(mainFrame));
		put("file.saveAs", new SaveAsAction(mainFrame));
		
		put("edit.undo", new UndoAction(mainFrame));
		put("edit.redo", new RedoAction(mainFrame));
		put("edit.insertGapFirst", new InsertGapFirstAlignmentAction(mainFrame));
		put("edit.insertGapSecond", new InsertGapSecondAlignmentAction(mainFrame));
		put("edit.removeGapFirst", new RemoveGapFirstAlignmentAction(mainFrame));
		put("edit.removeGapSecond", new RemoveGapSecondAlignmentAction(mainFrame));
		put("edit.addComment", new AddCommentAction(mainFrame));
		put("edit.moveComment", new MoveCommentAction(mainFrame));
		put("edit.changeCommentText", new ChangeCommentTextAction(mainFrame));
		put("edit.removeComment", new RemoveCommentAction(mainFrame));

		put("help.about", new AboutAction());
		put("help.website", new WebsiteAction());
	}
	
	
	private void setActionStatusBySelection() {
	  Iterator<Action> iterator = getMap().values().iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next();
			if (action instanceof DocumentAction) {
			  ((DocumentAction)action).setEnabled();
			}
		}
	}	


	public void refreshActionStatus() {
		setActionStatusBySelection();
		editUndoRedoMenus();
	}


	@Override
	protected AccessibleUndoManager getUndoManager() {
		return mainFrame.getDocument().getUndoManager();
	}


	@Override
	protected JMenu getUndoMenu() {
		return mainFrame.getUndoMenu();
	}


	@Override
	protected JMenu getRedoMenu() {
		return mainFrame.getRedoMenu();
	}


	@Override
	protected Action createUndoAction(UndoableEdit edit) {
		return new UndoToAction(mainFrame, edit);
	}


	@Override
	protected Action createRedoAction(UndoableEdit edit) {
		return new RedoToAction(mainFrame, edit);
	}
}