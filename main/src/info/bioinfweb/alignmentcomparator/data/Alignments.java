package info.bioinfweb.alignmentcomparator.data;


import info.bioinfweb.alignmentcomparator.data.pairalgorithms.SuperAlignmentAlgorithm;

import java.util.Iterator;
import java.util.Map;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;
import org.biojava3.core.sequence.template.SequenceView;



public class Alignments {
	public static final int GAP_INDEX = -1;
	
	
	private String[] names;
	private Sequence<NucleotideCompound>[][] unalignedSequences;
	private SequenceView<NucleotideCompound> [][] alignedSequences;
	private int[][] unalignedIndices; 
	
	
	public Alignments(Map<String, Sequence<NucleotideCompound>> firstAlignment, 
			Map<String, Sequence<NucleotideCompound>> secondAlignment, SuperAlignmentAlgorithm algorithm) {
		
		super();
		
		createArrays(firstAlignment.size());
		Iterator<String> iterator = firstAlignment.keySet().iterator();
		for (int i = 0; i < firstAlignment.size(); i++) {
			names[i] = iterator.next();
			unalignedSequences[0][i] = firstAlignment.get(names[i]);
			unalignedSequences[1][i] = secondAlignment.get(names[i]);
		}
		
		algorithm.performAlignment(this);
		//TODO align
	}
	
	
	private void createArrays(int size) {
		names = new String[size];
		unalignedSequences = new Sequence[2][size];
		alignedSequences = new SequenceView[2][size];
	}
	
	
	public Sequence<NucleotideCompound>[] getSingleAlignment(int index) {
		return unalignedSequences[index];
	}
	
	
	public int getUnalignedIndex(int alignmentIndex, int pos) {
		return unalignedIndices[alignmentIndex][pos];
	}
	
	
	public void setUnalignedIndexList(int alignmentIndex, int[] value) {
		unalignedIndices[alignmentIndex] = value;
	}
	
	
	public int getAlignedLength() {
		return unalignedIndices[0].length;
	}
}
