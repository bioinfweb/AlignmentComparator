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


import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.undo.RemoveGapEdit;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;



public class RemoveSupergapAction extends SupergapAction {
	private boolean backwards;
	
	
	public RemoveSupergapAction(MainFrame mainFrame, boolean backwards) {
		super(mainFrame);
		this.backwards = backwards;
		putValue(Action.NAME, "Delete supergap"); 
		putValue(Action.SHORT_DESCRIPTION, "Delete supergap"); 
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof SequenceArea) {
			AlignmentArea area = ((SequenceArea)e.getSource()).getOwner().getOwner();
			SelectionModel selection = area.getSelection();
			
			int firstColumn = selection.getFirstColumn();
			int behindLastColumn = selection.getLastColumn() + 1;
			if (backwards && (selection.getWidth() == 0)) {
				firstColumn--;  // Delete column left of the cursor
				behindLastColumn = firstColumn + 1;
			}
			
			if (((SuperalignedModelDecorator)area.getAlignmentModel()).containsSupergap(firstColumn, behindLastColumn)) {
				getDocument().executeEdit(new RemoveGapEdit(getDocument(), 
						getDocument().getAlignments().get(getMainFrame().getComparisonComponent().getAlignmentAreas().indexOf(area) - 
								AlignmentComparisonComponent.FIRST_ALIGNMENT_INDEX), firstColumn, behindLastColumn));
				
				area.getSelection().setNewCursorColumn(firstColumn);
			}
			else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
}
