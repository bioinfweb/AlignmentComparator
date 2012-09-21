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


import info.bioinfweb.alignmentcomparator.document.pairalgorithms.SuperAlignmentAlgorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;
import org.biojava3.core.sequence.template.SequenceView;



public class Document {
	public static final int GAP_INDEX = -1;
	
	
	private String[] names;
	private Sequence<NucleotideCompound>[][] unalignedSequences;
	private SequenceView<NucleotideCompound> [][] alignedSequences;
	private int[][] unalignedIndices; 
	private List<Comment> comments = new ArrayList<Comment>();
	
	
	public Document() {
		super();
	}
	
	
	public void setUnalignedData(Map<String, Sequence<NucleotideCompound>> firstAlignment, 
			Map<String, Sequence<NucleotideCompound>> secondAlignment, SuperAlignmentAlgorithm algorithm) {
		
		clear();
		createArrays(firstAlignment.size());
		Iterator<String> iterator = firstAlignment.keySet().iterator();
		for (int i = 0; i < firstAlignment.size(); i++) {
			names[i] = iterator.next();
			unalignedSequences[0][i] = firstAlignment.get(names[i]);
			alignedSequences[0][i] = new SuperAlignmentSequenceView(this, 0, i);
			unalignedSequences[1][i] = secondAlignment.get(names[i]);
			alignedSequences[1][i] = new SuperAlignmentSequenceView(this, 1, i);
		}
		
		algorithm.performAlignment(this);
	}
	
	
	public void setAlignedData(String[] names, Sequence<NucleotideCompound>[][] sequences, int[][] unalignedIndices) {
		clear();
		this.names = names;
		unalignedSequences = sequences;
		this.unalignedIndices = unalignedIndices;
		for (int alignmentIndex = 0; alignmentIndex < unalignedSequences.length; alignmentIndex++) {
			for (int sequenceIndex = 0; sequenceIndex < unalignedSequences[alignmentIndex].length; sequenceIndex++) {
				alignedSequences[alignmentIndex][sequenceIndex] = new SuperAlignmentSequenceView(this, alignmentIndex, sequenceIndex);
			}
		}
	}

	
	public void clear() {
		names = new String[0];
		unalignedSequences = new Sequence[0][];
		alignedSequences = new SequenceView[0][];
		unalignedIndices = new int[0][];
		comments.clear();
	}
	
	
	public boolean isEmpty() {
		return names.length == 0;
	}
	
	
	private void createArrays(int size) {
		names = new String[size];
		unalignedSequences = new Sequence[2][size];
		alignedSequences = new SequenceView[2][size];
	}
	
	
	public String getName(int index) {
		return names[index];
	}
	
	
	public Sequence<NucleotideCompound>[] getSingleAlignment(int index) {
		return unalignedSequences[index];
	}
	
	
	public Sequence<NucleotideCompound> getAlignedSequence(int alignmentIndex, int sequenceIndex) {
		return alignedSequences[alignmentIndex][sequenceIndex];
	}
	
	
	public Sequence<NucleotideCompound> getUnalignedSequence(int alignmentIndex, int sequenceIndex) {
		return unalignedSequences[alignmentIndex][sequenceIndex];
	}
	
	
	public int getUnalignedIndex(int alignmentIndex, int pos) {
		return unalignedIndices[alignmentIndex][pos];
	}
	
	
	public int[] getUnalignedIndices(int alignmentIndex) {
		return unalignedIndices[alignmentIndex];
	}
	
	
	public void setUnalignedIndexList(int alignmentIndex, int[] value) {
		unalignedIndices[alignmentIndex] = value;
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
			return unalignedIndices[0].length;
		}
	}


	public List<Comment> getComments() {
		return comments;
	}
}
