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
