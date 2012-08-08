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
package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


import info.bioinfweb.alignmentcomparator.data.pairalgorithms.ConsensusPairwiseAligner;
import info.bioinfweb.alignmentcomparator.data.pairalgorithms.SuperAlignmentAlgorithm;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.biojava3.alignment.NeedlemanWunsch;
import org.biojava3.alignment.SimpleGapPenalty;
import org.biojava3.alignment.SimpleSubstitutionMatrix;
import org.biojava3.alignment.template.GapPenalty;
import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;



public class ConsensusNeedlemanWunschPanel extends JPanel implements AlgorithmPreferencesPanel {
	private static final long serialVersionUID = 1L;
	private JLabel gopLabel = null;
	private JLabel gepLabel = null;

	
	/**
	 * This is the default constructor
	 */
	public ConsensusNeedlemanWunschPanel() {
		super();
		initialize();
	}

	
	@Override
	public SuperAlignmentAlgorithm getAlgorithm() {
		GapPenalty gapPenalty = new SimpleGapPenalty(gop, gep);
		SubstitutionMatrix<NucleotideCompound> substitutionMatrix = 
			  new SimpleSubstitutionMatrix<NucleotideCompound>();  //TODO Manuell Gleichverteilung angeben, da der BLOSUM Constructor nur für Proteine zulässig ist (wirft Exception)
		NeedlemanWunsch<Sequence<NucleotideCompound>, NucleotideCompound> needlemanWunsch = 
			  new NeedlemanWunsch<Sequence<NucleotideCompound>, NucleotideCompound>();
		needlemanWunsch.setGapPenalty(gapPenalty);
		needlemanWunsch.setSubstitutionMatrix(substitutionMatrix);
		return new ConsensusPairwiseAligner(needlemanWunsch);
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gepLabel = new JLabel();
		gepLabel.setText("JLabel");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gopLabel = new JLabel();
		gopLabel.setText("Gap open penalty: ");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(gopLabel, gridBagConstraints);
		this.add(gepLabel, gridBagConstraints1);
	}
}
