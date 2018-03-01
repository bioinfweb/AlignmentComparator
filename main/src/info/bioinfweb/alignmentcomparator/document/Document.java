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
package info.bioinfweb.alignmentcomparator.document;


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.comment.CommentList;
import info.bioinfweb.alignmentcomparator.document.comment.SequencePositionAdapter;
import info.bioinfweb.alignmentcomparator.document.event.DocumentEvent;
import info.bioinfweb.alignmentcomparator.document.event.DocumentListener;
import info.bioinfweb.alignmentcomparator.document.io.ComparisonDocumentDataAdapter;
import info.bioinfweb.alignmentcomparator.document.undo.DocumentEdit;
import info.bioinfweb.alignmentcomparator.gui.comment.CommentPositioner;
import info.bioinfweb.alignmentcomparator.gui.comment.CommentPositionerFactory;
import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.changemonitor.ChangeMonitorable;
import info.bioinfweb.commons.io.Savable;
import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.commons.swing.AccessibleUndoManager;
import info.bioinfweb.commons.swing.SwingSavable;
import info.bioinfweb.commons.swing.SwingSaver;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventWriter;

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
	
	
	private CharacterStateSetType tokenType = CharacterStateSetType.DISCRETE;
	private ListOrderedMap<String, ComparedAlignment> alignments = 
			ListOrderedMap.listOrderedMap(new TreeMap<String, ComparedAlignment>());
	private CommentList comments = new CommentList(new SequencePositionAdapter());
	
	private AccessibleUndoManager undoManager = new AccessibleUndoManager();
  private List<DocumentListener> views = new LinkedList<DocumentListener>();
  private NeXMLEventWriter writer = new NeXMLEventWriter();
  private ComparisonDocumentDataAdapter writerAdapter = new ComparisonDocumentDataAdapter(this);
  
	
	public Document() {
		super(DEFAULT_DOCUMENT_NAME);
		JPhyloIOContentExtensionFileFilter filter = 
				new JPhyloIOReaderWriterFactory().getFormatInfo(JPhyloIOFormatIDs.NEXML_FORMAT_ID).createFileFilter(TestStrategy.BOTH);
		//TODO Possibly do not extend SwingSavable in the future as use a method similar to PhyDE 2 and the LibrAlign Swing demo application.
		getFileChooser().removeChoosableFileFilter(getFileChooser().getAcceptAllFileFilter());
		getFileChooser().addChoosableFileFilter(filter);
		getFileChooser().addChoosableFileFilter(getFileChooser().getAcceptAllFileFilter());
  	//CurrentDirectoryModel.getInstance().addFileChooser(getFileChooser());
  	setDefaultExtension("." + filter.getDefaultExtension());
  	Iterator<String> iterator = filter.getExtensions().iterator();
  	if (iterator.hasNext()) {
	  	iterator.next();  // Skip default extension, which was already added.
	  	while (iterator.hasNext()) {
	  		addFileExtension("." + iterator.next());
	  	}
  	}
		clear();
	}
	
	
	public CharacterStateSetType getTokenType() {
		return tokenType;
	}


	public void setTokenType(CharacterStateSetType tokenType) {
		this.tokenType = tokenType;
		//TODO Should an IllegalStateException be thrown if document is not empty? Should single alignments be modified otherwise, if the property changes?
	}


	public ListOrderedMap<String, ComparedAlignment> getAlignments() {
		return alignments;
	}
	
	
	public CommentList getComments() {
		return comments;
	}

	
	@Override
	protected void saveDataToFile(File file) {
		try {
			ReadWriteParameterMap parameters = new ReadWriteParameterMap();
			parameters.put(ReadWriteParameterNames.KEY_APPLICATION_NAME, Main.APPLICATION_NAME);
			parameters.put(ReadWriteParameterNames.KEY_APPLICATION_VERSION, Main.getInstance().getVersion());
			parameters.put(ReadWriteParameterNames.KEY_APPLICATION_URL, Main.APPLICATION_URL);
			//parameters.put(ReadWriteParameterNames.KEY_LOGGER, Main.getInstance().getMainFrame().getReadWriteLogDialog());
			writer.writeDocument(writerAdapter, file, parameters);
			//Main.getInstance().getMainFrame().getReadWriteLogDialog().display();  //TODO Showing this dialog should be removed, if NeXML warnings in the future are always the same and intended.
		}
		catch (Exception e) {
			String position = "";
			if (e instanceof JPhyloIOReaderException) {
				position = " (line " + ((JPhyloIOReaderException)e).getLineNumber() + ", column " + 
						((JPhyloIOReaderException)e).getColumnNumber() + ")";
			}
			
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The error \"" + e.getMessage() + "\" occured when writing to the file \"" + 
					file.getAbsolutePath() + "\"" + position + ".", "Error", JOptionPane.ERROR_MESSAGE);  //TODO The GUI output should be given somewhere else, since this method could also be called in response to a command line call in future versions.
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
	
	
	public int getAlignedLength() {
		if (isEmpty()) {
			return 0;
		}
		else {
			return getAlignments().getValue(0).getSuperaligned().getMaxSequenceLength();  //TODO The length of the first sequence might be sufficient and would be faster to determine. 
		}
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
	
	
  /** Alerts all comment positioners to reposition the comments because of made changes. */
  private void alertPositioners() {
  	Iterator<CommentPositioner> iterator = CommentPositionerFactory.getInstance().getAllPositioners().iterator();
  	while (iterator.hasNext()) {
  		iterator.next().position(this);
  	}
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

  
  /**
   * Triggers that the GUI components displaying this document are updated and sets the changed flag to {@code true}.
   */
  @Override
	public void registerChange() {
		super.registerChange();
		alertPositioners();  // Positioners must be alerted first
		fireChangeHappened();
		Main.getInstance().getMainFrame().updateTitle();
	}
}
