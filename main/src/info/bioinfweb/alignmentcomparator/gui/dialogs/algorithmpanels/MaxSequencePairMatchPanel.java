package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch.MaxSequencePairMatchAligner;

import javax.swing.JPanel;



public class MaxSequencePairMatchPanel extends JPanel implements AlgorithmPreferencesPanel {
	@Override
	public SuperAlignmentAlgorithm getAlgorithm() {
		return new MaxSequencePairMatchAligner();
	}
}
