package info.bioinfweb.alignmentcomparator.gui;


import java.awt.GridBagLayout;
import javax.swing.JPanel;



public class AlignmentComparisonPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	
	/**
	 * This is the default constructor
	 */
	public AlignmentComparisonPanel() {
		super();
		initialize();
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		//this.setLayout(new GridBagLayout());
	}
}
