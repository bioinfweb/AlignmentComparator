package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


import javax.swing.JPanel;

import info.bioinfweb.alignmentcomparator.document.pairalgorithms.BioInfWebConsensusPairwiseAligner;
import info.bioinfweb.alignmentcomparator.document.pairalgorithms.SuperAlignmentAlgorithm;
import info.bioinfweb.util.alignment.pairwise.NeedlemanWunschAligner;



public class ConsensusBIWNeedlemanWunschPanel extends JPanel implements AlgorithmPreferencesPanel {
	@Override
	public SuperAlignmentAlgorithm getAlgorithm() {
		return new BioInfWebConsensusPairwiseAligner(new NeedlemanWunschAligner());
	}
}
