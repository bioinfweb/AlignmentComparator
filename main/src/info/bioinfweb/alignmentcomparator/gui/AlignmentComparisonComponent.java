package info.bioinfweb.alignmentcomparator.gui;


import javax.swing.JComponent;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.event.DocumentEvent;
import info.bioinfweb.alignmentcomparator.document.event.DocumentListener;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentArea;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionSynchronizer;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * The GUI component displaying the super alignment and the associated comments. 
 * 
 * @author Ben St&ouml;ver
 * @since 1.1.0
 */
public class AlignmentComparisonComponent extends MultipleAlignmentsContainer implements DocumentListener {
	public static final int FIRST_ALIGNMENT_INDEX = 1;
	
	
	private Document<?> document = null;
	private SelectionSynchronizer selectionSynchronizer = new SelectionSynchronizer();
	private AlignmentComparisonSelection selection = new AlignmentComparisonSelection(this);
	private CommentArea commentArea;

	
	public AlignmentComparisonComponent(Document<?> document) {
		super();
		this.document = document;
		init();
	}
	
	
	public Document<?> getDocument() {
		return document;
	}


	private AlignmentArea createIndexArea() {
		AlignmentArea result = new AlignmentArea(this);
		result.getContentArea().getDataAreas().getTopAreas().add(new SequenceIndexArea(result.getContentArea()));
		result.setAllowVerticalScrolling(false);
		return result;
	}
	
	
	private AlignmentArea createCommentAlignmentArea() {
		AlignmentArea result = new AlignmentArea(this);
		commentArea = new CommentArea(result.getContentArea());
		result.getContentArea().getDataAreas().getBottomAreas().add(commentArea);
		result.setAllowVerticalScrolling(false);
		return result;
	}
	
	
	private <T> AlignmentArea createComparisonPartArea(SequenceDataProvider<T> privoder) {
		AlignmentArea result = new AlignmentArea(this);
		result.getContentArea().setSequenceProvider(privoder, false);
		//TODO Link order objects
		result.getContentArea().getSelection().setType(SelectionType.COLUMN_ONLY);
		selectionSynchronizer.add(result.getContentArea().getSelection());  //TODO Do instances need to be removed, when a new comparison is loaded?
		//TODO Add consensus sequence area on bottom
		//TODO Link vertical scrolling
		result.setAllowVerticalScrolling(true);
		return result;
	}
	
	
	private void init() {
		getAlignmentAreas().add(createIndexArea());
		for (int i = 0; i < document.getAlignmentCount(); i++) {
			String name = document.getAlignmentName(i);
			getAlignmentAreas().add(createComparisonPartArea(document.getSuperAlignmentProvider(name)));
		}
		getAlignmentAreas().add(createCommentAlignmentArea());
	}
	
	
//	private void updateAlignments() {
//		for (int alignmentIndex = 0; alignmentIndex < document.getAlignmentCount(); alignmentIndex++) {
//			if (FIRST_ALIGNMENT_INDEX + alignmentIndex < getAlignmentAreas().size()) {
//				
//			}
//		}
//	}
	
	
	public AlignmentComparisonSelection getSelection() {
		return selection;
	}
	
	
	/**
	 * Returns the alignment content area of the top most alignment area that contains an alignment.
	 * 
	 * @return the first valid alignment content area or {@code null} if no alignment is contained in this instance
	 */
	public AlignmentContentArea getFirstAlignmentArea() {
		if (getAlignmentAreas().size() <= FIRST_ALIGNMENT_INDEX) {
			return null;
		}
		else {
			return getAlignmentAreas().get(FIRST_ALIGNMENT_INDEX).getContentArea();
		}
	}
	
	
	public CommentArea getCommentArea() {
		return commentArea;
	}


	@Override
	public void changeHappened(DocumentEvent e) {
		getAlignmentAreas().clear();
		init();
		//((JComponent)getToolkitComponent()).revalidate();  //TODO Move to LibrAlign
		assignSize();
		repaint();
		Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();	
	}


	@Override
	public void namesChanged(DocumentEvent e) {}  //TODO Implement when this event can happen.
}
