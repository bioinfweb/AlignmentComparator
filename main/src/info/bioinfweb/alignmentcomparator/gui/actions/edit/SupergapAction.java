package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;



public abstract class SupergapAction extends DocumentAction {
	public SupergapAction(MainFrame mainFrame) {
		super(mainFrame);
	}

	
	@Override
	public void setEnabled() {
		setEnabled(!getDocument().isEmpty());  //TODO adjust?
	}
}
