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
package info.bioinfweb.alignmentcomparator.gui.actions.file;


import info.bioinfweb.alignmentcomparator.document.SuperAlignmentCompoundSet;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.bioinfweb.alignmentcomparator.gui.dialogs.StartComparisonDialog;
import info.bioinfweb.commons.bio.biojava3.core.sequence.io.FastaReaderTools;
import info.bioinfweb.libralign.sequenceprovider.tokenset.BioJavaTokenSet;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JOptionPane;



public class CompareAlignmentsAction extends DocumentAction {
  private StartComparisonDialog dialog = null;
//  private FastaReader<DNASequence, NucleotideCompound> fastaReader = 
//  		new FastaReader<DNASequence, NucleotideCompound>(new DNASequenceCreator(
//  				new AlignmentAmbiguityNucleotideCompoundSet()));
  
  
  public CompareAlignmentsAction(MainFrame mainFrame) {
		super(mainFrame);
		dialog = new StartComparisonDialog(getMainFrame());
		putValue(Action.NAME, "Compare alignments"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		putValue(Action.SHORT_DESCRIPTION, "Compare alignments");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
	  loadSymbols("Open");
	}

  
	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog.execute()) {
			try {
				File firstFile = new File(dialog.getFirstPath());
				File secondFile = new File(dialog.getSecondPath());
				getDocument().setUnalignedData(firstFile.getAbsolutePath(), FastaReaderTools.readDNAAlignment(firstFile), 
				    secondFile.getAbsolutePath(), FastaReaderTools.readDNAAlignment(secondFile),
				    new BioJavaTokenSet(SuperAlignmentCompoundSet.getSuperAlignmentCompoundSet(), false),  //TODO Also allow protein sequences and token sets.
				    dialog.getAlgorithm());
//				getDocument().setUnalignedData(fastaReader.read(new File(dialog.getFirstPath())),
//						fastaReader.read(new File(dialog.getSecondPath())), dialog.getAlgorithm());
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(getMainFrame(), "An IO error occurred while loading the files.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void setEnabled() {}  // nothing to do (opening new files is always possible)
}
