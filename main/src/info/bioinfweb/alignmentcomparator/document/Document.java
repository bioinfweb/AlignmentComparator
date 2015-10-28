/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
 * <http://bioinfweb.info/Software>
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
package info.bioinfweb.alignmentcomparator.document;


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.comments.CommentList;
import info.bioinfweb.alignmentcomparator.document.comments.SequencePositionAdapter;
import info.bioinfweb.alignmentcomparator.document.event.DocumentEvent;
import info.bioinfweb.alignmentcomparator.document.event.DocumentListener;
import info.bioinfweb.alignmentcomparator.document.io.results.ResultsFileFilter;
import info.bioinfweb.alignmentcomparator.document.undo.DocumentEdit;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositionerFactory;
import info.bioinfweb.commons.changemonitor.ChangeMonitorable;
import info.bioinfweb.commons.io.Savable;
import info.bioinfweb.commons.swing.AccessibleUndoManager;
import info.bioinfweb.commons.swing.SwingSavable;
import info.bioinfweb.commons.swing.SwingSaver;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.collections4.map.ListOrderedMap;



public class Document extends SwingSaver implements ChangeMonitorable, Savable, SwingSavable {
	public static final int GAP_INDEX = -1;
	public static final String DEFAULT_DOCUMENT_NAME = "New";
	public static final double ARRAY_LIST_SIZE_FACTOR = 1.3;
	
	
	private ListOrderedMap<String, ComparedAlignment> alignments = 
			ListOrderedMap.listOrderedMap(new TreeMap<String, ComparedAlignment>());
	private CommentList comments = new CommentList(new SequencePositionAdapter());
	private AccessibleUndoManager undoManager = new AccessibleUndoManager();
	//private ResultsWriter writer = new ResultsWriter();  //TODO Move somewhere else?
  private List<DocumentListener> views = new LinkedList<DocumentListener>();
  
	
	public Document() {
		super(DEFAULT_DOCUMENT_NAME);
		getFileChooser().removeChoosableFileFilter(getFileChooser().getAcceptAllFileFilter());
		getFileChooser().addChoosableFileFilter(ResultsFileFilter.getInstance());
		getFileChooser().addChoosableFileFilter(getFileChooser().getAcceptAllFileFilter());
  	//CurrentDirectoryModel.getInstance().addFileChooser(getFileChooser());  //TODO TG Klasse in Utils auslagern
  	setDefaultExtension(ResultsFileFilter.EXTENSION);
		clear();
	}
	
	
	public ListOrderedMap<String, ComparedAlignment> getAlignments() {
		return alignments;
	}
	
	
//	public Iterator<Integer> sequenceIDIterator() {
//		if (isEmpty()) {
//			return Collections.emptyIterator();
//		}
//		else {
//			return originalAlignmentProviders.get(getAlignmentName(0)).sequenceIDIterator();  // Important that always the same provider is used, because the order might differ between the different providers.
//		}
//	}
//	
//	
//	public String sequenceNameByID(int sequenceID) {
//		if (isEmpty()) {
//			return null;
//		}
//		else {
//			return originalAlignmentProviders.get(getAlignmentName(0)).sequenceNameByID(sequenceID);
//		}
//	}
//	
//	
//	public int sequenceIDByName(String sequenceName) {
//		if (isEmpty()) {
//			return -1;
//		}
//		else {
//			return originalAlignmentProviders.get(getAlignmentName(0)).sequenceIDByName(sequenceName);
//		}
//	}
//	
//	
//	private void addSuperAlignment(String alignmentName, int alignmentIndex, Map<String, DNASequence> alignment, 
//			TokenSet<T> tokenSet) {  //TODO Remove alignmentIndex and use alignmentName in new SuperAlignmentSequenceView().
//		
//		BioJavaSequenceDataProvider provider = new BioJavaSequenceDataProvider(tokenSet, Collections.EMPTY_MAP);
//		for (String sequenceName : alignment.keySet()) {
//			provider.addSequence(sequenceName, new SuperAlignmentSequenceView(this, alignmentIndex, alignment.get(sequenceName)));
//		}
//		superAlignmentProviders.put(alignmentName, provider);
//	}
	
	
//	private void setData(String firstName, Map<String, DNASequence> firstMap, 
//			String secondName, Map<String, DNASequence> secondMap, TokenSet<T> tokenSet) {
//		
//		// Sort maps to ensure identical sequence IDs are assigned to both alignments (BioJava readers provide HashMaps here.):
//		TreeMap<String, DNASequence> firstAlignment = new TreeMap<String, DNASequence>();
//		firstAlignment.putAll(firstMap);
//		TreeMap<String, DNASequence> secondAlignment = new TreeMap<String, DNASequence>();
//		secondAlignment.putAll(secondMap);
//		
//		clear();
//		alignmentNames.add(firstName);
//		originalAlignmentProviders.put(firstName, new BioJavaSequenceDataProvider(tokenSet, firstAlignment)); //new AlignmentComparatorDataProvider<T>(tokenSet));
//		alignmentNames.add(secondName);
//		originalAlignmentProviders.put(secondName, new BioJavaSequenceDataProvider(tokenSet, secondAlignment));
//
//		addSuperAlignment(firstName, 0, firstAlignment, tokenSet);
//		addSuperAlignment(secondName, 1, secondAlignment, tokenSet);
//		
//		unalignedIndices = new ArrayList[2];
//		for (int i = 0; i < 2; i++) {
//			unalignedIndices[i] = new ArrayList<Integer>();
//		}
//	}
	
	
//	public void setUnalignedData(String firstName, Map<String, DNASequence> firstAlignment, 
//			String secondName, Map<String, DNASequence> secondAlignment, TokenSet<T> tokenSet, 
//			SuperAlignmentAlgorithm algorithm) throws Exception {
//		
//		setData(firstName, firstAlignment, secondName, secondAlignment, tokenSet);
//		algorithm.performAlignment(this);
//		registerChange();
//		fireNamesChanged();
//	}
//	
//	
//	public void setAlignedData(String firstName, Map<String, DNASequence> firstAlignment, 
//			String secondName, Map<String, DNASequence> secondAlignment, TokenSet<T> tokenSet, 
//			List<Integer>[] unalignedIndices) {
//		
//		setData(firstName, firstAlignment, secondName, secondAlignment, tokenSet);
//		for (int i = 0; i < unalignedIndices.length; i++) {
//			setUnalignedIndexList(i, unalignedIndices[i]);
//		}
//
//		performChange();  // Hier nicht registerChange(), da Dokument am Anfang nicht als ungespeichert angezeigt werden soll.
//		fireNamesChanged();
//	}

	
	@Override
	protected void saveDataToFile(File file) {
		try {
			//writer.write(new BufferedOutputStream(new FileOutputStream(file)), this);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The error \"" + e.getMessage() + "\" occured when writing to the file \"" + file.getAbsolutePath() + "\"", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void setDefaultName(String path) {
		super.setDefaultName(path);
		//Main.getInstance().getMainFrame().updateTitle();
	}


	@Override
	public void setFile(File file) {
		super.setFile(file);
		Main.getInstance().getMainFrame().updateTitle();
	}


	@Override
	public void reset() {
		super.reset();
		Main.getInstance().getMainFrame().updateTitle();
	}


	public void clear() {
//		originalAlignmentProviders.clear();
//		superAlignmentProviders.clear();
//		unalignedIndices = new ArrayList[0];
		alignments.clear();
		comments.clear();
	}
	
	
	public boolean isEmpty() {
		return alignments.isEmpty();
	}
	
	
//	public int getUnalignedIndex(int alignmentIndex, int pos) {
//		return unalignedIndices[alignmentIndex].get(pos);
//	}
	
	
//	/**
//	 * Returns <code>true</code>, if the specified alignment contains a supergap at the specified position.
//	 * 
//	 * @param alignmentIndex - the index of the alignment
//	 * @param pos - the position to be checked (BioJava indices start with 1)
//	 */
//	public boolean containsSuperGap(int alignmentIndex, int pos) {
//		return unalignedIndices[alignmentIndex].get(pos - 1).equals(SuperAlignmentSequenceView.GAP_INDEX);
//	}
	
	
//	/**
//	 * Returns {@code true} if the specified interval contains at least one supergap.
//	 * 
//	 * @param alignmentIndex - the index of the alignment
//	 * @param firstPos - the first position of the interval that is checked (BioJava indices start with 1)
//	 * @param lastPos - the last position of the interval that is checked (BioJava indices start with 1)
//	 * 
//	 * @see #isFilledWithSuperGaps(int, int, int)
//	 */
//	public boolean containsSuperGap(int alignmentIndex, int firstPos, int lastPos) {
//		for (int i = firstPos; i <= lastPos; i++) {
//			if (containsSuperGap(alignmentIndex, i)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	
//	/**
//	 * Returns <code>true</code> if the specified interval contains a supergap a every position.
//	 * 
//	 * @param alignmentIndex - the index of the alignment
//	 * @param firstPos - the first position of the interval that is checked (BioJava indices start with 1)
//	 * @param lastPos - the last position of the interval that is checked (BioJava indices start with 1)
//	 * 
//	 * @see #containsSuperGap(int, int, int)
//	 */
//	public boolean isFilledWithSuperGaps(int alignmentIndex, int firstPos, int lastPos) {
//		for (int i = firstPos; i <= lastPos; i++) {
//			if (!containsSuperGap(alignmentIndex, i)) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	
//	/**
//	 * Inserts a supergap at the specified position.
//	 * 
//	 * @param alignmentIndex - the index of the alignment
//	 * @param pos - the position where the supergap should be inserted (BioJava indices start with 1)
//	 */
//	public void insertSuperGap(int alignmentIndex, int pos) {
//		unalignedIndices[alignmentIndex].add(pos - 1, SuperAlignmentSequenceView.GAP_INDEX);
//		if (alignmentIndex == 0) {
//			unalignedIndices[1].add(SuperAlignmentSequenceView.GAP_INDEX);
//		}
//		else {
//			unalignedIndices[0].add(SuperAlignmentSequenceView.GAP_INDEX);
//		}
//		removeTrailingGaps();  //TODO Implementing this method for a whole interval instead of a single position would be more efficient because removeTrailingGaps() would have to be called only once. 
//	}
//	
//	
//	/**
//	 * Removes a supergap from the specified position.
//	 * 
//	 * @param alignmentIndex - the index of the alignment
//	 * @param pos - the position where the supergap should be removed (BioJava indices start with 1)
//	 * @throws IllegalArgumentException if there is no supergap present at the specified position
//	 */
//	public void removeSuperGap(int alignmentIndex, int pos) {
//		if (containsSuperGap(alignmentIndex, pos)) {
//			unalignedIndices[alignmentIndex].remove(pos - 1);
//			unalignedIndices[alignmentIndex].add(SuperAlignmentSequenceView.GAP_INDEX);
//			removeTrailingGaps();  //TODO Implementing this method for a whole interval instead of a single position would be more efficient because removeTrailingGaps() would have to be called only once.
//		}
//		else {
//			throw new IllegalArgumentException("There is no gap at position " + pos + " in alignment " + alignmentIndex + ".");
//		}
//	}
//	
//	
//	/**
//	 * Removes trailing super alignment gaps present in both alignments.
//	 */
//	public void removeTrailingGaps() {
//		int lastIndex = unalignedIndices[0].size() - 1;
//		while ((lastIndex >= 0) && containsSuperGap(0, lastIndex + 1) &&	containsSuperGap(1, lastIndex + 1)) {
//			unalignedIndices[0].remove(lastIndex);
//			unalignedIndices[1].remove(lastIndex);
//			lastIndex--;
//		}
//		//TODO K�nnen Kommentare in diesem Bereich liegen, die behandelt werden m�ssen?
//	}
//	
//	
//	public void setUnalignedIndexList(int alignmentIndex, List<Integer> list) {
//		if (list instanceof ArrayList<?>) {
//			unalignedIndices[alignmentIndex] = (ArrayList<Integer>)list;
//		}
//		else {
//			unalignedIndices[alignmentIndex] = new ArrayList<Integer>((int)(list.size() * ARRAY_LIST_SIZE_FACTOR));
//			unalignedIndices[alignmentIndex].addAll(list);
//		}
//	}
	
	
//	public void setUnalignedSequences(int alignmentIndex, List<DNASequence> list) {
//		if (list instanceof ArrayList<?>) {
//			unalignedSequences[alignmentIndex] = (ArrayList<DNASequence>)list;
//		}
//		else {
//			unalignedSequences[alignmentIndex] = new ArrayList<DNASequence>((int)(list.size() * ARRAY_LIST_SIZE_FACTOR));
//			unalignedSequences[alignmentIndex].addAll(list);
//		}
//	}
	
	
//	 /**
//		* Returns the number of sequences present in each alignment.
//		*/
//	public int getSequenceCount() {
//		if (isEmpty()) {
//			return 0;
//		}
//		else {
//			return originalAlignmentProviders.values().iterator().next().getSequenceCount();
//		}
//	}
//	
//	
//	public int getAlignedLength() {
//		if (isEmpty()) {
//			return 0;
//		}
//		else {
//			return unalignedIndices[0].size();
//		}
//	}


	public CommentList getComments() {
		return comments;
	}
	
	
  public AccessibleUndoManager getUndoManager() {
		return undoManager;
	}


	public void executeEdit(DocumentEdit edit) {
    if (!getUndoManager().addEdit(edit)) {  // Muss vor Ausf�hrung erfolgen da sonst Undo-Schalter ggf. nicht aktiviert werden.
      throw new RuntimeException("The edit could not be executed.");
    }
    edit.redo();  // for real this time
  }


	public boolean addDocumentListener(DocumentListener listener) {
		return views.add(listener);
	}
	
	
	public boolean removeDocumentListener(DocumentListener listener) {
		return views.remove(listener);
	}
	
	
  /** Alerts all registered views to display made changes. */
  private void fireChangeHappened() {
  	DocumentEvent e = new DocumentEvent(this);
	  Iterator<DocumentListener> iterator = views.iterator();
	  while (iterator.hasNext()) {
	  	iterator.next().changeHappened(e);
	  }
  }

  
  /** Alerts all registered views to display changed sequences names. */
  private void fireNamesChanged() {
  	DocumentEvent e = new DocumentEvent(this);
	  Iterator<DocumentListener> iterator = views.iterator();
	  while (iterator.hasNext()) {
	  	iterator.next().namesChanged(e);
	  }
  }

  
  /** Alerts all comment positioners to reposition the comments because of made changes. */
  private void alertPositioners() {
  	Iterator<CommentPositioner> iterator = CommentPositionerFactory.getInstance().getAllPositioners().iterator();
  	while (iterator.hasNext()) {
  		iterator.next().position(this);
  	}
  }

  
  private void performChange() {
		alertPositioners();  // Positioners must be alerted first
		fireChangeHappened();
  }
  

  @Override
	public void registerChange() {
		super.registerChange();
		performChange();
		Main.getInstance().getMainFrame().updateTitle();
	}
}
