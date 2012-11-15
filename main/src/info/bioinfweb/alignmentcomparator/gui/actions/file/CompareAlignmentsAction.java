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


import info.bioinfweb.alignmentcomparator.document.io.FastaReaderTools;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.bioinfweb.alignmentcomparator.gui.dialogs.StartComparisonDialog;
import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.DNASequenceCreator;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;



public class CompareAlignmentsAction extends DocumentAction {
  private StartComparisonDialog dialog = null;
  
  
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
				getDocument().setUnalignedData(FastaReaderTools.readAlignment(new File(dialog.getFirstPath())), 
						FastaReaderTools.readAlignment(new File(dialog.getSecondPath())), dialog.getAlgorithm());
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
