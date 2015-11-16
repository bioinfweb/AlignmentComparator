package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencematch;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.AlignmentModel;



public class MaximumSequenceMatchAligner implements SuperAlignmentAlgorithm {
	private static final int NO_GAP = 0;
	private static final int GAP_IN_FIRST = 1;
	private static final int GAP_IN_SECOND = 2;
	
	
	private Iterator<Integer> createSequenceIDIterator(Document alignments) {
		return alignments.getAlignments().getValue(0).getOriginal().sequenceIDIterator();
	}
	
	
	private int minAlignmentLength(Document alignments) {
		int result = 0;
		for (ComparedAlignment alignment : alignments.getAlignments().values()) {
			result = Math.min(result, alignment.getOriginal().getMaxSequenceLength());
		}
		return result;
	}
	
	
	private Map<Integer, List<int[]>> createShiftLists(Document alignments) {  //TODO Can this method also be efficiently implemented with more than two models? 
		Map<Integer, List<int[]>> result = new TreeMap<>();
		int alignmentCount = alignments.getAlignments().size();
		int[] positions = new int[alignmentCount];
		int[] columnCounts = new int[alignmentCount];
		boolean[] gaps = new boolean[alignmentCount];
		
		Iterator<Integer> idIterator = createSequenceIDIterator(alignments);
		while (idIterator.hasNext()) {
			int sequenceID = idIterator.next();
			List<int[]> shiftList = new ArrayList<int[]>();
			
			for (int i = 0; i < positions.length; i++) {
				positions[i] = 0;
				columnCounts[i] = alignments.getAlignments().getValue(i).getOriginal().getMaxSequenceLength();
			}
			
			int currentCase = NO_GAP;
			while ((positions[0] < columnCounts[0]) && (positions[1] < columnCounts[1])) {
				for (int i = 0; i < gaps.length; i++) {
					AlignmentModel<Character> model = alignments.getAlignments().getValue(i).getOriginal(); 
					gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, positions[i]));
				}
				
				int previousCase = currentCase;
				if (gaps[0] && !gaps[1]) {
					positions[0]++;
					currentCase = GAP_IN_FIRST;
					if (positions[1] == 0) {
						previousCase = currentCase;  // Avoid always creating entry in first step. 
					}
				}
				else if (!gaps[0] && gaps[1]) {
					positions[1]++;
					currentCase = GAP_IN_SECOND;
					if (positions[0] == 0) {
						previousCase = currentCase;  // Avoid always creating entry in first step. 
					}
				}
				else {
					positions[0]++;
					positions[1]++;
					currentCase = NO_GAP;
				}
				
				if (previousCase != currentCase) {
					shiftList.add(positions);
				}
			}
			
			result.put(sequenceID, shiftList);
		}
		
		return result;
	}
	
	
	@Override
	public void performAlignment(Document alignments) throws Exception {
		if (alignments.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			Map<Integer, List<int[]>> shiftLists = createShiftLists(alignments);
			//TODO Extract optimal path from set of lists.
		}
	}
}
