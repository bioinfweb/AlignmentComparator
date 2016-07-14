/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
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


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonSelection;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.commons.swing.ExtendedAbstractAction;



public abstract class DocumentAction extends ExtendedAbstractAction {
  private MainFrame mainFrame = null;

  
	public DocumentAction(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
	}


	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	
	public Document getDocument() {
		return getMainFrame().getDocument();
	}
	
	
	public AlignmentComparisonSelection getSelection() {  //TODO Rename to alignmentSelection
		return getMainFrame().getSelection();
	}
	
	
	public abstract void setEnabled();
}
