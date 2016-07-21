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
package info.bioinfweb.alignmentcomparator.gui.actions.file;


import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.io.ImportedAlignmentModelFactory;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.bioinfweb.alignmentcomparator.gui.dialogs.StartComparisonDialog;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.fasta.FASTAEventReader;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.collections4.map.ListOrderedMap;



public class CompareAlignmentsAction extends DocumentAction {
  private StartComparisonDialog dialog = null;
  private ImportedAlignmentModelFactory alignmentModelFactory = new ImportedAlignmentModelFactory(new SequenceIDManager());
  
  
  public CompareAlignmentsAction(MainFrame mainFrame) {
		super(mainFrame);
		dialog = new StartComparisonDialog(getMainFrame());
		putValue(Action.NAME, "Compare alignments"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		putValue(Action.SHORT_DESCRIPTION, "Compare alignments");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
	  loadSymbols("Open");
	}
  
  
  private AlignmentModel<Character> loadAlignment(File file) throws Exception {  //TODO Support loading multiple alignments from a file.
  	AlignmentDataReader reader = new AlignmentDataReader(new FASTAEventReader(file, new ReadWriteParameterMap()),  //TODO Support other formats. (Implement factory or GUI components in JPhyloIO that allow format selection.) 
  			alignmentModelFactory);
  	reader.readAll();
  	return (AlignmentModel<Character>)reader.getAlignmentModelReader().getCompletedModels().get(0);  //TODO Handle additional alignments read from the file or (e.g. Nexus) files without alignments.
  }
  
  
	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog.execute()) {
			try {
				getDocument().setTokenType(dialog.getTokenType());
		  	alignmentModelFactory.setTokenType(dialog.getTokenType());
		  	
				ListOrderedMap<String, ComparedAlignment> map = getDocument().getAlignments();
				map.clear();
				for (int i = 0; i < dialog.getFileListModel().getSize(); i++) {
					File file = dialog.getFileListModel().get(i);
					map.put(file.getAbsolutePath(), new ComparedAlignment(loadAlignment(file)));  //TODO Use shorter key?
				}
				
				dialog.getAlgorithm().performAlignment(getDocument());
				getDocument().registerChange();
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
