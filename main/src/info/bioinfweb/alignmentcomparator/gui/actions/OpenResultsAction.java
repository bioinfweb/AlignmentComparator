package info.bioinfweb.alignmentcomparator.gui.actions;


import info.bioinfweb.alignmentcomparator.document.io.results.ResultsReader;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;



public class OpenResultsAction extends AlignmentComparatorAction {
  private JFileChooser fileChooser = null;
  private ResultsReader reader = new ResultsReader();

  
	public OpenResultsAction(MainFrame mainFrame) {
		super(mainFrame);
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
				JOptionPane.showMessageDialog(getMainFrame(), "Error", "The error \"" + ex.getMessage() + 
						"\" occured, while trying to read from the file \"" + getFileChooser().getSelectedFile() + "\".", 
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}
