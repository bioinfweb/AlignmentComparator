package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import java.awt.event.ActionEvent;

import javax.swing.Action;

import info.bioinfweb.alignmentcomparator.document.undo.InsertGapEdit;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;



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
			getDocument().executeEdit(new InsertGapEdit(getDocument(), 
					getDocument().getAlignments().get(getMainFrame().getComparisonComponent().getAlignmentAreas().indexOf(area) - 
							AlignmentComparisonComponent.FIRST_ALIGNMENT_INDEX), 
					area.getSelection().getFirstColumn(), area.getSelection().getLastColumn()));  // lastColumn - 1?
			
//			ListOrderedMap<String, ComparedAlignment> alignments = getDocument().getAlignments();
//			List<Integer> indexList = alignments.get(alignments.get(alignmentIndex)).getSuperAligned().getUnalignedIndices();
//			int start = area.getSelection().getFirstColumn();
//			for (int i = start; i < area.getSelection().getLastColumn(); i++) {
//				indexList.add(start, Document.GAP_INDEX);
//			}
		}
	}
}
