package info.bioinfweb.alignmentcomparator.gui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.event.DocumentEvent;
import info.bioinfweb.alignmentcomparator.document.event.DocumentListener;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentArea;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionChangeEvent;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionSynchronizer;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.libralign.sequenceprovider.SequenceAccessDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * The GUI component displaying the super alignment and the associated comments. 
 * 
 * @author Ben St&ouml;ver
 * @since 1.1.0
 */
public class AlignmentComparisonComponent extends MultipleAlignmentsContainer implements DocumentListener {
	public static final int FIRST_ALIGNMENT_INDEX = 1;
	public static final int BOTTOM_AREAS_COUNT = 1;  // Currently only comment area.

	private final SelectionListener SELECTION_LISTENER = new SelectionListener() {
				@Override
				public void selectionChanged(SelectionChangeEvent event) {  //TODO Check if this implementation needs to changed due to update and object recreation strategies.
					Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();	
				}
			}; 
	
	
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
		result.getDataAreas().getTopAreas().add(new SequenceIndexArea(result.getContentArea()));
		result.setAllowVerticalScrolling(false);
		return result;
	}
	
	
	private AlignmentArea createCommentAlignmentArea() {
		AlignmentArea result = new AlignmentArea(this);
		commentArea = new CommentArea(result.getContentArea());
		result.getDataAreas().getBottomAreas().add(commentArea);
		result.setAllowVerticalScrolling(false);
		return result;
	}
	
	
	private <T> AlignmentArea createComparisonPartArea(SequenceDataProvider<T> privoder) {
		AlignmentArea result = new AlignmentArea(this);
		result.setSequenceProvider(privoder, false);
		//TODO Link order objects
		result.getSelection().setType(SelectionType.COLUMN_ONLY);
		selectionSynchronizer.add(result.getSelection());  //TODO Do instances need to be removed, when a new comparison is loaded?
		//TODO Add consensus sequence area on bottom
		//TODO Link vertical scrolling
		result.setAllowVerticalScrolling(true);
		return result;
	}
	
	
	private void addSelectionListener() {
		if (document.getAlignmentCount() > 0) {
			getFirstAlignmentArea().getSelection().addSelectionListener(SELECTION_LISTENER);
		}
	}
	
	
	private void init() {
		getAlignmentAreas().add(createIndexArea());
		for (int i = 0; i < document.getAlignmentCount(); i++) {
			String name = document.getAlignmentName(i);
			getAlignmentAreas().add(createComparisonPartArea(document.getSuperAlignmentProvider(name)));
		}
		addSelectionListener();
		getAlignmentAreas().add(createCommentAlignmentArea());
	}
	
	
	private void updateAlignments() {
		// Backup current GUI components:
		List<AlignmentArea> topAreas = new ArrayList<AlignmentArea>(getAlignmentAreas().size());
		for (int i = 0; i < FIRST_ALIGNMENT_INDEX; i++) {
			topAreas.add(getAlignmentAreas().get(i));
		}
		
		Map<SequenceDataProvider, AlignmentArea> previousComparisonParts = new HashMap<SequenceDataProvider, AlignmentArea>();
		for (AlignmentArea area : getAlignmentAreas()) {
			if (area.hasSequenceProvider()) {
				previousComparisonParts.put(area.getSequenceProvider(), area);
			}
		}
		
		List<AlignmentArea> bottomAreas = new ArrayList<AlignmentArea>(getAlignmentAreas().size());
		for (int i = getAlignmentAreas().size() - BOTTOM_AREAS_COUNT; i < getAlignmentAreas().size(); i++) {
			bottomAreas.add(getAlignmentAreas().get(i));
		}
		
		// Remove current GUI elements:
		getFirstAlignmentArea().getSelection().removeSelectionListener(SELECTION_LISTENER);
		getAlignmentAreas().clear();
		selectionSynchronizer.clear();
		
		// Repopulate area list:
		getAlignmentAreas().addAll(topAreas);
		
		for (int i = 0; i < document.getAlignmentCount(); i++) {
			String name = document.getAlignmentName(i);
			SequenceAccessDataProvider provider = document.getSuperAlignmentProvider(name);
			AlignmentArea area = previousComparisonParts.get(provider);
			if (area != null) {
				getAlignmentAreas().add(area);
				selectionSynchronizer.add(area.getSelection());
			}
			else {
				getAlignmentAreas().add(createComparisonPartArea(provider));  // selectionSynchronizer.add() is called inside createComparisonPartArea().
			}			
		}
		addSelectionListener();
	
		getAlignmentAreas().addAll(bottomAreas);
	}
	
	
	public AlignmentComparisonSelection getSelection() {
		return selection;
	}
	
	
	/**
	 * Returns the alignment area of the top most alignment area that contains an alignment.
	 * 
	 * @return the first valid alignment area or {@code null} if no alignment is contained in this instance
	 */
	public AlignmentArea getFirstAlignmentArea() {
		if (getAlignmentAreas().size() <= FIRST_ALIGNMENT_INDEX) {
			return null;
		}
		else {
			return getAlignmentAreas().get(FIRST_ALIGNMENT_INDEX);
		}
	}
	
	
	public CommentArea getCommentArea() {
		return commentArea;
	}


	@Override
	public void changeHappened(DocumentEvent e) {
		updateAlignments();
		((JComponent)getToolkitComponent()).revalidate();  //TODO Move to LibrAlign
		assignSize();  //TODO Needed?
		Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();	
	}


	@Override
	public void namesChanged(DocumentEvent e) {}  //TODO Implement when this event can happen.
}
