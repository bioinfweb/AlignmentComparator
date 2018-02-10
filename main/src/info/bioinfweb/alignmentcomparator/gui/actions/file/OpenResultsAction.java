/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/AlignmentComparator>
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


import info.bioinfweb.alignmentcomparator.document.io.ComparisonDocumentReader;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;



public class OpenResultsAction extends DocumentAction {
  private JFileChooser fileChooser = null;
  
  
	public OpenResultsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Open results"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.SHORT_DESCRIPTION, "Open results");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("Open");
	}


	private JFileChooser getFileChooser() {
  	if (fileChooser == null) {
  		fileChooser = new JFileChooser();
  		fileChooser.setDialogTitle("Open comparison results");
  		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
  		//fileChooser.addChoosableFileFilter(ResultsFileFilter.getInstance());  //TODO Add NeXML filter here?
  		fileChooser.addChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
  		//TODO zu DirModel hinzufügen
  	}
  	return fileChooser;
  }
	
  
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getFileChooser().showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			try {
				ComparisonDocumentReader reader = new ComparisonDocumentReader();
				reader.read(getFileChooser().getSelectedFile(), getMainFrame().getDocument());
				getMainFrame().getDocument().setFile(getFileChooser().getSelectedFile());
				
				getDocument().registerChange();
				getDocument().reset();  // Set change flag to false again.
			}
			catch (Exception ex) {
				String position = "";
				if (ex instanceof JPhyloIOReaderException) {
					position = " (line " + ((JPhyloIOReaderException)ex).getLineNumber() + ", column " + 
							((JPhyloIOReaderException)ex).getColumnNumber() + ")";
				}
				
				JOptionPane.showMessageDialog(getMainFrame(), "The error \"" + ex.getMessage() + 
						"\" occured, while trying to read from the file \"" + getFileChooser().getSelectedFile() + "\"" + position + ".", 
						"Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}


	@Override
	public void setEnabled() {}  // nothing to do (opening new files is always possible)
}
