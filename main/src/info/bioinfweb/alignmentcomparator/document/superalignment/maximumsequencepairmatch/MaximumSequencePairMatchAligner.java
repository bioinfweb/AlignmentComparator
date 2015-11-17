package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencepairmatch;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.utils.DegapedIndexCalculator;



public class MaximumSequencePairMatchAligner implements SuperAlignmentAlgorithm {
	private static final int NO_GAP = 0;
	private static final int GAP_IN_FIRST = 1;
	private static final int GAP_IN_SECOND = 2;
	
	
	private DegapedIndexCalculator[] calculators;
	
	
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
	
	
	private ShiftQueues createShiftQueues(Document alignments) {  //TODO Can this method also be efficiently implemented with more than two models? 
		ShiftQueues result = new ShiftQueues();
		int alignmentCount = alignments.getAlignments().size();
		int[] positions = new int[alignmentCount];
		int[] columnCounts = new int[alignmentCount];
		boolean[] gaps = new boolean[alignmentCount];
		
		Iterator<Integer> idIterator = createSequenceIDIterator(alignments);
		while (idIterator.hasNext()) {
			int sequenceID = idIterator.next();
			Queue<int[]> shiftList = result.getQueue(sequenceID);
			
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
		}
		
		return result;
	}
	
	
	private void createCalculators(Document alignments) {
		calculators = new DegapedIndexCalculator[alignments.getAlignments().size()];
		for (int i = 0; i < calculators.length; i++) {
			calculators[i] = new DegapedIndexCalculator<Character>(alignments.getAlignments().valueList().get(i).getOriginal());
		}
	}
	
	
	/**
	 * Returns the number of matches between all correctly aligned sequence pairs from both alignments.
	 * <p>
	 * This implementation will not work with more than two alignments. (It would have to add up all 
	 * pairwise scores then.) 
	 * 
	 * @param alignments the document containing the single original alignments
	 * @param startColumns an array of start columns with the length 2
	 * @param length the length of the superalignment to be scored
	 * @return the number of positions in a sequence pair that match in the specified superalignment
	 */
	private int calculateScore(Document alignments, int[] startColumns, int length) {
		int result = 0;
		
		boolean[] gaps = new boolean[2];
		int[] degapedIndices = new int[2];
		for (int position = 0; position < length; position++) {
			Iterator<Integer> idIterator = createSequenceIDIterator(alignments);
			while (idIterator.hasNext()) {
				int sequenceID = idIterator.next();
				for (int i = 0; i < gaps.length; i++) {
					AlignmentModel<Character> model = alignments.getAlignments().getValue(i).getOriginal();
					int alignedPosition = startColumns[i] + position;
					gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, alignedPosition));
					degapedIndices[i] = calculators[0].degapedIndex(sequenceID, alignedPosition);
				}
				
				if (!gaps[0] && !gaps[1] && (degapedIndices[0] == degapedIndices[1])) {
					result++;
				}
			}
		}
		return result;
	}
	
	
	private AlignmentNode getOpenEndNode(Map<Integer, AlignmentNode> openEnds, int[] positions) {
		for (AlignmentNode node : openEnds.values()) {
			if (node.equalsToPositions(positions)) {
				return node;
			}
		}
		return new AlignmentNode(positions);
	}
	
	
	private void linkNodeToOptimalOpenEnd(Document alignments, Map<Integer, AlignmentNode> openEnds, 
			AlignmentNode currentNode, int maxScore) {
		
		AlignmentNode optimalPreviousNode = null;
		for (AlignmentNode otherNode : openEnds.values()) {
			int length = Math.min(currentNode.getPosition(0) - otherNode.getPosition(0), 
					currentNode.getPosition(1) - otherNode.getPosition(1));
			
			if (length > 0) {
				int currentScore = otherNode.getOptimalScore() + calculateScore(alignments, otherNode.getPositions(), length);
				if (currentScore > maxScore) {
					optimalPreviousNode = currentNode;
					maxScore = currentScore;
				}
			}
		}
		
		currentNode.setOptimalPreviousNode(optimalPreviousNode);  // null will be set here, if the score to the superalignment start was maximal.
		currentNode.setOptimalScore(maxScore);
	}
	
	
	private AlignmentNode createGraph(Document alignments, ShiftQueues shiftQueues) {
		Map<Integer, AlignmentNode> openEnds = new TreeMap<>();
		
		// Create graph with optimal paths:
		while (!shiftQueues.isEmpty()) {
			int sequenceID = shiftQueues.nextSequenceID();
			int[] positions = shiftQueues.getQueue(sequenceID).poll();
			AlignmentNode currentNode = getOpenEndNode(openEnds, positions);
			
			if (currentNode.getOptimalScore() == -1) {  // new node
				AlignmentNode optimalPreviousNode = null;
				int maxScore = -1;
				if (openEnds.size() < alignments.getAlignments().getValue(0).getOriginal().getSequenceCount()) {  // If at least one open end is the start of the superalignment:
					maxScore = calculateScore(alignments, new int[]{0, 0},  // Score to superalignment start 
							Math.min(currentNode.getPosition(0), currentNode.getPosition(1)));
				}
				
				for (AlignmentNode otherNode : openEnds.values()) {
					int length = Math.min(currentNode.getPosition(0) - otherNode.getPosition(0), 
							currentNode.getPosition(1) - otherNode.getPosition(1));
					
					if (length > 0) {
						int currentScore = otherNode.getOptimalScore() + calculateScore(alignments, otherNode.getPositions(), length);
						if (currentScore > maxScore) {
							optimalPreviousNode = currentNode;
							maxScore = currentScore;
						}
					}
				}
				
				currentNode.setOptimalPreviousNode(optimalPreviousNode);  // null will be set here, if the score to the superalignment start was maximal.
				currentNode.setOptimalScore(maxScore);
			}
			openEnds.put(sequenceID, currentNode);  // Must not happen before score calculation.
		}
		
		// Calculate maximum score from open ends to the end of the superalignment: 
		int ends[] = new int[alignments.getAlignments().size()];
		for (int i = 0; i < ends.length; i++) {
			ends[i] = alignments.getAlignments().getValue(0).getOriginal().getMaxSequenceLength(); 
		}
		AlignmentNode result = new AlignmentNode(ends);
		for (AlignmentNode otherNode : openEnds.values()) {
			
		}
		return result;
	}
	
	
	@Override
	public void performAlignment(Document alignments) throws Exception {
		if (alignments.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			ShiftQueues shiftQueues = createShiftQueues(alignments);
			createCalculators(alignments);
			
			//TODO Extract optimal path from set of lists.
		}
	}
}
