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


import info.bioinfweb.alignmentcomparator.document.io.results.ResultsReader;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;



public class OpenResultsAction extends DocumentAction {
  private JFileChooser fileChooser = null;
  private ResultsReader reader = new ResultsReader();

  
	public OpenResultsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Open results"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.SHORT_DESCRIPTION, "Open results");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('O', 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("Open");
	}


	private JFileChooser getFileChooser() {
  	if (fileChooser == null) {
  		fileChooser = new JFileChooser();
  		fileChooser.setDialogTitle("Open comparison results");
  		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml"));
  	}
  	return fileChooser;
  }

  
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getFileChooser().showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {  //TODO hier nicht MainFrame verwenden, falls Aktion auch vor Anzeige des MainFrames verfügbar sein soll
			try {
				reader.read(new BufferedInputStream(new FileInputStream(getFileChooser().getSelectedFile())), 
						getMainFrame().getDocument());
				//TODO Inform Model
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(getMainFrame(), "The error \"" + ex.getMessage() + 
						"\" occured, while trying to read from the file \"" + getFileChooser().getSelectedFile() + "\".", 
						"Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}


	@Override
	public void setEnabled() {}  // nothing to do (opening new files is always possible)
}
