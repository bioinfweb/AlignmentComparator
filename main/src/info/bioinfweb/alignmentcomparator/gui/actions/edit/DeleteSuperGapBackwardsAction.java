package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import java.awt.event.ActionEvent;

import javax.swing.Action;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;



public class DeleteSuperGapBackwardsAction extends SupergapAction {
	public DeleteSuperGapBackwardsAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Delete supergap"); 
		putValue(Action.SHORT_DESCRIPTION, "Delete supergap"); 
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
