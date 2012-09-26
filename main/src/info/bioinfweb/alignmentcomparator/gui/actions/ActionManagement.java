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

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.webinsel.util.swing.ActionHashMap;



public class ActionManagement extends ActionHashMap {
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
		put("edit.addComment", new AddCommentAction(mainFrame));
		put("edit.moveComment", new MoveCommentAction(mainFrame));
		put("edit.changeCommentText", new ChangeCommentTextAction(mainFrame));
		put("edit.removeComment", new RemoveCommentAction(mainFrame));
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

	@Override
	public void refreshActionStatus() {
		setActionStatusBySelection();
		//TODO undo und redo status setzen
	}
}