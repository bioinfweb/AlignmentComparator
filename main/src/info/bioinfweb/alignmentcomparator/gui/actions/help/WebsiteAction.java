package info.bioinfweb.alignmentcomparator.gui.actions.help;


import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.commons.swing.ExtendedAbstractAction;



/**
 * Action object that opens the AlignmentComparator website.
 * 
 * @author Ben St&ouml;ver
 */
public class WebsiteAction extends ExtendedAbstractAction {
	public WebsiteAction() {
		super();
		
		putValue(Action.NAME, "AlignmentComparator website"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
	  //loadSymbols("AlignmentComparator");
	}

	
	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URI(Main.APPLICATION_URL));
		}
		catch (URISyntaxException | IOException ex) {
			JOptionPane.showMessageDialog(Main.getInstance().getMainFrame(), 
					"An error occurred when trying open the selected link. (" + ex.getMessage() + ")", 
					"Navigation failed,", JOptionPane.ERROR_MESSAGE);
		}
	}
}
