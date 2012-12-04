/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
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
import info.bioinfweb.alignmentcomparator.document.io.results.ResultsWriter;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.alignmentcomparator.document.undo.DocumentEdit;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner;
import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositionerFactory;
import info.webinsel.util.ChangeMonitorable;
import info.webinsel.util.io.Savable;
import info.webinsel.util.swing.AccessibleUndoManager;
import info.webinsel.util.swing.SwingSavable;
import info.webinsel.util.swing.SwingSaver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.template.Sequence;
import org.biojava3.core.sequence.template.SequenceView;



public class Document extends SwingSaver 
    implements ChangeMonitorable, Savable, SwingSavable {
	
	public static final int GAP_INDEX = -1;
	public static final String DEFAULT_DOCUMENT_NAME = "New";
	public static final double ARRAY_LIST_SIZE_FACTOR = 1.3;
	
	
	private String[] names;
	private ArrayList<DNASequence>[] unalignedSequences;
	private SequenceView[][] alignedSequences;
	private ArrayList<Integer>[] unalignedIndices; 
	private CommentList comments = new CommentList(new SequencePositionAdapter());
	private AccessibleUndoManager undoManager = new AccessibleUndoManager();
	private ResultsWriter writer = new ResultsWriter();
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
	
	
	public void setUnalignedData(Map<String, DNASequence> firstAlignment, Map<String, DNASequence> secondAlignment, 
			SuperAlignmentAlgorithm algorithm) throws Exception {
		
		clear();
		createArrays(firstAlignment.size());
		Iterator<String> iterator = firstAlignment.keySet().iterator();
		for (int i = 0; i < firstAlignment.size(); i++) {
			names[i] = iterator.next();
			unalignedSequences[0].add(firstAlignment.get(names[i]));
			alignedSequences[0][i] = new SuperAlignmentSequenceView(this, 0, i);
			unalignedSequences[1].add(secondAlignment.get(names[i]));
			alignedSequences[1][i] = new SuperAlignmentSequenceView(this, 1, i);
		}
		
		algorithm.performAlignment(this);
		registerChange();
		fireNamesChanged();
	}
	
	
	public void setAlignedData(String[] names, List<DNASequence>[] sequences, List<Integer>[] unalignedIndices) {
		createArrays(names.length);
		this.names = names;
		for (int i = 0; i < unalignedIndices.length; i++) {
			setUnalignedIndexList(i, unalignedIndices[i]);
			setUnalignedSequences(i, sequences[i]);
		}
		for (int alignmentIndex = 0; alignmentIndex < unalignedSequences.length; alignmentIndex++) {
			for (int sequenceIndex = 0; sequenceIndex < unalignedSequences[alignmentIndex].size(); sequenceIndex++) {
				alignedSequences[alignmentIndex][sequenceIndex] = new SuperAlignmentSequenceView(this, alignmentIndex, sequenceIndex);
			}
		}
		performChange();  // Hier nicht registerChange(), da Dokument am Anfang nicht als ungespeichert angezeigt werden soll.
		fireNamesChanged();
	}

	
	@Override
	protected void saveDataToFile(File file) {
		try {
			writer.write(new BufferedOutputStream(new FileOutputStream(file)), this);
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
		names = new String[0];
		unalignedSequences = new ArrayList[0];
		alignedSequences = new SequenceView[0][];
		unalignedIndices = new ArrayList[0];
		comments.clear();
	}
	
	
	public boolean isEmpty() {
		return names.length == 0;
	}
	
	
	private void createArrays(int size) {
		names = new String[size];
		unalignedSequences = new ArrayList[2];
		alignedSequences = new SequenceView[2][size];
		unalignedIndices = new ArrayList[2];
		for (int i = 0; i < 2; i++) {
			unalignedSequences[i] = new ArrayList<DNASequence>();
			unalignedIndices[i] = new ArrayList<Integer>();
		}
	}
	
	
	public String getName(int index) {
		return names[index];
	}
	
	
	public int getIndexByName(String name) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	
	public List<DNASequence> getSingleAlignment(int index) {
		return unalignedSequences[index];
	}
	
	
	public Sequence getAlignedSequence(int alignmentIndex, int sequenceIndex) {
		return alignedSequences[alignmentIndex][sequenceIndex];
	}
	
	
	public DNASequence getUnalignedSequence(int alignmentIndex, int sequenceIndex) {
		return unalignedSequences[alignmentIndex].get(sequenceIndex);
	}
	
	
	public ArrayList<DNASequence> getUnalignedSequences(int alignmentIndex) {
		return unalignedSequences[alignmentIndex];
	}
	
	
	public int getUnalignedIndex(int alignmentIndex, int pos) {
		return unalignedIndices[alignmentIndex].get(pos);
	}
	
	
	public void insertSuperGap(int alignmentIndex, int pos) {
		unalignedIndices[alignmentIndex].add(pos, SuperAlignmentSequenceView.GAP_INDEX);
		if (alignmentIndex == 0) {
			unalignedIndices[1].add(SuperAlignmentSequenceView.GAP_INDEX);
		}
		else {
			unalignedIndices[0].add(SuperAlignmentSequenceView.GAP_INDEX);
		}
	}
	
	
	public void removeSuperGap(int alignmentIndex, int pos) {
		if (unalignedIndices[alignmentIndex].get(pos).equals(SuperAlignmentSequenceView.GAP_INDEX)) {
			unalignedIndices[alignmentIndex].remove(pos);
			unalignedIndices[alignmentIndex].add(SuperAlignmentSequenceView.GAP_INDEX);
		}
		else {
			throw new IllegalArgumentException("There is no gap at position " + pos + " in alignment " + alignmentIndex + ".");
		}
	}
	
	
	/**
	 * Removes trailing super alignment gaps present in both alignments.
	 */
	public void removeTrailingGaps() {
		int lastIndex = unalignedIndices[0].size() - 1;
		while ((lastIndex >= 0) && unalignedIndices[0].get(lastIndex).equals(SuperAlignmentSequenceView.GAP_INDEX) &&
				unalignedIndices[1].get(lastIndex).equals(SuperAlignmentSequenceView.GAP_INDEX)) {
			
			unalignedIndices[0].remove(lastIndex);
			unalignedIndices[1].remove(lastIndex);
			lastIndex--;
		}
	}
	
	
	public void setUnalignedIndexList(int alignmentIndex, List<Integer> list) {
		if (list instanceof ArrayList<?>) {
			unalignedIndices[alignmentIndex] = (ArrayList<Integer>)list;
		}
		else {
			unalignedIndices[alignmentIndex] = new ArrayList<Integer>((int)(list.size() * ARRAY_LIST_SIZE_FACTOR));
			unalignedIndices[alignmentIndex].addAll(list);
		}
	}
	
	
	public void setUnalignedSequences(int alignmentIndex, List<DNASequence> list) {
		if (list instanceof ArrayList<?>) {
			unalignedSequences[alignmentIndex] = (ArrayList<DNASequence>)list;
		}
		else {
			unalignedSequences[alignmentIndex] = new ArrayList<DNASequence>((int)(list.size() * ARRAY_LIST_SIZE_FACTOR));
			unalignedSequences[alignmentIndex].addAll(list);
		}
	}
	
	
	 /**
		* Returns the number of sequences present in each alignment.
		*/
	public int getSequenceCount() {
		return names.length;
	}
	
	
	public int getAlignedLength() {
		if (isEmpty()) {
			return 0;
		}
		else {
			return unalignedIndices[0].size();
		}
	}


	public CommentList getComments() {
		return comments;
	}
	
	
  public AccessibleUndoManager getUndoManager() {
		return undoManager;
	}


	public void executeEdit(DocumentEdit edit) {
    if (!getUndoManager().addEdit(edit)) {  // Muss vor Ausführung erfolgen da sonst Undo-Schalter ggf. nicht aktiviert werden.
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
