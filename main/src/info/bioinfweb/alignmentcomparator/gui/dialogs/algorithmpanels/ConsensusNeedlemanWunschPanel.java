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


import info.bioinfweb.alignmentcomparator.document.pairalgorithms.ConsensusPairwiseAligner;
import info.bioinfweb.alignmentcomparator.document.pairalgorithms.SuperAlignmentAlgorithm;
import info.webinsel.util.Math2;

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
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;



public class ConsensusNeedlemanWunschPanel extends JPanel implements AlgorithmPreferencesPanel {
	private static final long serialVersionUID = 1L;
	
	
	private JLabel gopLabel = null;
	private JLabel gepLabel = null;
	private JFormattedTextField gopTextField;
	private JFormattedTextField gepTextField;
	private JTextField mismatchTextField;

	
	/**
	 * This is the default constructor
	 */
	public ConsensusNeedlemanWunschPanel() {
		super();
		initialize();
	}

	
	@Override
	public SuperAlignmentAlgorithm getAlgorithm() {
		GapPenalty gapPenalty = new SimpleGapPenalty(Short.parseShort(gopTextField.getText()), Short.parseShort(gepTextField.getText()));
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
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gopLabel = new JLabel();
		gopLabel.setText("Gap open penalty: ");
		this.setSize(300, 200);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		this.setLayout(gridBagLayout);
		
		JLabel mismatchPanel = new JLabel("Mismatch penalty: ");
		GridBagConstraints gbc_mismatchPanel = new GridBagConstraints();
		gbc_mismatchPanel.anchor = GridBagConstraints.EAST;
		gbc_mismatchPanel.insets = new Insets(0, 0, 5, 5);
		gbc_mismatchPanel.gridx = 0;
		gbc_mismatchPanel.gridy = 0;
		add(mismatchPanel, gbc_mismatchPanel);
		
		mismatchTextField = new JTextField();
		mismatchTextField.setText("1");
		GridBagConstraints gbc_mismatchTextField = new GridBagConstraints();
		gbc_mismatchTextField.insets = new Insets(0, 0, 5, 0);
		gbc_mismatchTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_mismatchTextField.gridx = 1;
		gbc_mismatchTextField.gridy = 0;
		add(mismatchTextField, gbc_mismatchTextField);
		mismatchTextField.setColumns(10);
		this.add(gopLabel, gridBagConstraints);
		
		gopTextField = new JFormattedTextField();
		gopTextField.setText("1");
		GridBagConstraints gbc_gopTextField = new GridBagConstraints();
		gbc_gopTextField.insets = new Insets(0, 0, 5, 0);
		gbc_gopTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_gopTextField.gridx = 1;
		gbc_gopTextField.gridy = 1;
		add(gopTextField, gbc_gopTextField);
		gopTextField.setColumns(10);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gepLabel = new JLabel();
		gepLabel.setText("Gap extension penalty: ");
		this.add(gepLabel, gridBagConstraints1);
		
		gepTextField = new JFormattedTextField();
		gepTextField.setText("1");
		GridBagConstraints gbc_gepTextField = new GridBagConstraints();
		gbc_gepTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_gepTextField.gridx = 1;
		gbc_gepTextField.gridy = 2;
		add(gepTextField, gbc_gepTextField);
		gepTextField.setColumns(10);
	}
}
