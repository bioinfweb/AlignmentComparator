package info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.utils.DegapedIndexCalculator;



public class MaxSequencePairMatchAligner implements SuperAlignmentAlgorithm {
	private static final int DIAGONAL = 0;
	private static final int UP = 1;
	private static final int LEFT = 2;
	
	
	private Iterator<Integer> createSequenceIDIterator(Document alignments) {
		return alignments.getAlignments().getValue(0).getOriginal().sequenceIDIterator();
	}

	
	private int calculateScore(Document document, int[] alignedIndices,	DegapedIndexCalculator[] calculators) {
		int result = 0;
		boolean anyMatch = false;
		boolean[] gaps = new boolean[2];
		int[] degapedIndices = new int[2];
		Iterator<Integer> idIterator = createSequenceIDIterator(document);
		while (idIterator.hasNext()) {
			int sequenceID = idIterator.next();
			for (int i = 0; i < gaps.length; i++) {
				AlignmentModel<Character> model = document.getAlignments().getValue(i).getOriginal();
				gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, alignedIndices[i]));
				degapedIndices[i] = calculators[i].degapedIndex(sequenceID, alignedIndices[i]);
			}
			
			if (!gaps[0] && !gaps[1]) {
				if (degapedIndices[0] == degapedIndices[1]) {
					result++;
					anyMatch = true;
				}
			}
			else {
				anyMatch = true;
			}
		}
		//System.out.println("calculateScore: " + alignedIndices[0] + " " + alignedIndices[1] + " " + result);
		if (!anyMatch) {
			return -1;
		}
		else {
			return result;
		}
	}
	
	
	private byte[][] createDPMatrix(Document document, int horizontalModelIndex, int verticalModelIndex) {
		// Initialize matrix:
		AlignmentModel<Character> horizontalModel = document.getAlignments().getValue(horizontalModelIndex).getOriginal(); 
		AlignmentModel<Character> verticalModel = document.getAlignments().getValue(verticalModelIndex).getOriginal(); 
		int matrixWidth = horizontalModel.getMaxSequenceLength() + 1;
		int matrixHeight = verticalModel.getMaxSequenceLength() + 1;
		int[][] scores = new int[matrixWidth][];
		byte[][] directions = new byte[matrixWidth][];
		for (int column = 0; column < scores.length; column++) {
			scores[column] = new int[matrixHeight];
			scores[column][0] = 0;
			directions[column] = new byte[matrixHeight];
			directions[column][0] = LEFT;
		}
		for (int row = 1; row < scores[0].length; row++) {
			scores[0][row] = 0;
			directions[0][row] = UP;
		}
		
		DegapedIndexCalculator[] calculators = new DegapedIndexCalculator[2];
		calculators[1] = new DegapedIndexCalculator<Character>(verticalModel);
		int startColumn = 1;
		for (int row = 1; row < scores[0].length; row++) {
			int column;
			for (column = 0; column < startColumn; column++) {
				directions[column][row] = UP;
			}
			
			//int column = startColumn;
			boolean beforeStart = true;
			int score = 0;
			calculators[0] = new DegapedIndexCalculator<Character>(horizontalModel);
			System.out.print(row + " " + column);
			while ((column < scores.length) && ((score > 0) || beforeStart)) {
				score = calculateScore(document, new int[]{column - 1, row - 1}, calculators);
				if (score == -1) {
					if (beforeStart) {
						startColumn = column + 1;  //TODO + 1 ?
					}
					score = 0;
				}
				
				scores[column][row] = scores[column - 1][row - 1] + score;
				directions[column][row] = DIAGONAL;
				if (scores[column - 1][row] > scores[column][row]) {
					scores[column][row] = scores[column - 1][row]; 
					directions[column][row] = LEFT;
				}
				if (scores[column][row - 1] > scores[column][row]) {  // Compares with the maximum of the other two.
					scores[column][row] = scores[column][row - 1]; 
					directions[column][row] = UP;
				}
				//System.out.print(scores[column][row] + " " + directions[column][row] + ", ");

				if (score > 0) {
					beforeStart = false;
				}
				column++;
			}
			System.out.println(" " + column);

			for (; column < directions.length; column++) {
				directions[column][row] = LEFT;
			}
		}		
		
		// Calculate cells:
//		DegapedIndexCalculator[] calculators = new DegapedIndexCalculator[2];
//		calculators[0] = new DegapedIndexCalculator<Character>(horizontalModel);
//		for (int column = 1; column < scores.length; column++) {  // horizontal
//			calculators[1] = new DegapedIndexCalculator<Character>(verticalModel);
//			for (int row = 1; row < scores[column].length; row++) {  // vertical
//				scores[column][row] = scores[column - 1][row - 1] + calculateScore(document, new int[]{column - 1, row - 1}, calculators);
//				directions[column][row] = DIAGONAL;
//				if (scores[column - 1][row] > scores[column][row]) {
//					scores[column][row] = scores[column - 1][row]; 
//					directions[column][row] = LEFT;
//				}
//				if (scores[column][row - 1] > scores[column][row]) {  // Compares with the maximum of the other two.
//					scores[column][row] = scores[column][row - 1]; 
//					directions[column][row] = UP;
//				}
//				
//				//System.out.print(scores[column][row] + " " + directions[column][row] + ", ");
//			}
//			System.out.println(column);
//		}
		
		return directions;
	}
	
	
	private void createSuperAlignment(Document document, byte[][] matrix) {
		ArrayDeque[] unalignedIndexLists = new ArrayDeque[document.getAlignments().size()];  //TODO Does inserting on the left really happen in contant time or are all other elements moved in each call?
		int[] unalignedIndex = new int[2];
		for (int i = 0; i < unalignedIndexLists.length; i++) {
			int length = document.getAlignments().getValue(i).getOriginal().getMaxSequenceLength(); 
			unalignedIndexLists[i] = new ArrayDeque<Integer>(length);  //TODO Possibly multiply by factor.
			unalignedIndex[i] = length - 1;
		}

		int column = matrix.length - 1;
		int row = matrix[column].length - 1;
		while ((column > 0) || (row > 0)) {
			if (matrix[column][row] == UP) {
				// Align vertical alignment with horizontal supergap 
				unalignedIndexLists[0].addFirst(Document.GAP_INDEX);
				unalignedIndexLists[1].addFirst(unalignedIndex[1]);
				unalignedIndex[1]--;
				row--;
			}
			else if (matrix[column][row] == LEFT) {
				// Align horizontal alignment with vertical supergap 
				unalignedIndexLists[0].addFirst(unalignedIndex[0]);
				unalignedIndex[0]--;
				unalignedIndexLists[1].addFirst(Document.GAP_INDEX);
				column--;
			}
			else {
				// Align both positions without supergaps
				unalignedIndexLists[0].addFirst(unalignedIndex[0]);
				unalignedIndex[0]--;
				unalignedIndexLists[1].addFirst(unalignedIndex[1]);
				unalignedIndex[1]--;
				row--;
				column--;
			}
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
			createSuperAlignment(document, createDPMatrix(document, 0, 1));
		}
	}
}
