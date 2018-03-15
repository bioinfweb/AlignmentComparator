/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/AlignmentComparator>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.event.DocumentEvent;
import info.bioinfweb.alignmentcomparator.document.event.DocumentListener;
import info.bioinfweb.alignmentcomparator.gui.comment.CommentArea;
import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionSynchronizer;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.AminoAcidTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.LabelDataArea;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.KeyStroke;



/**
 * The GUI component displaying the super alignment and the associated comments. 
 * 
 * @author Ben St&ouml;ver
 * @since 1.1.0
 */
public class AlignmentComparisonComponent extends MultipleAlignmentsContainer implements DocumentListener {
	public static final Color SUPER_GAP_COLOR = Color.LIGHT_GRAY;	
	public static final Color HIGHLIGHT_COLOR = Color.RED;	
	
	public static final int FIRST_ALIGNMENT_INDEX = 1;
	public static final int BOTTOM_AREAS_COUNT = 1;

	private final SelectionListener<GenericEventObject<SelectionModel>> FIRST_SELECTION_LISTENER = new SelectionListener<GenericEventObject<SelectionModel>>() {
				@Override
				public void selectionChanged(GenericEventObject<SelectionModel> event) {  //TODO Check if this implementation needs to be changed due to update and object recreation strategies.
					Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();	
				}
			};
	private final PosHighlightOverlay OVERLAY = new PosHighlightOverlay();
	
	
	private MainFrame owner;
	private CommentArea commentArea;
	private VerticalScrollingSynchronizer verticalScrollingSynchronizer = new VerticalScrollingSynchronizer(this);
	private SelectionSynchronizer selectionSynchronizer = new SelectionSynchronizer();
	private AlignmentComparisonSelection selection = new AlignmentComparisonSelection(this);

	
	public AlignmentComparisonComponent(MainFrame owner) {
		super();
		this.owner = owner;
		init();
	}
	
	
	public MainFrame getOwner() {
		return owner;
	}


	public CommentArea getCommentArea() {
		return commentArea;
	}


	public Document getDocument() {
		return getOwner().getDocument();
	}


	private AlignmentArea createIndexArea() {
		AlignmentArea result = new AlignmentArea(this);
		result.getDataAreas().getTopAreas().add(new SequenceIndexArea(result.getContentArea()));
		result.setAllowVerticalScrolling(false);
		return result;
	}
	
	
	private SingleColorTokenPainter createTokenPainter() {
		//TODO Can't this method be replaced by functionality available in LibrAlign?
		
		SingleColorTokenPainter painter;
		switch (getDocument().getTokenType()) {
			case NUCLEOTIDE:
				painter = new NucleotideTokenPainter();
				break;
			case AMINO_ACID:
				painter = new AminoAcidTokenPainter();
				break;
			default:
				throw new InternalError("The unexpected document token type " + getDocument().getTokenType() + 
						" was encountered. Contact the developers if you see this error.");
		}
		painter.getBackgroundColorMap().put(Character.toString(SuperalignedModelDecorator.SUPER_ALIGNMENT_GAP), SUPER_GAP_COLOR);
		return painter;
	}
	
	
	private AlignmentArea createComparisonPartArea(String alignmentName) {
		AlignmentArea result = new AlignmentArea(this);
		result.setAlignmentModel(getDocument().getAlignments().get(alignmentName).getSuperaligned(), false);
		
		result.getDataAreas().getTopAreas().add(new LabelDataArea(result.getContentArea(), result, alignmentName, false, true));
		result.getDataAreas().getBottomAreas().add(new AveragePositionArea(result.getContentArea(), result, alignmentName));  //TODO Use optionally, if average indices are present or use generic data area depending on the used method in the future.
		
		result.getPaintSettings().getTokenPainterList().set(0, createTokenPainter());
		
		result.getSequenceOrder().setAlphabeticalSequenceOrder(true);  //TODO Use order of first alignment instead (or allow the user to choose between this and the alphabetical order)
		result.getSelection().setType(SelectionType.COLUMN_ONLY);
		selectionSynchronizer.add(result.getSelection());  //TODO Do instances need to be removed, when a new comparison is loaded?
		//TODO Add consensus sequence area on bottom
		result.setAllowVerticalScrolling(true);
		result.getScrollListeners().add(verticalScrollingSynchronizer);
		
		result.getOverlays().add(OVERLAY);  //TODO Is the triggerd repaint here a problem?
		
		result.getContentArea().getActionMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), 
				getOwner().getActionManagement().get("edit.insertSupergap"));
		result.getContentArea().getActionMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), 
				getOwner().getActionManagement().get("edit.removeSupergapBackwards"));
		result.getContentArea().getActionMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), 
				getOwner().getActionManagement().get("edit.removeSupergapForward"));
		
		return result;
	}
	
	
	private AlignmentArea createCommentAlignmentArea() {
		AlignmentArea result = new AlignmentArea(this);
		commentArea = new CommentArea(result.getContentArea());
		result.getDataAreas().getBottomAreas().add(commentArea);
		result.setAllowVerticalScrolling(false);
		return result;
	}

	
	private void addSelectionListener() {  // Due to selection synchronization this listener only needs to be registered with the one alignment area.
		if (getDocument().getAlignments().size() > 0) {
			getFirstAlignmentArea().getSelection().addSelectionListener(FIRST_SELECTION_LISTENER);
		}
	}
	
	
	private void init() {
		getAlignmentAreas().add(createIndexArea());
		Iterator<String> iterator = getDocument().getAlignments().keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (getDocument().getAlignments().get(name).hasSuperaligned()) {
				getAlignmentAreas().add(createComparisonPartArea(name));
			}
		}
		getAlignmentAreas().add(createCommentAlignmentArea());
		addSelectionListener();
	}
	
	
	/**
	 * Tests if the currently displayed alignment areas match the data model.
	 * 
	 * @return {@code true} if an update is needed to reflect the data model, {@code false} if not.
	 */
	private boolean updateNeeded() {
		int pos = FIRST_ALIGNMENT_INDEX;
		Iterator<String> iterator = getDocument().getAlignments().keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if ((pos >= getAlignmentAreas().size() - BOTTOM_AREAS_COUNT) || (!getAlignmentAreas().get(pos).getAlignmentModel().equals(
					getDocument().getAlignments().get(name).getSuperaligned()))) {
				
				return true;
			}
			pos++;
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private void updateAlignments() {
		// Backup current GUI components:
		List<AlignmentArea> topAreas = new ArrayList<AlignmentArea>(getAlignmentAreas().size());
		for (int i = 0; i < FIRST_ALIGNMENT_INDEX; i++) {
			topAreas.add(getAlignmentAreas().get(i));
		}
		
		Map<AlignmentModel<Character>, AlignmentArea> previousComparisonParts = 
				new HashMap<AlignmentModel<Character>, AlignmentArea>();
		for (AlignmentArea area : getAlignmentAreas()) {
			if (area.hasAlignmentModel()) {
				previousComparisonParts.put((AlignmentModel<Character>)area.getAlignmentModel(), area);
			}
		}
		
		List<AlignmentArea> bottomAreas = new ArrayList<AlignmentArea>(getAlignmentAreas().size());
		for (int i = getAlignmentAreas().size() - BOTTOM_AREAS_COUNT; i < getAlignmentAreas().size(); i++) {
			bottomAreas.add(getAlignmentAreas().get(i));
		}
		
		// Remove current GUI elements:
		AlignmentArea firstArea = getFirstAlignmentArea();
		if (firstArea != null) {
			firstArea.getSelection().removeSelectionListener(FIRST_SELECTION_LISTENER);
		}
		getAlignmentAreas().clear();
		selectionSynchronizer.clear();
		// Vertical scrolling synchronizer does not have to be removed from removed AlignmentAreas, since these should not trigger events anymore.
		
		// Repopulate area list:
		getAlignmentAreas().addAll(topAreas);
		
		Iterator<String> iterator = getDocument().getAlignments().keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			AlignmentArea area = previousComparisonParts.get(getDocument().getAlignments().get(name).getSuperaligned());
			if (area != null) {
				getAlignmentAreas().add(area);
				selectionSynchronizer.add(area.getSelection());
			}
			else if (getDocument().getAlignments().get(name).hasSuperaligned()) {
				getAlignmentAreas().add(createComparisonPartArea(name));  // selectionSynchronizer.add() is called inside createComparisonPartArea().
			}			
		}
		addSelectionListener();
	
		getAlignmentAreas().addAll(bottomAreas);
		redistributeHeight();
	}
	
	
	public AlignmentComparisonSelection getSelection() {
		return selection;
	}
	
	
	/**
	 * Returns the top most alignment area that actually contains alignment data.
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
	
	
	/**
	 * Returns a list with all alignment areas that display one compared alignment. In contrast to
	 * {@link #getAlignmentAreas()} this list does not contain additional areas that display data
	 * areas like the column index area.
	 * 
	 * @return a list of alignment areas that may be empty
	 */
	public List<AlignmentArea> getComparisonAlignmentAreas() {
		return getAlignmentAreas().subList(FIRST_ALIGNMENT_INDEX, getAlignmentAreas().size() - BOTTOM_AREAS_COUNT);
	}
	
	
	@Override
	public void changeHappened(DocumentEvent e) {
		if (updateNeeded()) {
			updateAlignments();
		}
		((JComponent)getToolkitComponent()).revalidate();  //TODO Move to LibrAlign (Still necessary?)
		assignSizeToAll();  // Necessary e.g. to resize and repaint comment area.
		redistributeHeight();
		Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();	
	}


	@Override
	public void namesChanged(DocumentEvent e) {}  //TODO Implement when this event can happen.


	public boolean isSelectionSynchronized() {
		return selectionSynchronizer.isEnabled();
	}


	public void setSelectionSynchronized(boolean enabled) {
		selectionSynchronizer.setEnabled(enabled);
	}


	public void toggleSelectionSynchronized() {
		selectionSynchronizer.setEnabled(!isSelectionSynchronized());
	}
}
