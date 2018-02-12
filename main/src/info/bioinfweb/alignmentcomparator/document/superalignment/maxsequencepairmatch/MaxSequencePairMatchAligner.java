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
package info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch;


import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.utils.indextranslation.RandomAccessIndexTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



public class MaxSequencePairMatchAligner implements SuperAlignmentAlgorithm {
	private void createSuperAlignment(ComparedAlignment[] alignments) {
		final AlignmentModel<Character> firstAlignment = alignments[0].getOriginal();
		final AlignmentModel<Character> secondAlignment = alignments[1].getOriginal();
		final int columnCountInFirst = firstAlignment.getMaxSequenceLength();
		final int columnCountInSecond = secondAlignment.getMaxSequenceLength();
		final MaxSeqPairMatchGraph graph = new MaxSeqPairMatchGraph(columnCountInFirst, columnCountInSecond);
		
		// Create score nodes:
		@SuppressWarnings("unchecked")
		RandomAccessIndexTranslator<Character>[] calculators = new RandomAccessIndexTranslator[2];
		for (int i = 0; i < calculators.length; i++) {
			calculators[i] = new RandomAccessIndexTranslator<Character>(alignments[i].getOriginal());
		}

		
		for (int columnInFirst = 0; columnInFirst < columnCountInFirst; columnInFirst++) {
			Iterator<String> iterator = firstAlignment.sequenceIDIterator();
			while (iterator.hasNext()) {
				String seqIDInFirst = iterator.next();
				if (!firstAlignment.getTokenSet().isGapToken(firstAlignment.getTokenAt(seqIDInFirst, columnInFirst))) {
					String seqName = firstAlignment.sequenceNameByID(seqIDInFirst);
					String seqIDInSecond = secondAlignment.sequenceIDsByName(seqName).iterator().next();
					
					int unalignedIndex = calculators[0].getUnalignedIndex(seqIDInFirst, columnInFirst).getCorresponding();
					if (unalignedIndex >= 0) {
						int columnInSecond = calculators[1].getAlignedIndex(seqIDInSecond, unalignedIndex);
						int[] columnPair = new int[]{columnInFirst, columnInSecond};
						MaxSeqPairMatchNode node = graph.get(columnPair);
						if (node == null) {
							node = new MaxSeqPairMatchNode(columnPair);
							graph.add(node);
						}
						node.increaseScore();
					}
					else {
						throw new InternalError("Unexpected error: Index (" + columnInFirst + " -> " + unalignedIndex + 
								") was at a gap or out of range. Contact support@bioinfweb.info if you see this message.");
					}
				}
			}
		}
		
		// Calculate scores and connect nodes:
		for (int[] columnPair : graph.keySet()) {
			MaxSeqPairMatchNode currentNode = graph.get(columnPair);
			
			// Calculate score:
			MaxSeqPairMatchNode optimalNode = currentNode.optimalPreviousNode();
			if (optimalNode != null) {
				currentNode.setScore(currentNode.getScore() + optimalNode.getScore());
			}
			
			// Connect node:
			MaxSeqPairMatchNode nextNode = graph.rightSeqPairValue(columnPair);
			if (nextNode != null) {
				int nextColumn = nextNode.getPosition(0);
				do {
					nextNode.getPreviousNodes().add(currentNode);
					nextNode = graph.higherValue(nextNode.getPositions());
				} while ((nextNode != null) && (nextNode.getPosition(0) == nextColumn));
			}
		}
		
		// Extract optimal path:
		List<MaxSeqPairMatchNode> optimalPath = new LinkedList<>();
		MaxSeqPairMatchNode optimalNode = graph.getEndNode();
		while (optimalNode != null) {
			optimalPath.add(0, optimalNode);
			optimalNode = optimalNode.optimalPreviousNode();  //TODO Speed up implementation by avoiding to call this method twice (here and above).
		}
		
		// Create superalignment:
		List<Integer> firstUnalignedIndices = new ArrayList<>(columnCountInFirst);  //TODO Possibly use factor for initial size
		List<Integer> secondUnalignedIndices = new ArrayList<>(columnCountInFirst);  //TODO Possibly use factor for initial size
		int currentColumnInFirst = 0;
		int currentColumnInSecond = 0;
		for (MaxSeqPairMatchNode node : optimalPath) {
			while (currentColumnInFirst < node.getPosition(0)) {
				firstUnalignedIndices.add(currentColumnInFirst++);
			}
			while (currentColumnInSecond < node.getPosition(1)) {
				secondUnalignedIndices.add(currentColumnInSecond++);
			}
			while (firstUnalignedIndices.size() < secondUnalignedIndices.size()) {
				firstUnalignedIndices.add(SuperalignedModelDecorator.SUPER_GAP_INDEX);
			}
			while (secondUnalignedIndices.size() < firstUnalignedIndices.size()) {
				secondUnalignedIndices.add(SuperalignedModelDecorator.SUPER_GAP_INDEX);
			}
		}
		alignments[0].createSuperaligned(firstUnalignedIndices);
		alignments[1].createSuperaligned(secondUnalignedIndices);
	}
	
	
	@Override
	public void performAlignment(Document document) throws Exception {
		if (document.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			createSuperAlignment(
					new ComparedAlignment[]{document.getAlignments().getValue(0), document.getAlignments().getValue(1)});
		}
	}
}
