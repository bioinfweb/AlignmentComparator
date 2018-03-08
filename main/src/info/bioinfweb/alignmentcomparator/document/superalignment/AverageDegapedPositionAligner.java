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
	private static final double REMOVE_LATER = -1.0;
	
	
	private double calculateRelativeIndex(OriginalAlignment model, String sequenceID, int alignedIndex) {
		IndexRelation relation = model.getIndexTranslator().getUnalignedIndex(sequenceID, alignedIndex);
		double unalignedIndex;
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
			double gapCenterPos = alignedIndex + 0.5;  // A gap of length 1 should have an index between the indices of both ends and not one equal to its start.
			unalignedIndex =
					unalignedPosBefore * (gapCenterPos - gapStartPos) / gapLength +  // The position before the gap weighted by the distance of the current position to the start of the gap relative to the gap length. 
					unalignedPosAfter * (gapEndPos - gapCenterPos) / gapLength;  // The position after the gap weighted by the distance of the current position to the end of the gap relative to the gap length.
		}
		else {  // Aligned index is outside of a gap.
			unalignedIndex = relation.getCorresponding() + 1;  // + 1, since the position of the first token and the position at the start of the alignment should be different to model leading gaps.
		}
		
		
		return unalignedIndex / (double)(model.getIndexTranslator().getUnalignedLength(sequenceID) + 1);  // + 1 since the positions before and after the alignment are also modeled.
	}
	
	
	private Deque<Double> calculateAverageIndices(OriginalAlignment model) {
		int alignmentLength = model.getMaxSequenceLength();
		Deque<Double> result = new ArrayDeque<Double>(alignmentLength);
		for (int column = 0; column < alignmentLength; column++) {
			double averageIndex = 0.0;
			Iterator<String> seqIDIterator = model.sequenceIDIterator();
			while (seqIDIterator.hasNext()) {
				averageIndex += calculateRelativeIndex(model, seqIDIterator.next(), column);
			}
			result.add(averageIndex / model.getSequenceCount());
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
		Map<String, List<Double>> averageAlignedPositions = new TreeMap<String, List<Double>>();
		Iterator<String> iterator = averageUnalignedPositions.keySet().iterator();
		while (iterator.hasNext()) {
			averageAlignedPositions.put(iterator.next(), new ArrayList<Double>());
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
					averageAlignedPositions.get(name).add(nextPosition);
				}
				else {
					averageAlignedPositions.get(name).add(Double.NaN);  // Add gap.
				}
			}
			
			// Obtain next minimum from queues:
			nextPosition = nextMin(averageUnalignedPositions);
		}
		return averageAlignedPositions;
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
			if (!result.isNaN()) {
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
		int length = alignedPositions.values().iterator().next().size();  // All lists should have equal lengths.
		SortedSetMultimap<Double, Integer> result = TreeMultimap.create();
		double current = 0.0;
		double next;
		for (int column = 0; column < length; column++) {
			next = findPrealignedValue(alignedPositions, column);  // Returns the average position stored in this column.
			result.put(next - current, column);  // Calculate position to the left neighbor.
			current = next;
		}
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
				if (!list.get(column - 1).isNaN() && !list.get(column).isNaN()) {  // Check if two neighboring positions are both not gaps. (This includes possible marking to remove this position later.)
					return false;
				}
			}
			return true;  // Return true only of no pairs of non-gap entries were found.
		}
		else {
			return false;
		}
	}
	
	
	private void markTwoColumns(Map<String, List<Double>> alignedPositions, int secondColumn) {
		if (secondColumn > 0) {  // First entry contains distance to 0 and cannot be combined.
			for (String name : alignedPositions.keySet()) {
				List<Double> list = alignedPositions.get(name);
				if (list.get(secondColumn).isNaN()) {  // Mark for later removal to maintain indices:
					list.set(secondColumn, REMOVE_LATER);
				}
				else {
					list.set(secondColumn - 1, REMOVE_LATER);
				}
			}
		}
	}
	
	
	private void removeMarkedCells(Map<String, List<Double>> alignedPositions) {
		for (String name : alignedPositions.keySet()) {
			Iterator<Double> iterator = alignedPositions.get(name).iterator();
			while (iterator.hasNext()) {
				if (iterator.next() == REMOVE_LATER) {
					iterator.remove();
				}
			}
		}
	}
	
	
	private void compressAlignment(Map<String, List<Double>> alignedPositions, SortedSetMultimap<Double, Integer> columnDistances) {
		Iterator<Double> distanceIterator = columnDistances.keys().iterator();
		while (distanceIterator.hasNext()) {  // Iterate over all existing distances between neighboring columns, starting with the shortest and then increasing.
			Iterator<Integer> columnIterator = columnDistances.get(distanceIterator.next()).iterator();
			while (columnIterator.hasNext()) {  // Iterate over all columns that have the current distance to their left neighbor. 
				int secondColumn = columnIterator.next();
				if (columnsCombinable(alignedPositions, secondColumn)) {  // Check if the current column can be aligned with its left neighbor.
					markTwoColumns(alignedPositions, secondColumn);  // Mark all supergaps in this and the neighboring column for removal.
				}
			}
		}

		removeMarkedCells(alignedPositions);
	}
	
	
	private void setAveragePositions(Document document, Map<String, List<Double>> alignedPositions) {
		for(String name : document.getAlignments().keySet()) {
			document.getAlignments().get(name).setAveragePositions(alignedPositions.get(name));
		}
	}
	
	
	@Override
	public void performAlignment(Document document, ApplicationLogger logger) throws Exception {
		// Calculate average positions:
		Map<String, Deque<Double>> averageUnalignedPositions = new TreeMap<String, Deque<Double>>();
		for (String name : document.getAlignments().keyList()) {
			averageUnalignedPositions.put(name, calculateAverageIndices(document.getAlignments().get(name).getOriginal()));
		}
		
		// Calculate superalignment:
		Map<String, List<Double>> averageAlignedPositions = superalignPositions(averageUnalignedPositions);
		compressAlignment(averageAlignedPositions, calculateColumnDistances(averageAlignedPositions));
	
		// Apply superalignment to model:
		calculateUnalignedIndices(document, averageAlignedPositions);
		setAveragePositions(document, averageAlignedPositions);
	}
}
