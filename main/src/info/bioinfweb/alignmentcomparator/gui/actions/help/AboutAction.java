package info.bioinfweb.alignmentcomparator.gui.actions.help;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.gui.dialogs.AboutDialog;
import info.bioinfweb.commons.swing.ExtendedAbstractAction;



/**
 * Action object that displays the about dialog of AlignmentComarator.
 * 
 * @author Ben St&ouml;ver
 */
public class AboutAction extends ExtendedAbstractAction {
	private AboutDialog dialog = null;
	
	
	public AboutAction() {
		super();
		putValue(Action.NAME, "About..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
	  //loadSymbols("AlignmentComarator");
  }


	private AboutDialog getDialog() {
		if (dialog == null) {
			dialog = new AboutDialog(Main.getInstance().getMainFrame());
		}
		return dialog;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		getDialog().setVisible(true);
	}
}
