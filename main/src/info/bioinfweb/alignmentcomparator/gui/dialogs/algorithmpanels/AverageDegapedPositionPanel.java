package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


import info.bioinfweb.alignmentcomparator.document.superalignment.AverageDegapedPositionAligner;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;

import javax.swing.JPanel;



public class AverageDegapedPositionPanel extends JPanel implements AlgorithmPreferencesPanel {
	@Override
	public SuperAlignmentAlgorithm getAlgorithm() {
		return new AverageDegapedPositionAligner();
	}
}
