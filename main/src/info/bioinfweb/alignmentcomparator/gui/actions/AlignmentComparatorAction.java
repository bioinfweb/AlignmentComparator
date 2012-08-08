package info.bioinfweb.alignmentcomparator.gui.actions;


import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.webinsel.util.swing.ExtendedAbstractAction;



public abstract class AlignmentComparatorAction extends ExtendedAbstractAction {
  private MainFrame mainFrame = null;

  
	public AlignmentComparatorAction(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
	}


	public MainFrame getMainFrame() {
		return mainFrame;
	}
}
