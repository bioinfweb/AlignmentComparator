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
package info.bioinfweb.alignmentcomparator.gui.dialogs;


import info.bioinfweb.alignmentcomparator.document.superalignment.CompareAlgorithm;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels.AlgorithmPreferencesPanel;
import info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels.AlgorithmPreferencesPanelFactory;
import info.webinsel.util.swing.OkCancelApplyDialog;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



public class StartComparisonDialog extends OkCancelApplyDialog {
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JPanel alignmentsPanel = null;
	private JTextField firstPathTextField = null;
	private JButton firstPathButton = null;
	private JTextField secondPathTextField = null;
	private JButton secondPathButton = null;
	private AlgorithmPreferencesPanel preferencesPanel = null;
	private JFileChooser fileChooser = null;
	private JPanel algorithmPanel;
	private JComboBox<CompareAlgorithm> algorithmComboBox;
	private JPanel outerPreferencesPanel;

	
	/**
	 * @param owner
	 */
	public StartComparisonDialog(Frame owner) {
		super(owner);
		setModal(true);
		initialize();
	}

	
	@Override
	protected boolean apply() {
		return true;
	}
	
	
	public String getFirstPath() {
		return getFirstPathTextField().getText();
	}
	
	
	public String getSecondPath() {
		return getSecondPathTextField().getText();
	}
	
	
	public SuperAlignmentAlgorithm getAlgorithm() {
		return getPreferencesPanel().getAlgorithm();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Compare alignments");
		//this.setSize(300, 200);
		setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		pack();
	}

	
	private JFileChooser getFileChooser() {
  	if (fileChooser == null) {
  		fileChooser = new JFileChooser();
  		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTA alignment", "fasta", "fas"));
  	}
  	return fileChooser;
  }

  
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
			jContentPane.add(getAlignmentsPanel(), null);
			jContentPane.add(getAlgorithmPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes alignmentsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAlignmentsPanel() {
		if (alignmentsPanel == null) {
			GridBagConstraints firstTextGBC = new GridBagConstraints();
			firstTextGBC.gridx = 0;
			firstTextGBC.gridy = 0;
			firstTextGBC.weightx = 1.0;
			firstTextGBC.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints firstButtonGBC = new GridBagConstraints();
			firstButtonGBC.gridx = 1;
			firstButtonGBC.gridy = 0;
			GridBagConstraints secondTextGBC = new GridBagConstraints();
			secondTextGBC.gridx = 0;
			secondTextGBC.gridy = 1;
			secondTextGBC.weightx = 1.0;
			secondTextGBC.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints secondButtonGBC = new GridBagConstraints();
			secondButtonGBC.gridx = 1;
			secondButtonGBC.gridy = 1;
			alignmentsPanel = new JPanel();
			alignmentsPanel.setLayout(new GridBagLayout());
			alignmentsPanel.setBorder(BorderFactory.createTitledBorder(null, "Alignments", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			alignmentsPanel.add(getFirstPathTextField(), firstTextGBC);
			alignmentsPanel.add(getFirstPathButton(), firstButtonGBC);
			alignmentsPanel.add(getSecondPathTextField(), secondTextGBC);
			alignmentsPanel.add(getSecondPathButton(), secondButtonGBC);
		}
		return alignmentsPanel;
	}


	/**
	 * This method initializes firstPathTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFirstPathTextField() {
		if (firstPathTextField == null) {
			firstPathTextField = new JTextField();
		}
		return firstPathTextField;
	}



	/**
	 * This method initializes firstPathButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFirstPathButton() {
		if (firstPathButton == null) {
			firstPathButton = new JButton();
			firstPathButton.setText("...");
			final StartComparisonDialog thisDialog = this;
			firstPathButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getFileChooser().showOpenDialog(thisDialog) == JFileChooser.APPROVE_OPTION) {
						getFirstPathTextField().setText(getFileChooser().getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		return firstPathButton;
	}


	/**
	 * This method initializes secondPathTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSecondPathTextField() {
		if (secondPathTextField == null) {
			secondPathTextField = new JTextField();
		}
		return secondPathTextField;
	}


	/**
	 * This method initializes secondPathButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSecondPathButton() {
		if (secondPathButton == null) {
			secondPathButton = new JButton();
			secondPathButton.setText("...");
			final StartComparisonDialog thisDialog = this;
			secondPathButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getFileChooser().showOpenDialog(thisDialog) == JFileChooser.APPROVE_OPTION) {
						getSecondPathTextField().setText(getFileChooser().getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		return secondPathButton;
	}


	private void setPereferencesPanel(CompareAlgorithm algorithm) {
		if (preferencesPanel != null) {
			getAlgorithmPanel().remove((Component)preferencesPanel);
		}
		
		preferencesPanel = null;
		if (algorithm != null) {
			preferencesPanel = AlgorithmPreferencesPanelFactory.getInstance().getPanel(algorithm);
		}
		
		getOuterPreferencesPanel().removeAll();
		if (preferencesPanel != null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			getOuterPreferencesPanel().add((JComponent)preferencesPanel, gridBagConstraints);
			getOuterPreferencesPanel().setVisible(true);
		}
		else {
			getOuterPreferencesPanel().setVisible(false);
		}
		pack();
	}
	
	
	private JPanel getAlgorithmPanel() {
		if (algorithmPanel == null) {
			algorithmPanel = new JPanel();
			algorithmPanel.setBorder(BorderFactory.createTitledBorder(null, "Algorithm", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			GridBagLayout gbl_algorithmPanel = new GridBagLayout();
			gbl_algorithmPanel.rowWeights = new double[]{0.0, 1.0};
			gbl_algorithmPanel.columnWeights = new double[]{1.0};
			algorithmPanel.setLayout(gbl_algorithmPanel);
			GridBagConstraints gbc_algorithmComboBox = new GridBagConstraints();
			gbc_algorithmComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_algorithmComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_algorithmComboBox.gridx = 0;
			gbc_algorithmComboBox.gridy = 0;
			algorithmPanel.add(getAlgorithmComboBox(), gbc_algorithmComboBox);
			GridBagConstraints gbc_outerPreferencesPanel = new GridBagConstraints();
			gbc_outerPreferencesPanel.fill = GridBagConstraints.BOTH;
			gbc_outerPreferencesPanel.gridx = 0;
			gbc_outerPreferencesPanel.gridy = 1;
			algorithmPanel.add(getOuterPreferencesPanel(), gbc_outerPreferencesPanel);
		}
		return algorithmPanel;
	}
	
	
	private JComboBox<CompareAlgorithm> getAlgorithmComboBox() {
		if (algorithmComboBox == null) {
			algorithmComboBox = new JComboBox<CompareAlgorithm>();
			algorithmComboBox.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							setPereferencesPanel((CompareAlgorithm)getAlgorithmComboBox().getSelectedItem());
						}
					});
			CompareAlgorithm[] algorithms = CompareAlgorithm.class.getEnumConstants();
			for (int i = 0; i < algorithms.length; i++) {
		    algorithmComboBox.addItem(algorithms[i]);
		  }
		}
		return algorithmComboBox;
	}
	
	
	private JPanel getOuterPreferencesPanel() {
		if (outerPreferencesPanel == null) {
			outerPreferencesPanel = new JPanel();
		}
		return outerPreferencesPanel;
	}


	public AlgorithmPreferencesPanel getPreferencesPanel() {
		return preferencesPanel;
	}
}
