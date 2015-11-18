package info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
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



public class MaxSequencePairMatchAligner implements SuperAlignmentAlgorithm {
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
	
	
	private ShiftQueues createShiftQueues(Document document) {  //TODO Can this method also be efficiently implemented with more than two models? 
		ShiftQueues result = new ShiftQueues();
		int alignmentCount = document.getAlignments().size();
		int[] positions = new int[alignmentCount];
		int[] columnCounts = new int[alignmentCount];
		boolean[] gaps = new boolean[alignmentCount];
		for (int i = 0; i < columnCounts.length; i++) {
			columnCounts[i] = document.getAlignments().getValue(i).getOriginal().getMaxSequenceLength();
		}
		
		Iterator<Integer> idIterator = createSequenceIDIterator(document);
		while (idIterator.hasNext()) {
			int sequenceID = idIterator.next();
			Queue<int[]> shiftList = result.getQueue(sequenceID);
			
			for (int i = 0; i < positions.length; i++) {
				positions[i] = 0;
			}
			
			int currentCase = NO_GAP;
			while ((positions[0] < columnCounts[0]) && (positions[1] < columnCounts[1])) {
				for (int i = 0; i < gaps.length; i++) {
					AlignmentModel<Character> model = document.getAlignments().getValue(i).getOriginal(); 
					gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, positions[i]));
				}
				
				if (gaps[0] && !gaps[1]) {
					positions[0]++;
					currentCase = GAP_IN_FIRST;
				}
				else if (!gaps[0] && gaps[1]) {
					positions[1]++;
					currentCase = GAP_IN_SECOND;
				}
				else {
					if (currentCase != NO_GAP) {
						shiftList.add(Arrays.copyOf(positions, 2));
					}
					
					positions[0]++;
					positions[1]++;
					currentCase = NO_GAP;
				}
			}
		}
		
		return result;
	}
	
	
	private void createCalculators(Document document) {
		calculators = new DegapedIndexCalculator[document.getAlignments().size()];
		for (int i = 0; i < calculators.length; i++) {
			calculators[i] = new DegapedIndexCalculator<Character>(document.getAlignments().valueList().get(i).getOriginal());
		}
	}
	
	
	/**
	 * Returns the number of matches between all correctly aligned sequence pairs from both alignments.
	 * <p>
	 * This implementation will not work with more than two alignments. (It would have to add up all 
	 * pairwise scores then.) 
	 * 
	 * @param document the document containing the single original alignments
	 * @param startColumns an array of start columns with the length 2
	 * @param length the length of the superalignment to be scored
	 * @return the number of positions in a sequence pair that match in the specified superalignment
	 */
	private int calculateScore(Document document, int[] startColumns, int length) {
		int result = 0;
		
		boolean[] gaps = new boolean[2];
		int[] degapedIndices = new int[2];
		for (int position = 0; position < length; position++) {
			Iterator<Integer> idIterator = createSequenceIDIterator(document);
			while (idIterator.hasNext()) {
				int sequenceID = idIterator.next();
				for (int i = 0; i < gaps.length; i++) {
					AlignmentModel<Character> model = document.getAlignments().getValue(i).getOriginal();
					int alignedPosition = startColumns[i] + position;
					gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, alignedPosition));
					degapedIndices[i] = calculators[i].degapedIndex(sequenceID, alignedPosition);
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
	
	
	private void linkNodeToOptimalOpenEnd(Document document, Map<Integer, AlignmentNode> openEnds, AlignmentNode newNode) {
		AlignmentNode optimalPreviousNode = null;
		int maxScore = -1;
		if (openEnds.size() < document.getAlignments().getValue(0).getOriginal().getSequenceCount()) {  // If at least one open end is the start of the superalignment:
			maxScore = calculateScore(document, new int[]{0, 0},  // Score to superalignment start 
					Math.min(newNode.getPosition(0), newNode.getPosition(1)));
		}
		
		for (AlignmentNode otherNode : openEnds.values()) {
			int length = Math.min(newNode.getPosition(0) - otherNode.getPosition(0), 
					newNode.getPosition(1) - otherNode.getPosition(1));
			
			if (length > 0) {  // Also avoids linking the node to itself
				int currentScore = otherNode.getOptimalScore() + calculateScore(document, otherNode.getPositions(), length);
				if (currentScore > maxScore) {
					optimalPreviousNode = otherNode;
					maxScore = currentScore;
				}
			}
		}
		
		newNode.setOptimalPreviousNode(optimalPreviousNode);  // null will be set here, if the score to the superalignment start was maximal.
		newNode.setOptimalScore(maxScore);
	}
	
	
	private AlignmentNode createGraph(Document document, ShiftQueues shiftQueues) {
		Map<Integer, AlignmentNode> openEnds = new TreeMap<>();
		
		// Create graph with optimal paths:
		while (!shiftQueues.isEmpty()) {
			int sequenceID = shiftQueues.nextSequenceID();
			int[] positions = shiftQueues.getQueue(sequenceID).poll();
			AlignmentNode currentNode = getOpenEndNode(openEnds, positions);
			
			if (currentNode.getOptimalScore() == -1) {  // new node
				linkNodeToOptimalOpenEnd(document, openEnds, currentNode);
			}
			openEnds.put(sequenceID, currentNode);  // Must not happen before score calculation.
		}
		
		// Calculate maximum score from open ends to the end of the superalignment: 
		int ends[] = new int[document.getAlignments().size()];
		for (int i = 0; i < ends.length; i++) {
			ends[i] = document.getAlignments().getValue(i).getOriginal().getMaxSequenceLength(); 
		}
		AlignmentNode result = new AlignmentNode(ends);
		
		linkNodeToOptimalOpenEnd(document, openEnds, result);
		return result;
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void createSuperAlignment(Document document, AlignmentNode end) {
		ArrayDeque[] unalignedIndexLists = new ArrayDeque[document.getAlignments().size()];  //TODO Does inserting on the left really happen in contant time or are all other elements moved in each call? 
		for (int i = 0; i < unalignedIndexLists.length; i++) {
			unalignedIndexLists[i] = new ArrayDeque<Integer>(
					document.getAlignments().getValue(i).getOriginal().getMaxSequenceLength());  //TODO Possibly multiply by factor.
		}
		
		AlignmentNode currentNode = end;
		while (currentNode != null) {
			int[] previousPositions;
			if (currentNode.getOptimalPreviousNode() == null) {
				previousPositions = new int[]{0, 0};
			}
			else {
				previousPositions = currentNode.getOptimalPreviousNode().getPositions();
			}
			
			// Insert supergap:
			int superGapLength = (currentNode.getPosition(0) - previousPositions[0]) -
					(currentNode.getPosition(1) - previousPositions[1]);
			if (superGapLength < 0) {
				for (int i = 0; i < -superGapLength; i++) {
					unalignedIndexLists[0].addFirst(Document.GAP_INDEX);
				}
			}
			else if (superGapLength > 0) {
				for (int i = 0; i < superGapLength; i++) {
					unalignedIndexLists[1].addFirst(Document.GAP_INDEX);
				}
			}
			
			// Insert link indices:
			for (int alignmentIndex = 0; alignmentIndex < unalignedIndexLists.length; alignmentIndex++) {
				for (int columnIndex = currentNode.getPosition(alignmentIndex) - 1; columnIndex >= previousPositions[alignmentIndex]; 
						columnIndex--) {
					
					unalignedIndexLists[alignmentIndex].addFirst(columnIndex);
				}
			}
			
			currentNode = currentNode.getOptimalPreviousNode();
		}
		
		// Create superalignment decorators:
		for (int alignmentIndex = 0; alignmentIndex < unalignedIndexLists.length; alignmentIndex++) {
			document.getAlignments().getValue(alignmentIndex).createSuperaligned(new ArrayList(unalignedIndexLists[alignmentIndex]));
			unalignedIndexLists[alignmentIndex] = null;  // Allow removing copy of list from memory.
		}
	}
	
	
	@Override
	public void performAlignment(Document document) throws Exception {
		if (document.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			ShiftQueues shiftQueues = createShiftQueues(document);
			createCalculators(document);
			AlignmentNode end = createGraph(document, shiftQueues);
//			AlignmentNode node = end;
//			while (node != null) {
//				System.out.println(node + " ");
//				node = node.getOptimalPreviousNode();
//			}
			createSuperAlignment(document, end);
		}
	}
}
