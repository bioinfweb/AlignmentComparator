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


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.io.ImportedAlignmentModelFactory;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.bioinfweb.alignmentcomparator.gui.dialogs.StartComparisonDialog;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Iterator;

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
  
  
  private Iterator<AlignmentModel<?>> loadAlignments(File file, String format) throws Exception {
  	ReadWriteParameterMap parameters = new ReadWriteParameterMap();
  	JPhyloIOEventReader eventReader;
		JPhyloIOReaderWriterFactory factory = Main.getInstance().getReaderWriterFactory();
  	if (format == null) {
  		eventReader = factory.guessReader(file, parameters);
  	}
  	else {
  		eventReader = factory.getReader(format, file, parameters);
  	}
  	
  	try {
	  	AlignmentDataReader reader = new AlignmentDataReader(eventReader, alignmentModelFactory);
	  	reader.readAll();
	  	return reader.getAlignmentModelReader().getCompletedModels().iterator();  // The returned iterator maybe empty.
  	}
  	finally {
  		eventReader.close();
  	}
  }
  
  
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog.execute()) {
			try {
				getDocument().setTokenType(dialog.getTokenType());
		  	alignmentModelFactory.setTokenType(dialog.getTokenType());
		  	
				ListOrderedMap<String, ComparedAlignment> map = getDocument().getAlignments();
				map.clear();
		  	StringBuffer warningMessage = new StringBuffer();
				for (int i = 0; i < dialog.getFileListModel().getSize(); i++) {
					StartComparisonDialog.FileSelection fileSelection = dialog.getFileListModel().get(i);
					Iterator<AlignmentModel<?>> iterator = loadAlignments(fileSelection.getFile(), fileSelection.getFormat());
					int index = 0;
					if (iterator.hasNext()) {
						do {
							//TODO Am besten evtl. Namen aus Datei laden.
							map.put(fileSelection.getFile().getAbsolutePath() + " [" + index + "]", 
									new ComparedAlignment((AlignmentModel<Character>)iterator.next()));  //TODO Use shorter key?
							index++;
						} while (iterator.hasNext());
					}
					else {
						warningMessage.append("\n- ");
						warningMessage.append(fileSelection.getFile());
					}
				}
				
				if (warningMessage.length() > 0) {
					JOptionPane.showMessageDialog(getMainFrame(), "The following files did not contain any alignments:\n" + 
							warningMessage.toString(), "Empty file(s)", JOptionPane.WARNING_MESSAGE);
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
