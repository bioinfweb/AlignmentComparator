package info.bioinfweb.alignmentcomparator.gui.actions;


import info.webinsel.util.swing.ActionHashMap;



public class ActionManagement extends ActionHashMap {
	public ActionManagement() {
		super();
		fillMap();
	}


	/**
	 * All <code>Action</code> objects used in XMLFormatCreator are added to the <code>HashMap</code>
	 * in this method. New actions should be added here as well.
	 */
	protected void fillMap() {
		//put("file.exit", new ExitAction());
	}
	

	@Override
	public void refreshActionStatus() {
	}
}