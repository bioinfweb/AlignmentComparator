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
package info.bioinfweb.alignmentcomparator.gui.actions;


import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.undo.UndoableEdit;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.edit.*;
import info.bioinfweb.alignmentcomparator.gui.actions.file.*;
import info.bioinfweb.alignmentcomparator.gui.actions.help.*;
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
	 * All {@link Action} objects used in XMLFormatCreator are added to the {@link HashMap}
	 * in this method. New actions should be added here as well.
	 */
	protected void fillMap() {
		put("file.compareAlignments", new CompareAlignmentsAction(mainFrame));
		put("file.openResults", new OpenResultsAction(mainFrame));
		put("file.save", new SaveAction(mainFrame));
		put("file.saveAs", new SaveAsAction(mainFrame));
		
		put("edit.undo", new UndoAction(mainFrame));
		put("edit.redo", new RedoAction(mainFrame));
		put("edit.insertSupergap", new InsertSupergapAction(mainFrame));
		put("edit.removeSupergapBackwards", new RemoveSupergapAction(mainFrame, true));
		put("edit.removeSupergapForward", new RemoveSupergapAction(mainFrame, false));
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