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
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;
import org.biojava3.core.sequence.io.template.FastaHeaderParserInterface;
import org.biojava3.core.sequence.template.Sequence;



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
				getDocument().setUnalignedData(readAlignment(new File(dialog.getFirstPath())), 
						readAlignment(new File(dialog.getSecondPath())), dialog.getAlgorithm());
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(getMainFrame(), "Error", "An IO error occurred while loading the files.", 
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	
	
	private Map<String, DNASequence> readAlignment(File file) throws IOException {
  	FastaReader<DNASequence, NucleotideCompound> fastaReader = 
  	    new FastaReader<DNASequence, NucleotideCompound>(
    	  		new BufferedInputStream(new FileInputStream(file)),
    	  		new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was würde DNASequenceCreator anders machen? 
		return fastaReader.process();

		//private Map<String, DNASequence> readAlignment(File file) throws IOException {
//    FastaReader<Sequence<NucleotideCompound>, NucleotideCompound> fastaReader = 
//    	  new FastaReader<Sequence<NucleotideCompound>, NucleotideCompound>(
//    	  		new BufferedInputStream(new FileInputStream(file)),
//    	  		//new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
//    	  		new FastaHeaderParserInterface<Sequence<NucleotideCompound>, NucleotideCompound>() {
//    	  			public void parseHeader(String header, Sequence<NucleotideCompound> sequence) {}
//						},
//            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was würde DNASequenceCreator anders machen? 
//    
//		return fastaReader.process();
		//return FastaReaderHelper.readFastaDNASequence(file);
	}


	@Override
	public void setEnabled() {}  // nothing to do (opening new files is always possible)
}
