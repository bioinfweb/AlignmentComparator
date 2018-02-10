/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import java.awt.event.ActionEvent;

import javax.swing.Action;

import info.bioinfweb.alignmentcomparator.document.undo.InsertGapEdit;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;



/**
 * Inserts a supergap into the focused alignment. The length of the gap depends on the width of the current selection.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class InsertSupergapAction extends SupergapAction {
	public InsertSupergapAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Insert supergap"); 
		putValue(Action.SHORT_DESCRIPTION, "Insert supergap"); 
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof SequenceArea) {
			AlignmentArea area = ((SequenceArea)e.getSource()).getOwner().getOwner();
			SelectionModel selection = area.getSelection();
			getDocument().executeEdit(new InsertGapEdit(getDocument(), 
					getDocument().getAlignments().get(getMainFrame().getComparisonComponent().getAlignmentAreas().indexOf(area) - 
							AlignmentComparisonComponent.FIRST_ALIGNMENT_INDEX), 
							selection.getFirstColumn(), selection.getLastColumn() + 1));
			
			selection.setNewCursorColumn(selection.getLastColumn() + 1);  // Move cursor behind inserted gap and clear selection.
		}
	}
}
