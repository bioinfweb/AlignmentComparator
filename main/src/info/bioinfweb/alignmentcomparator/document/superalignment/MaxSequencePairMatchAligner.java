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
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.TranslatableAlignment;
import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.commons.log.ApplicationLogger;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	
	private Phylogeny calculateGuideTree(Document document, ApplicationLogger logger) {
		long maxScore = calculateMaxScore(document);
		logger.addMessage("Maximum possible score is " + maxScore + ".");
		logger.addMessage("Calculating pairwise distances...");
		ListOrderedMap<String, ComparedAlignment> alignments = document.getAlignments();
		BasicSymmetricalDistanceMatrix matrix = new BasicSymmetricalDistanceMatrix(alignments.size());
		for (int column = 0; column < alignments.size(); column++) {
			matrix.setIdentifier(column, alignments.get(column));
			for (int row = column + 1; row < alignments.size(); row++) {
				matrix.setValue(column, row, maxScore - calculateDirectionMatrix(Arrays.asList(alignments.getValue(column).getOriginal()), 
						Arrays.asList(alignments.getValue(row).getOriginal())).score);
				logger.addMessage(matrix.getValue(column, row) + " is the distance between " + alignments.getValue(column).getName() + " and " + alignments.getValue(row).getName() + ".");
				//TODO Path could already be extracted from matrix here for later reuse or using memory for matrix could be avoided here, when it's not used.
			}
		}
		logger.addMessage("Calculating pairwise distances done.");
		
		logger.addMessage("Calculating NJ guide tree...");
		Phylogeny result = NeighborJoining.createInstance().execute(matrix);
		reroot(result);
		logger.addMessage("Calculating guide tree done.");
		return result;
	}
	
	
	private void printScoreMatrix(long[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.print(String.format("% 4d", matrix[i][j]));
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	private Matrix calculateDirectionMatrix(List<? extends TranslatableAlignment> groupA, List<? extends TranslatableAlignment> groupB) {
		//TODO Refactor to use only one row of the score matrix at a time in the future.
		
		final int columnCountA = groupA.get(0).getMaxSequenceLength();  // All alignments in one group should have the same length, since they are already superaligned.
		final int columnCountB = groupB.get(0).getMaxSequenceLength();  // All alignments in one group should have the same length, since they are already superaligned.
		final long[][] scoreMatrix = new long[columnCountA + 1][columnCountB + 1];  // Values are initialized with 0.
		
		// Write local scores into matrix:
		for (int columnInFirst = 0; columnInFirst < columnCountA; columnInFirst++) {
			for (TranslatableAlignment alignmentA : groupA) {
				Iterator<String> iterator = alignmentA.sequenceIDIterator();
				while (iterator.hasNext()) {
					String seqIDInFirst = iterator.next();
					char tokenA = alignmentA.getTokenAt(seqIDInFirst, columnInFirst);
					if (!alignmentA.getTokenSet().isGapToken(tokenA) && (tokenA != SuperalignedModelDecorator.SUPER_ALIGNMENT_GAP)) {
						String seqName = alignmentA.sequenceNameByID(seqIDInFirst);
						int unalignedIndex = alignmentA.getIndexTranslator().getUnalignedIndex(seqIDInFirst, columnInFirst).getCorresponding();
						if (unalignedIndex >= 0) {  // This should never happen, because columnInFirst should be in range and the token is not a gap.
							for (TranslatableAlignment alignmentB : groupB) {
								scoreMatrix[columnInFirst + 1][alignmentB.getIndexTranslator().getAlignedIndex(
										alignmentB.sequenceIDsByName(seqName).iterator().next(), unalignedIndex) + 1]++;
										// + 1 because the first column and row in the matrix refer to the position before the first alignment column.
							}
						}
						else {
							throw new InternalError("Unexpected error: Index (" + columnInFirst + " -> " + unalignedIndex + 
									") was at a gap or out of range when mapping sequence " + seqName + " in " + alignmentA +  
									". Contact support@bioinfweb.info if you see this message.");
						}
					}
				}
			}
		}
		
		// Calculate global scores and directions using DP:
		byte[][] directionMatrix = new byte[columnCountA + 1][columnCountB + 1];
		for (int column = 0; column < directionMatrix.length; column++) {
			directionMatrix[column][0] = LEFT;
		}
		for (int row = 0; row < directionMatrix[0].length; row++) {
			directionMatrix[0][row] = UP;
		}
		
		for (int column = 1; column < scoreMatrix.length; column++) {
			for (int row = 1; row < scoreMatrix[0].length; row++) {
				scoreMatrix[column][row] += scoreMatrix[column - 1][row - 1];  // Column match score only counts for diagonal moves. 
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
		
		//printScoreMatrix(scoreMatrix);
		return new Matrix(directionMatrix, scoreMatrix[columnCountA][columnCountB]);
	}

	
	private void applySuperalignment(List<? extends TranslatableAlignment> group, List<Integer> unalignedIndexList) {
		Collections.reverse(unalignedIndexList);
		for (TranslatableAlignment alignment : group) {
			if (alignment instanceof OriginalAlignment) {
				((OriginalAlignment)alignment).getOwner().createSuperaligned(unalignedIndexList);
			}
			else {  // SuperalignedModelDecorator
				SuperalignedModelDecorator decorator = (SuperalignedModelDecorator)alignment;
				decorator.getIndexTranslator().setAutoUpdateMapping(false);
				try {
					for (int i = 0; i < unalignedIndexList.size(); i++) {
						if (unalignedIndexList.get(i) == SuperalignedModelDecorator.SUPER_GAP_INDEX) {
							decorator.insertSupergap(i, 1);
						}
					}
				}
				finally {
					decorator.getIndexTranslator().setAutoUpdateMapping(true);  // Will also perform update if necessary.
				}
			}
		}
	}
	
	
	private long createSuperAlignment(List<? extends TranslatableAlignment> groupA, List<? extends TranslatableAlignment> groupB) {
		MaxSequencePairMatchAligner.Matrix m = calculateDirectionMatrix(groupA, groupB);
		byte[][] matrix = m.directionMatrix;

		int length = groupA.get(0).getMaxSequenceLength();
		List<Integer> unalignedIndexListA = new PackedObjectArrayList<Integer>(length + 2, (int)(1.2 * length));  // GAP and OUT_OF_RANGE are additional values.
		int unalignedIndexA = length - 1;
		
		length = groupB.get(0).getMaxSequenceLength();
		List<Integer> unalignedIndexListB = new PackedObjectArrayList<Integer>(length + 2, (int)(1.2 * length));  // GAP and OUT_OF_RANGE are additional values.
		int unalignedIndexB = length - 1;
		
		// Follow back path:
		int column = matrix.length - 1;
		int row = matrix[0].length - 1;
		while ((column > 0) || (row > 0)) {
			if (matrix[column][row] == UP) {
				// Align vertical alignment with horizontal supergap 
				unalignedIndexListA.add(Document.GAP_INDEX);
				unalignedIndexListB.add(unalignedIndexB);
				unalignedIndexB--;
				row--;
			}
			else if (matrix[column][row] == LEFT) {
				// Align horizontal alignment with vertical supergap 
				unalignedIndexListA.add(unalignedIndexA);
				unalignedIndexA--;
				unalignedIndexListB.add(Document.GAP_INDEX);
				column--;
			}
			else {
				// Align both positions without supergaps
				unalignedIndexListA.add(unalignedIndexA);
				unalignedIndexA--;
				unalignedIndexListB.add(unalignedIndexB);
				unalignedIndexB--;
				row--;
				column--;
			}
		}
		
		// Store superalignment:
		applySuperalignment(groupA, unalignedIndexListA);
		applySuperalignment(groupB, unalignedIndexListB);
		
		return m.score;
	}
	
	
	private void addAlignmentGroup(List<? extends TranslatableAlignment> group, List<TranslatableAlignment> target) {
		for (TranslatableAlignment alignment : group) {
			if (alignment instanceof OriginalAlignment) {
				alignment = ((OriginalAlignment)alignment).getOwner().getSuperaligned();
			}
			target.add(alignment);
		}
	}
	
	
	private CharSequence groupToString(List<? extends TranslatableAlignment> group) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		Iterator<? extends TranslatableAlignment> iterator = group.iterator();
		while (iterator.hasNext()) {
			result.append(iterator.next().getOwner().getName());
			if (iterator.hasNext()) {
				result.append(", ");
			}
		}
		result.append("]");
		return result;
	}
	
	
	private List<? extends TranslatableAlignment> processGuideTree(Document document, PhylogenyNode node, ApplicationLogger logger) {
		if (node.getDescendants().isEmpty()) {  // Terminal node. No alignment to perform.
			return Arrays.asList(document.getAlignments().get(node.getName()).getOriginal());
		}
		else {
			List<? extends TranslatableAlignment> groupA = processGuideTree(document, node.getChildNode(0), logger);
			List<? extends TranslatableAlignment> groupB = processGuideTree(document, node.getChildNode(1), logger);
			
			logger.addMessage("Superaligning " + groupToString(groupA) + " and " + groupToString(groupB) + "...");
			long score = createSuperAlignment(groupA, groupB);
			logger.addMessage("Superalignment done with score " + score + ".");
			
			List<TranslatableAlignment> result = new ArrayList<>(groupA.size() + groupB.size());
			addAlignmentGroup(groupA, result);
			addAlignmentGroup(groupB, result);
			return result;
		}
	}
	
	
	private static void printNode(PhylogenyNode node, String prefix) {
		System.out.println(prefix + node.getId() + " " + node.getName() + " " + node.getDistanceToParent());
		for (PhylogenyNode child : node.getDescendants()) {
			printNode(child, prefix + "  ");
		}
	}
	
	
	@Override
	public void performAlignment(Document document, ApplicationLogger logger) throws Exception {
		if (document.getAlignments().size() > 2) {
			Phylogeny tree = calculateGuideTree(document, logger);
			//printNode(tree.getRoot(), "");
			processGuideTree(document, tree.getRoot(), logger);
		}
		else {
			logger.addMessage("Starting paiwise superalignment...");
			long score = createSuperAlignment(
					Arrays.asList(document.getAlignments().getValue(1).getOriginal()),
					Arrays.asList(document.getAlignments().getValue(0).getOriginal()));
			logger.addMessage("Superalignment done with score " + score + ".");
		}
		logger.addMessage("Finished.");
	}
}
