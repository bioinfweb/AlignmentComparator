/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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

	private static final int ALL_COLUMNS_LEFT_OF_ROWS = -1;
	private static final int ALL_COLUMNS_RIGHT_OF_ROWS = -2;
	
	
	private Iterator<String> createSequenceIDIterator(Document alignments) {
		return alignments.getAlignments().getValue(0).getOriginal().sequenceIDIterator();
	}

	
	private int calculateScore(Document document, int[] alignedIndices,	DegapedIndexCalculator<?>[] calculators) {
		int result = 0;
		boolean allColumnsLeftOfRows = true;
		boolean allColumnsRightOfRows = true;
		boolean[] gaps = new boolean[2];
		int[] degapedIndices = new int[2];
		Iterator<String> idIterator = createSequenceIDIterator(document);
		while (idIterator.hasNext()) {
			String sequenceID = idIterator.next();
			for (int i = 0; i < gaps.length; i++) {
				AlignmentModel<Character> model = document.getAlignments().getValue(i).getOriginal();
				
				//TODO Problem: LibrAlign sequence IDs are not equal in both alignments. 
				//     1) Do matching in a different way?
				//     2) Assign different (matching) IDs?
				System.out.println("calculateScore() seqCount: " + model.getSequenceCount());
				Iterator<String> iterator = model.sequenceIDIterator();
				while (iterator.hasNext()) {
					System.out.print(iterator.next() + " ");
				}
				System.out.println();
				
				gaps[i] = model.getTokenSet().isGapToken(model.getTokenAt(sequenceID, alignedIndices[i]));
				degapedIndices[i] = calculators[i].degapedIndex(sequenceID, alignedIndices[i]);
			}
			
			if (degapedIndices[0] == degapedIndices[1]) {
				if (!gaps[0] && !gaps[1]) {
					result++;
					
					allColumnsLeftOfRows = false; 
					allColumnsRightOfRows = false;
				}
				else if (gaps[0] && !gaps[1]) {
					allColumnsRightOfRows = false;
				}
				else if (!gaps[0] && gaps[1]) {
					allColumnsLeftOfRows = false;
				}
			}
			else if (degapedIndices[0] < degapedIndices[1]) {
				allColumnsRightOfRows = false;
			}
			else {  // degapedIndices[0] > degapedIndices[1]
				allColumnsLeftOfRows = false;
			}
		}
		
		if (allColumnsLeftOfRows) {
			return ALL_COLUMNS_LEFT_OF_ROWS;
		}
		else if (allColumnsRightOfRows) {
			return ALL_COLUMNS_RIGHT_OF_ROWS;
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
		
		// Calculate cells:
		DegapedIndexCalculator[] calculators = new DegapedIndexCalculator[2];
		calculators[1] = new DegapedIndexCalculator<Character>(verticalModel);
		int startColumn = 1;
		for (int row = 1; row < scores[0].length; row++) {
			int column;
			for (column = 0; column < startColumn; column++) {
				directions[column][row] = UP;
			}
			
			boolean allColumnsRightOfRows = false;
			calculators[0] = new DegapedIndexCalculator<Character>(horizontalModel);
			//System.out.print(row + " " + column);
			while ((column < scores.length) && !allColumnsRightOfRows) {
				int score = calculateScore(document, new int[]{column - 1, row - 1}, calculators);
				
				if (score < 0) {
					if (score == ALL_COLUMNS_LEFT_OF_ROWS) {
						startColumn = column;
					}
					else if (score == ALL_COLUMNS_RIGHT_OF_ROWS) {
						allColumnsRightOfRows = true;
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
