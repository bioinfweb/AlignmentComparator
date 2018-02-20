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
package info.bioinfweb.alignmentcomparator.document.superalignment;


import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.OriginalAlignment;
import info.bioinfweb.commons.collections.PackedObjectArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.forester.evoinference.distance.NeighborJoining;
import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;



public class MaxSequencePairMatchAligner implements SuperAlignmentAlgorithm {
	private static final byte DIAGONAL = 0;
	private static final byte UP = 1;
	private static final byte LEFT = 2;	

	
	private static class Matrix {
		byte[][] directionMatrix;
		long score;
		
		public Matrix(byte[][] directionMatrix, long score) {
			super();
			this.directionMatrix = directionMatrix;
			this.score = score;
		}
	}
	
	
	/**
	 * Calculates the maximal score a superalignment could have, which is equal to the number of non-gap tokens in
	 * one alignment. (It is assumed that all alignments in {@code document} contain the same non-gap tokens in each
	 * sequence.)
	 * 
	 * @param document the document containing the alignments to be compared
	 */
	private long calculateMaxScore(Document document) {
		long result = 0;
		if (!document.getAlignments().isEmpty()) {  // This method assumes that all alignments contain the same non-gap tokens in each sequence.
			OriginalAlignment alignment = document.getAlignments().getValue(0).getOriginal();
			Iterator<String> iterator = alignment.sequenceIDIterator();
			while (iterator.hasNext()) {
				result += alignment.getIndexTranslator().getUnalignedLength(iterator.next());
			}
		}
		return result;
	}
	
	
	private PhylogenyNode findLongestBranch(PhylogenyNode parent, PhylogenyNode result, double length) {
		for (PhylogenyNode child : parent.getDescendants()) {
			if (child.getDistanceToParent() > length) {
				result = child;
				length = child.getDistanceToParent();
			}
			result = findLongestBranch(child, result, length);
		}
		return result;
	}
	
	
	/**
	 * Makes sure the tree is rooted on its longest branch.
	 * 
	 * @param tree the tree to be possibly rerooted
	 */
	private void reroot(Phylogeny tree) {
		PhylogenyNode root = tree.getRoot();
		if (root.getDescendants().size() != 2) {
			throw new InternalError("Unexpected NJ tree.");
		}
		
		double length = root.getChildNode(0).getDistanceToParent() + root.getChildNode(1).getDistanceToParent();  // Assumes that there are always two nodes under the root.
		root = findLongestBranch(root, null, length);  // The direct children of root will not be selected, since they must have a samller distance than their combined branches.
		if (root != null) {
			tree.reRoot(root);
		}
	}
	
	
	private Phylogeny calculateGuideTree(Document document) {
		long maxScore = calculateMaxScore(document);
		ListOrderedMap<String, ComparedAlignment> alignments = document.getAlignments();
		BasicSymmetricalDistanceMatrix matrix = new BasicSymmetricalDistanceMatrix(alignments.size());
		for (int column = 0; column < alignments.size(); column++) {
			matrix.setIdentifier(column, alignments.get(column));
			for (int row = column; row < alignments.size(); row++) {
				matrix.setValue(column, row, maxScore - calculateDirectionMatrix(
						new ComparedAlignment[]{alignments.getValue(column), alignments.getValue(row)}).score);
				//TODO Path could already be extracted from matrix here for later reuse or using memory for matrix could be avoided here, when it's not used.
			}
		}

		Phylogeny result = NeighborJoining.createInstance().execute(matrix);
		reroot(result);
		return result;
	}
	
	
	private Matrix calculateDirectionMatrix(ComparedAlignment[] alignments) {
		//TODO Refactor to use only one row of the score matrix at a time.
		
		final OriginalAlignment firstAlignment = alignments[0].getOriginal();
		final OriginalAlignment secondAlignment = alignments[1].getOriginal();
		final int columnCountInFirst = firstAlignment.getMaxSequenceLength();
		final int columnCountInSecond = secondAlignment.getMaxSequenceLength();
		final long[][] scoreMatrix = new long[columnCountInFirst + 1][columnCountInSecond + 1];  // Values are initialized with 0.
		
		// Write local scores into matrix:
		for (int columnInFirst = 0; columnInFirst < columnCountInFirst; columnInFirst++) {
			Iterator<String> iterator = firstAlignment.sequenceIDIterator();
			while (iterator.hasNext()) {
				String seqIDInFirst = iterator.next();
				if (!firstAlignment.getTokenSet().isGapToken(firstAlignment.getTokenAt(seqIDInFirst, columnInFirst))) {
					String seqName = firstAlignment.sequenceNameByID(seqIDInFirst);
					String seqIDInSecond = secondAlignment.sequenceIDsByName(seqName).iterator().next();
					
					int unalignedIndex = firstAlignment.getIndexTranslator().getUnalignedIndex(seqIDInFirst, columnInFirst).getCorresponding();
					if (unalignedIndex >= 0) {
						scoreMatrix[columnInFirst + 1][secondAlignment.getIndexTranslator().getAlignedIndex(seqIDInSecond, unalignedIndex) + 1]++;
								// + 1 because the first column and row in the matrix refer to the position before the first alignment column.
					}
					else {
						throw new InternalError("Unexpected error: Index (" + columnInFirst + " -> " + unalignedIndex + 
								") was at a gap or out of range. Contact support@bioinfweb.info if you see this message.");
					}
				}
			}
		}
		
		// Calculate global scores and directions using DP:
		byte[][] directionMatrix = new byte[columnCountInFirst + 1][columnCountInSecond + 1];
		for (int column = 0; column < directionMatrix.length; column++) {
			directionMatrix[column][0] = LEFT;
		}
		for (int row = 0; row < directionMatrix[0].length; row++) {
			directionMatrix[0][row] = UP;
		}
		
		for (int column = 1; column < scoreMatrix.length; column++) {
			for (int row = 1; row < scoreMatrix[0].length; row++) {
				scoreMatrix[column][row] += scoreMatrix[column - 1][row - 1];
				directionMatrix[column][row] = DIAGONAL;
				
				if (scoreMatrix[column - 1][row] > scoreMatrix[column][row]) {
					scoreMatrix[column][row] = scoreMatrix[column - 1][row];
					directionMatrix[column][row] = LEFT;
				}
				
				if (scoreMatrix[column][row - 1] > scoreMatrix[column][row]) {
					scoreMatrix[column][row] = scoreMatrix[column][row - 1];
					directionMatrix[column][row] = UP;
				}
			}
		}

		return new Matrix(directionMatrix, scoreMatrix[columnCountInFirst][columnCountInSecond]);
	}
	
	
	private void createSuperAlignment(ComparedAlignment[] alignments) {
		byte[][] matrix = calculateDirectionMatrix(alignments).directionMatrix;
		int[] unalignedIndex = new int[2];
		@SuppressWarnings("unchecked")
		List<Integer>[] unalignedIndexLists = new List[2];
		for (int i = 0; i < unalignedIndexLists.length; i++) {
			int length = alignments[i].getOriginal().getMaxSequenceLength();
			unalignedIndexLists[i] = new PackedObjectArrayList<Integer>(length + 2, (int)(1.2 * length));  // GAP and OUT_OF_RANGE are additional values.
			unalignedIndex[i] = length - 1;
		}
		
		// Follow back path:
		int column = matrix.length - 1;
		int row = matrix[0].length - 1;
		while ((column > 0) || (row > 0)) {
			if (matrix[column][row] == UP) {
				// Align vertical alignment with horizontal supergap 
				unalignedIndexLists[0].add(Document.GAP_INDEX);
				unalignedIndexLists[1].add(unalignedIndex[1]);
				unalignedIndex[1]--;
				row--;
			}
			else if (matrix[column][row] == LEFT) {
				// Align horizontal alignment with vertical supergap 
				unalignedIndexLists[0].add(unalignedIndex[0]);
				unalignedIndex[0]--;
				unalignedIndexLists[1].add(Document.GAP_INDEX);
				column--;
			}
			else {
				// Align both positions without supergaps
				unalignedIndexLists[0].add(unalignedIndex[0]);
				unalignedIndex[0]--;
				unalignedIndexLists[1].add(unalignedIndex[1]);
				unalignedIndex[1]--;
				row--;
				column--;
			}
		}
		
		// Create superalignment decorators:
		for (int alignmentIndex = 0; alignmentIndex < alignments.length; alignmentIndex++) {
			Collections.reverse(unalignedIndexLists[alignmentIndex]);  //TODO Probably not possible with the packed list.
			alignments[alignmentIndex].createSuperaligned(unalignedIndexLists[alignmentIndex]);
		}
	}
	
	
//	private static void printNode(PhylogenyNode node, String prefix) {
//		System.out.println(prefix + node.getId() + " " + node.getName() + " " + node.getDistanceToParent());
//		for (PhylogenyNode child : node.getDescendants()) {
//			printNode(child, prefix + "  ");
//		}
//	}
	
	
	@Override
	public void performAlignment(Document document) throws Exception {
		if (document.getAlignments().size() > 2) {
			Phylogeny tree = calculateGuideTree(document);
//			printNode(tree.getRoot(), "");
			
			
		}
		else {
			createSuperAlignment(
					new ComparedAlignment[]{document.getAlignments().getValue(0), document.getAlignments().getValue(1)});
		}
	}
}
