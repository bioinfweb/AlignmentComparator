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


import java.awt.event.ActionEvent;

import info.bioinfweb.alignmentcomparator.document.undo.InsertGapEdit;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;



public class InsertGapAction extends InsertRemoveGapAction {
	public InsertGapAction(MainFrame mainFrame, boolean inFirstAlignment) {
		super(mainFrame, inFirstAlignment);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
//		int otherIndex = isInFirstAlignment() ? 1 : 0;
//		if (getDocument().containsSuperGap(otherIndex, getSelection().getFirstPos(), 
//				getSelection().getLastPos())) {  // This is checked here and not in setEnabled() to be able to tell the user why the operation is not possible 
//			
//			JOptionPane.showMessageDialog(getMainFrame(), "You cannot insert a gap in this interval, because " +
//					"the other alignment already contains a gap at this position.", "Invalid operation", 
//					JOptionPane.ERROR_MESSAGE);
//		}
//		else {
			getDocument().executeEdit(new InsertGapEdit(getDocument(), isInFirstAlignment(), 
					getSelection().getFirstPos(),	getSelection().getLastPos()));
//		}
	}

	
	@Override
	public void setEnabled() {
		setEnabled(getSelection().isSequenceSelected() && !getDocument().containsSuperGap(
				isInFirstAlignment() ? 1 : 0,	getSelection().getFirstPos(),	getSelection().getLastPos()));
	}
}
