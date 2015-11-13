package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencematch;


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.utils.DegapedIndexCalculator;



public class MaximumSequenceMatchAligner implements SuperAlignmentAlgorithm {
	private AlignmentNode createGraph(Document alignments) {  //TODO Can this method also be efficiently implemented with more than two models? 
		// Create index calculators:
		DegapedIndexCalculator<Character>[] calculators = new DegapedIndexCalculator[alignments.getAlignments().size()];
		for (int i = 0; i < calculators.length; i++) {
			calculators[i] = new DegapedIndexCalculator<Character>(alignments.getAlignments().get(
					alignments.getAlignments().get(i)).getOriginal()); 
		}
		
		// Create map of current column positions:
		Map<Integer, int[]> positionsMap = new TreeMap<Integer, int[]>();
		int alignmentCount = alignments.getAlignments().size();
		Iterator<Integer> idIterator = 
				alignments.getAlignments().get(alignments.getAlignments().get(0)).getOriginal().sequenceIDIterator();
		while (idIterator.hasNext()) {
			int[] positions = new int[alignmentCount];
			for (int i = 0; i < positions.length; i++) {
				positions[i] = 0;
			}
			positionsMap.put(idIterator.next(), positions);
		}
		
		//TODO Algoriothmus wie im Wiki beschrieben implementieren und dabei eine Datenstruktur füllen, die 
		//     Positionen der Supergaps und Verknüpfungen der alternativen Alignierungen abbildet.
		
		AlignmentNode result = new AlignmentNode();
		
		return result;
	}
	
	
	@Override
	public void performAlignment(Document alignments) throws Exception {
		if (alignments.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			//TODO implement
		}
	}
}
