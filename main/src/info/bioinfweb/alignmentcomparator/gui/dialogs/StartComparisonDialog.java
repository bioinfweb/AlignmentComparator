/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.superalignment.CompareAlgorithm;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels.AlgorithmPreferencesPanel;
import info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels.AlgorithmPreferencesPanelFactory;
import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.commons.io.ExtensionFileFilter;
import info.bioinfweb.jphyloio.JPhyloIOFormatSpecificObject;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JRadioButton;

import org.apache.commons.collections4.set.ListOrderedSet;



public class StartComparisonDialog extends OkCancelApplyWikiHelpDialog {
	public static final class FileSelection {
		private File file;
		private String format;
		
		public FileSelection(File file, String format) {
			super();
			this.file = file;
			this.format = format;
		}

		public File getFile() {
			return file;
		}

		public String getFormat() {
			return format;
		}

		@Override
		public String toString() {
			String formatName = "";
			if (getFormat() != null) {
				formatName = " (" + Main.getInstance().getReaderWriterFactory().getFormatInfo(getFormat()).getFormatName() + ")";
			}
			return "\"" + getFile().toString() + "\"" + formatName;
		}
	}
	
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JPanel alignmentsPanel = null;
	private JButton addPathButton = null;
	private JButton removeButton = null;
	private AlgorithmPreferencesPanel preferencesPanel = null;
	private JFileChooser fileChooser = null;
	private JPanel algorithmPanel;
	private JComboBox<CompareAlgorithm> algorithmComboBox;
	private JPanel outerPreferencesPanel;
	private JList<FileSelection> fileList;
	private JPanel tokenTypePanel;
	private ButtonGroup tokenTypeGroup;
	private JRadioButton nucleotideRadioButton;
	private JRadioButton aminoAcidRadioButton;
	private JRadioButton otherRadioButton;
	
	private ExtensionFileFilter allFormatsFilter;
	
	
	/**
	 * @param owner
	 */
	public StartComparisonDialog(Frame owner) {
		super(owner, Main.getInstance().getWikiHelp());
		setModal(true);
		setHelpCode(3);  //TODO Adjust help code before next release.
		initialize();
	}

	
	@Override
	public boolean execute() {
		getFileListModel().clear();
		getRemoveButton().setEnabled(false);
		return super.execute();
	}


	@Override
	protected boolean apply() {
		boolean enoughFiles = (getFileListModel().size() >= 2);  //TODO Does this have to be adjusted, if multiple alignments come from one file?
		if (!enoughFiles) {
			JOptionPane.showMessageDialog(this, "At least two alignment files need to be selected for comparison.", 
					"Not enough files", JOptionPane.ERROR_MESSAGE);
		}
		return enoughFiles;
	}
	
	
	public CharacterStateSetType getTokenType() {
		if (nucleotideRadioButton.isSelected()) {
			return CharacterStateSetType.NUCLEOTIDE;
		}
		else if (aminoAcidRadioButton.isSelected()) {
			return CharacterStateSetType.AMINO_ACID;
		}
		else {
			return CharacterStateSetType.DISCRETE;
		}
	}
	
	
	public DefaultListModel<FileSelection> getFileListModel() {
		return (DefaultListModel<FileSelection>)getFileList().getModel();
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
		setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		pack();
	}

	
	private JFileChooser getFileChooser() {
  	if (fileChooser == null) {
  		fileChooser = new JFileChooser();
  		
  		fileChooser.setMultiSelectionEnabled(true);
  		fileChooser.setAcceptAllFileFilterUsed(true);
  		
  		JPhyloIOReaderWriterFactory factory = Main.getInstance().getReaderWriterFactory();
  		ListOrderedSet<String> validExtensions = new ListOrderedSet<String>();
  		for (String formatID : factory.getFormatIDsSet()) {
  			JPhyloIOFormatInfo info = factory.getFormatInfo(formatID);
  			if (info.isElementModeled(EventContentType.ALIGNMENT, true)) {
  					ContentExtensionFileFilter filter = info.createFileFilter(TestStrategy.BOTH);
  					validExtensions.addAll(filter.getExtensions());
  					fileChooser.addChoosableFileFilter(filter);
  			}
  		}
  		allFormatsFilter = new ExtensionFileFilter("All supported formats", false, validExtensions.asList());
  		fileChooser.addChoosableFileFilter(allFormatsFilter);
  		fileChooser.setFileFilter(allFormatsFilter);
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
			jContentPane.add(getTokenTypePanel());
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
			GridBagConstraints firstButtonGBC = new GridBagConstraints();
			firstButtonGBC.insets = new Insets(0, 0, 5, 0);
			firstButtonGBC.fill = GridBagConstraints.HORIZONTAL;
			firstButtonGBC.gridx = 1;
			firstButtonGBC.gridy = 0;
			GridBagConstraints secondButtonGBC = new GridBagConstraints();
			secondButtonGBC.fill = GridBagConstraints.HORIZONTAL;
			secondButtonGBC.gridx = 1;
			secondButtonGBC.gridy = 1;
			alignmentsPanel = new JPanel();
			GridBagLayout gbl_alignmentsPanel = new GridBagLayout();
			gbl_alignmentsPanel.rowWeights = new double[]{1.0, 0.0};
			gbl_alignmentsPanel.columnWeights = new double[]{1.0, 0.0};
			alignmentsPanel.setLayout(gbl_alignmentsPanel);
			alignmentsPanel.setBorder(BorderFactory.createTitledBorder(null, "Alignments", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			GridBagConstraints gbc_list = new GridBagConstraints();
			gbc_list.gridheight = 2;
			gbc_list.weighty = 1.0;
			gbc_list.weightx = 1.0;
			gbc_list.insets = new Insets(0, 0, 5, 5);
			gbc_list.fill = GridBagConstraints.BOTH;
			gbc_list.gridx = 0;
			gbc_list.gridy = 0;
			alignmentsPanel.add(getFileList(), gbc_list);
			alignmentsPanel.add(getAddButton(), firstButtonGBC);
			alignmentsPanel.add(getRemoveButton(), secondButtonGBC);
		}
		return alignmentsPanel;
	}



	/**
	 * This method initializes firstPathButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddButton() {
		if (addPathButton == null) {
			addPathButton = new JButton();
			addPathButton.setText("Add...");
			final StartComparisonDialog thisDialog = this;
			addPathButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (getFileChooser().showOpenDialog(thisDialog) == JFileChooser.APPROVE_OPTION) {
						StringBuilder skippedFiles = new StringBuilder();
						for (File file : getFileChooser().getSelectedFiles()) {
							if (file.exists() && !getFileListModel().contains(file)) {
								String formatID = null;
								if (getFileChooser().getFileFilter() instanceof JPhyloIOFormatSpecificObject) {  // Otherwise one of the "all formats" filter was used and the format needs to be guessed.
									formatID = ((JPhyloIOFormatSpecificObject)getFileChooser().getFileFilter()).getFormatID();
								}
								getFileListModel().addElement(new FileSelection(file, formatID));
							}
							else {
								skippedFiles.append(file.getAbsolutePath() + "\n");
							}
						}
						if (skippedFiles.length() > 0) {
							JOptionPane.showMessageDialog(thisDialog, 
									"The following files were skipped, because the do not exist or are already in the list:\n\n" 
									 + skippedFiles.toString(), "Invalid file(s)", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			});
		}
		return addPathButton;
	}


	/**
	 * This method initializes secondPathButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = getFileList().getSelectedIndex();
					if (index != -1) {
						getFileListModel().remove(index);
					}
				}
			});
		}
		return removeButton;
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
			algorithmComboBox.addItem(CompareAlgorithm.MAX_SEQUENCE_PAIR_MATCH);
			algorithmComboBox.addItem(CompareAlgorithm.AVERAGE_UNGAPED_POSITION);
//			CompareAlgorithm[] algorithms = CompareAlgorithm.class.getEnumConstants();
//			for (int i = 0; i < algorithms.length; i++) {
//		    algorithmComboBox.addItem(algorithms[i]);
//		  }
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
	
	
	private JList<FileSelection> getFileList() {
		if (fileList == null) {
			fileList = new JList<FileSelection>(new DefaultListModel<FileSelection>());
			fileList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					getRemoveButton().setEnabled(getFileList().getSelectedIndex() != -1);
				}
			});
			fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return fileList;
	}
	
	
	private JPanel getTokenTypePanel() {
		if (tokenTypePanel == null) {
			tokenTypePanel = new JPanel();
			tokenTypePanel.setBorder(new TitledBorder(null, "Token type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			getTokenTypeGroup();
			tokenTypePanel.add(getNucleotideRadioButton());
			tokenTypePanel.add(getAminoAcidRadioButton());
			//tokenTypePanel.add(getOtherRadioButton());  //TODO If other token types are supported, String tokens instead of chacter tokens must be used in such cases.
		}
		return tokenTypePanel;
	}
	
	
	private ButtonGroup getTokenTypeGroup() {
		if (tokenTypeGroup == null) {
			tokenTypeGroup = new ButtonGroup();
			tokenTypeGroup.add(getNucleotideRadioButton());
			tokenTypeGroup.add(getAminoAcidRadioButton());
			//tokenTypeGroup.add(getOtherRadioButton());  //TODO If other token types are supported, String tokens instead of chacter tokens must be used in such cases.
		}
		return tokenTypeGroup;
	}


	private JRadioButton getNucleotideRadioButton() {
		if (nucleotideRadioButton == null) {
			nucleotideRadioButton = new JRadioButton("Nucleotide");
			nucleotideRadioButton.setSelected(true);
		}
		return nucleotideRadioButton;
	}
	
	
	private JRadioButton getAminoAcidRadioButton() {
		if (aminoAcidRadioButton == null) {
			aminoAcidRadioButton = new JRadioButton("Amino acid");
		}
		return aminoAcidRadioButton;
	}
	
	
	private JRadioButton getOtherRadioButton() {
		if (otherRadioButton == null) {
			otherRadioButton = new JRadioButton("Other discrete tokens");
		}
		return otherRadioButton;
	}
}
