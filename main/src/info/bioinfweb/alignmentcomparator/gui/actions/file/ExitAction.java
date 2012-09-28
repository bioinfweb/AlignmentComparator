package info.bioinfweb.alignmentcomparator.gui.actions.file;


import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;



public class ExitAction extends DocumentAction {
	public ExitAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Exit"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		getMainFrame().close();
	}


	@Override
	public void setEnabled() {}  // nothing to do
}
