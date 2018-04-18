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


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.OriginalAlignment;
import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;

import java.text.DecimalFormat;  // Needed for optional output methods.
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;



public class AverageDegapedPositionAligner implements SuperAlignmentAlgorithm {
	private static final double REMOVE = -1.0;
	private static final double REMOVE_OPTION = -2.0;
	
	
	private double calculateUnalignedPosition(OriginalAlignment model, String sequenceID, int alignedIndex) {
		IndexRelation relation = model.getIndexTranslator().getUnalignedIndex(sequenceID, alignedIndex);
		double unalignedPosSum;
		if (relation.getCorresponding() == IndexRelation.GAP) {
			// Calculate positions before the gap:
			int unalignedPosBefore;  // the unaligned position of the first token before the gap (starting with index 1) or 0 if the gap is leading
			int gapStartPos;  // The first position of the gap.
			if (relation.getBefore() == IndexRelation.OUT_OF_RANGE) {
				unalignedPosBefore = 0;
				gapStartPos = 0;
			}
			else {
				unalignedPosBefore = relation.getBefore() + 1;  // + 1, since the position of the first token and the position at the start of the alignment should be different to model leading gaps.
				gapStartPos = model.getIndexTranslator().getAlignedIndex(sequenceID, relation.getBefore()) + 1;  // + 1, since the first position of the gap and not before it is meant here.
				if (gapStartPos == IndexRelation.OUT_OF_RANGE) {
					gapStartPos = model.getSequenceLength(sequenceID);
				}
			}

			// Calculate positions after the gap:
			int unalignedPosAfter;  // the unaligned position of the first token behind the gap (starting with index 1) or (unalignedLength + 1) if the gap is trailing
			int gapEndPos = IndexRelation.OUT_OF_RANGE;  // The first position after the gap.
			if (relation.getAfter() == IndexRelation.OUT_OF_RANGE) {
				unalignedPosAfter = model.getIndexTranslator().getUnalignedLength(sequenceID) + 1;
			}
			else {
				unalignedPosAfter = relation.getAfter() + 1;  // + 1, since the position of the first token and the position at the start of the alignment should be different to model leading gaps.
				gapEndPos = model.getIndexTranslator().getAlignedIndex(sequenceID, relation.getAfter());  // The first unaligned position after the gap is translated to the first aligned position after the gap.
			}
			if (gapEndPos == IndexRelation.OUT_OF_RANGE) {
				gapEndPos = model.getSequenceLength(sequenceID);  // The position behind the alignment.
			}
			
			// Calculate position in the gap:
			double gapLength = gapEndPos - gapStartPos;
			double relGapPosition = (alignedIndex + 0.5 - gapStartPos) / gapLength;  // 0.5 is added since a gap of length 1 should have an index between the indices of both ends and not one equal to its start.
			unalignedPosSum = unalignedPosBefore * (1 - relGapPosition) + unalignedPosAfter * relGapPosition;
		}
		else {  // Aligned index is outside of a gap.
			unalignedPosSum = relation.getCorresponding() + 1;  // + 1, since the position of the first token and the position at the start of the alignment should be different to model leading gaps.
		}
		
		return unalignedPosSum;
		
		// The following alternative was used to calculate relative indices. This is currently not done, since the same are 
		// may be differently superaligned if previous columns are removed from both alignments, due to the possibly different 
		// step length in sequences with different unaligned length.
		// This method may still have advantages for some use cases, which could be further explored in the future. 
		//return unalignedPosSum / (double)(model.getIndexTranslator().getUnalignedLength(sequenceID) + 1);  // + 1 since the positions before and after the alignment are also modeled.
	}
	
	
	private Deque<Double> calculateAveragePositions(OriginalAlignment model) {
		int alignmentLength = model.getMaxSequenceLength();
		Deque<Double> result = new ArrayDeque<Double>(alignmentLength);
		for (int column = 0; column < alignmentLength; column++) {
			//System.out.print("Column " + column + ": ");
			double positionSum = 0.0;
			Iterator<String> seqIDIterator = model.sequenceIDIterator();
			while (seqIDIterator.hasNext()) {
				double value = calculateUnalignedPosition(model, seqIDIterator.next(), column);
				//System.out.print(value + " ");
				positionSum += value;
			}
			result.add(positionSum / model.getSequenceCount());
			//System.out.println();
		}
		return result;
	}
	
	
	private double nextMin(Map<String, Deque<Double>> unalignedPositions) {
		double result = Double.POSITIVE_INFINITY;
		Iterator<String> iterator = unalignedPositions.keySet().iterator();
		while (iterator.hasNext()) {
			Double value = unalignedPositions.get(iterator.next()).peekFirst();
			if (value != null) {
				result = Math.min(result, value);
			}
		}
		if (result == Double.POSITIVE_INFINITY) {  // All queues are empty.
			result = Double.NaN;
		}
		return result;
	}
	
	
	private Map<String, List<Double>> superalignPositions(Map<String, Deque<Double>> averageUnalignedPositions) {
		// Create empty lists:
		Map<String, List<Double>> result = new TreeMap<String, List<Double>>();
		Iterator<String> iterator = averageUnalignedPositions.keySet().iterator();
		while (iterator.hasNext()) {
			result.put(iterator.next(), new ArrayList<Double>());
		}
		
		// Calculate initial superalignment:
		Double nextPosition = new Double(nextMin(averageUnalignedPositions));  // Take next minimal position from queue.
		while (!nextPosition.isNaN()) {  // while queues are not empty
			// Align current minimum with other equal indices or with supergaps:
			iterator = averageUnalignedPositions.keySet().iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				Deque<Double> currentQueue = averageUnalignedPositions.get(name); 
				if (nextPosition.equals(currentQueue.peekFirst())) {  // Needs to be done with wrappers to handle currentQueue.peekFirst() == null.
					currentQueue.pollFirst();
					result.get(name).add(nextPosition);
				}
				else {
					result.get(name).add(Double.NaN);  // Add gap.
				}
			}
			
			// Obtain next minimum from queues:
			nextPosition = nextMin(averageUnalignedPositions);
		}
		return result;
	}
	
	
	/**
	 * Returns the first average position in the specified column that is not a supergap ({@link Double#isNaN()}).
	 * <p>
	 * Note that all average indices in one column at this stage are equal, therefore the returned average position
	 * if equal to all non-gap entries for the specified column. 
	 * 
	 * @param positions the positions to searched
	 * @param column the column to be searched
	 * @return the average index of this column
	 */
	private double findPrealignedValue(Map<String, List<Double>> positions, int column) {
		Iterator<String> iterator = positions.keySet().iterator();
		while (iterator.hasNext()) {
			Double result = positions.get(iterator.next()).get(column);
			if (!result.isNaN() && (result != REMOVE)) {
				return result;
			}
		}
		throw new InternalError("Empty column found in prealignment.");  // Should not happen, since all columns should contain at least one value.
	}
	
	
	/**
	 * Returns a sorted multimap with position differences of the specified alignment as keys and their column
	 * index in the specified alignment as values.
	 * 
	 * @param alignedPositions the alignment to be compressed 
	 * @return the sorted multimap
	 */
	private SortedSetMultimap<Double, Integer> calculateColumnDistances(Map<String, List<Double>> alignedPositions) {
		// As an alternative to a sorted multimap, this implementation could also just return a sorted list. In this case the map 
		// must either be converted in the end or a sorting algorithm would have to be applied to sort a list of pairs.
		
		int length = alignedPositions.values().iterator().next().size();  // All lists should have equal lengths.
		SortedSetMultimap<Double, Integer> result = TreeMultimap.create();
		double current = 0.0;
		double next;
		//System.out.print("Column distances: ");
		for (int column = 0; column < length; column++) {
			next = findPrealignedValue(alignedPositions, column);  // Returns the average position stored in this column.
			result.put(next - current, column);  // Calculate position to the left neighbor.
			//System.out.print(new DecimalFormat("0.00").format(next - current) + " ");
			current = next;
		}
		//System.out.println();
		
		//System.out.print("Order: ");
//		for (Double distance : result.keySet()) {
//			System.out.print(new DecimalFormat("0.00").format(distance) + ": [");
//			for (Integer column : result.get(distance)) {
//				System.out.print(column + " ");
//			}
//			System.out.print("] ");
//		}
//		System.out.println();
		
		
		return result;
	}
	
	
	private void calculateUnalignedIndices(Document document, Map<String, List<Double>> alignedPositions) {
		for (String name : document.getAlignments().keyList()) {
			List<Integer> unalignedIndices = new ArrayList<Integer>(alignedPositions.get(name).size());
			int unalignedIndex = 0;
			for (Double position : alignedPositions.get(name)) {
				if (position.isNaN()) {
					unalignedIndices.add(Document.GAP_INDEX);
				}
				else {
					unalignedIndices.add(unalignedIndex);
					unalignedIndex++;
				}
			}
			document.getAlignments().get(name).createSuperaligned(unalignedIndices);
		}
	}
	
	
	private boolean isCellDeletable(Double value) {
		return value.isNaN() || (value == REMOVE_OPTION);  // Average indices and REMOVE markings cannot be deleted.
	}
	
	
	/**
	 * Checks if the specified column could be combined with its left neighbor.
	 * <p>
	 * Two columns are not combinable if positions in both columns of the same sequence (of average indices 
	 * of one alignment to be compared) are occupied either by an average index or a marking to remove this
	 * position (currently a supergap) later. Only if at least one column for each sequence contains a 
	 * supergap, combining both columns is possible. 
	 * 
	 * @param alignedPositions the position lists
	 * @param column the column to be checked for combination with its left neighbor
	 * @return {@code true} if the columns can be combined, {@code false} otherwise
	 */
	private boolean columnsCombinable(Map<String, List<Double>> alignedPositions, int column) {
		if (column > 0) {  // First entry contains distance to 0 and cannot be combined.
			for (String name : alignedPositions.keySet()) {
				List<Double> list = alignedPositions.get(name);
				if (!isCellDeletable(list.get(column - 1)) && !isCellDeletable(list.get(column))) {  // Check if at least one position could be deleted.
					return false;
				}
			}
			return true;  // Return true only of no pairs of non-deletable entries were found.
		}
		else {
			return false;
		}
	}
	
	
	private void removeOuterOptionMarking(List<Double> list, int pos, int direction) {
		while (list.get(pos) != REMOVE_OPTION) {  // No bound check is performed, since a remove option must appear before the end of the list. If an IndexOutOfBoundsException happens here, the contents of the list were incorrect.
			pos += direction;
		}
		list.set(pos, REMOVE);
	}
	
	
	private void markTwoColumns(Map<String, List<Double>> alignedPositions, int secondColumn) {
		if (secondColumn > 0) {  // First entry contains distance to 0 and cannot be combined.
			for (String name : alignedPositions.keySet()) {
				List<Double> list = alignedPositions.get(name);
				if (list.get(secondColumn - 1).isNaN()) {  // -
					if (list.get(secondColumn).isNaN()) {  // -- -> OO
						list.set(secondColumn - 1, REMOVE_OPTION);
						list.set(secondColumn, REMOVE_OPTION);
					}
					else if (list.get(secondColumn) == REMOVE_OPTION) {  // -O -> OR
						list.set(secondColumn - 1, REMOVE_OPTION);
						list.set(secondColumn, REMOVE);
					}
					else {  // -R -> RR or -Z -> RZ
						list.set(secondColumn - 1, REMOVE);
					}
				}
				else if (list.get(secondColumn - 1) == REMOVE_OPTION) {  // O
					if (list.get(secondColumn).isNaN()) {  // O- -> RO
						list.set(secondColumn - 1, REMOVE);
						list.set(secondColumn, REMOVE_OPTION);
					}
					else if (list.get(secondColumn) == REMOVE_OPTION) {  // OO -> RR (Both neighboring pairs are two remove options.)
						list.set(secondColumn - 1, REMOVE);
						list.set(secondColumn, REMOVE);
					}
					else {  // O(R*)OR -> -$1RR or O(R*)OZ -> -$1RZ (The other rules do not allow anything else then R to be between Os. Therefore no additional checks are necessary.)
						list.set(secondColumn - 1, REMOVE);
						removeOuterOptionMarking(list, secondColumn - 2, -1);  // Replace further left O by -.
					}
				}
				else {  // R or Z
					if (list.get(secondColumn).isNaN()) {  // R- -> RR or Z- -> ZR
						list.set(secondColumn, REMOVE);
					}
					else if (list.get(secondColumn) == REMOVE_OPTION) {  // RO(R*)O -> RR$1- or ZO(R*)O -> ZR$1- (The other rules do not allow anything else then R to be between Os. Therefore no additional checks are necessary.)
						list.set(secondColumn, REMOVE);
						removeOuterOptionMarking(list, secondColumn + 1, 1);  // Replace further right O by -.
					}
					// The else case would RR, RZ, ZR or ZZ which all should not occur when this method was called, because the column would not be combinable.
				}
			}
		}
	}
	
	
	/**
	 * Replaces possible remaining remove options.
	 * 
	 * @param alignedPositions
	 */
	private void processRemoveOptions(Map<String, List<Double>> alignedPositions) {
		for (String name : alignedPositions.keySet()) {
			List<Double> list = alignedPositions.get(name);
			boolean isLeftOption = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == REMOVE_OPTION) {
					if (isLeftOption) {
						list.set(i, REMOVE);  // Replace left option by REMOVE marking.
					}
					else {
						list.set(i, Double.NaN);  // Replace right option by gap.
					}
					isLeftOption = !isLeftOption;
				}
			}
		}
	}
	
	
	private void removeMarkedCells(Map<String, List<Double>> alignedPositions) {
		for (String name : alignedPositions.keySet()) {
			Iterator<Double> iterator = alignedPositions.get(name).iterator();
			while (iterator.hasNext()) {
				if (iterator.next() == REMOVE) {
					iterator.remove();
				}
			}
		}
	}
	
	
	private void shortenAlignment(Map<String, List<Double>> alignedPositions, SortedSetMultimap<Double, Integer> columnDistances) {
		Iterator<Double> distanceIterator = columnDistances.keySet().iterator();  // keys() would instead return keys with multiple values multiple times.
		while (distanceIterator.hasNext()) {  // Iterate over all existing distances between neighboring columns, starting with the shortest and then increasing.
			Iterator<Integer> columnIterator = columnDistances.get(distanceIterator.next()).iterator();
			while (columnIterator.hasNext()) {  // Iterate over all columns that have the current distance to their left neighbor. 
				int secondColumn = columnIterator.next();
				if (columnsCombinable(alignedPositions, secondColumn)) {  // Check if the current column can be aligned with its left neighbor.
					markTwoColumns(alignedPositions, secondColumn);  // Mark all supergaps in this and the neighboring column for removal.
				}
			}
		}

		//printSuperAlignment(alignedPositions);
		processRemoveOptions(alignedPositions);
		removeMarkedCells(alignedPositions);
	}
	
	
	private void setAveragePositions(Document document, Map<String, List<Double>> alignedPositions) {
		for(String name : document.getAlignments().keySet()) {
			document.getAlignments().get(name).setAveragePositions(alignedPositions.get(name));
		}
	}
	
	
	private void printSuperAlignment(Map<String, List<Double>> alignment) {
		DecimalFormat format = new DecimalFormat("0.00000");
		for (String name : alignment.keySet()) {
			System.out.print(name);
			for (Double position : alignment.get(name)) {
				System.out.print("\t");
				if (position.isNaN()) {
					System.out.print("-");
				}
				else if (position == REMOVE) {
					System.out.print("R");
				}
				else if (position == REMOVE_OPTION) {
					System.out.print("O");
				}
				else {
					System.out.print(format.format(position));
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	@Override
	public void performAlignment(Document document, ApplicationLogger logger) throws Exception {
		// Calculate average positions:
		Map<String, Deque<Double>> averageUnalignedPositions = new TreeMap<String, Deque<Double>>();
		for (String name : document.getAlignments().keyList()) {
			averageUnalignedPositions.put(name, calculateAveragePositions(document.getAlignments().get(name).getOriginal()));
		}
		
		// Calculate superalignment:
		Map<String, List<Double>> superalignedUnalignedPositions = superalignPositions(averageUnalignedPositions);
		//printSuperAlignment(superalignedUnalignedPositions);
		shortenAlignment(superalignedUnalignedPositions, calculateColumnDistances(superalignedUnalignedPositions));
		//printSuperAlignment(superalignedUnalignedPositions);
	
		// Apply superalignment to model:
		calculateUnalignedIndices(document, superalignedUnalignedPositions);
		setAveragePositions(document, superalignedUnalignedPositions);
	}
}
