/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
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
package info.bioinfweb.alignmentcomparator.document.superalignment;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.utils.DegapedIndexCalculator;



public class AverageDegapedPositionAligner implements SuperAlignmentAlgorithm {
	private static final double REMOVE_LATER = -1.0;
	
	
	private Deque<Double> calculateAverageIndices(AlignmentModel<Character> model) {
		DegapedIndexCalculator<Character> calculator = new DegapedIndexCalculator<Character>(model);

		// Save degaped length:
		double[] degapedLengths = new double[model.getSequenceCount()];
		Iterator<String> seqIDIterator = model.sequenceIDIterator();  // This iterator may have a alignment dependent order. That is not problematic, since degapedLengths is only used for the current alignment. 
		int sequenceIndex = 0;
		while (seqIDIterator.hasNext()) {
			String id = seqIDIterator.next();
			degapedLengths[sequenceIndex] = calculator.degapedIndex(id, model.getSequenceLength(id) - 1);
			sequenceIndex++;
		}
		
		// Calculate average indices:
		int alignmentLength = model.getMaxSequenceLength();
		Deque<Double> result = new ArrayDeque<Double>(alignmentLength);
		for (int column = 0; column < alignmentLength; column++) {
			double averageIndex = 0.0;
			seqIDIterator = model.sequenceIDIterator();
			sequenceIndex = 0;
			while (seqIDIterator.hasNext()) {
				String id = seqIDIterator.next();
				averageIndex += (double)calculator.degapedIndex(id, column) / degapedLengths[sequenceIndex];
				sequenceIndex++;
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
	
	
	private Map<String, List<Double>> alignPositions(Map<String, Deque<Double>> unalignedPositions) {
		Map<String, List<Double>> alignedPositions = new TreeMap<String, List<Double>>();
		Iterator<String> iterator = unalignedPositions.keySet().iterator();
		while (iterator.hasNext()) {
			alignedPositions.put(iterator.next(), new ArrayList<Double>());
		}
		
		Double nextPosition = new Double(nextMin(unalignedPositions));
		while (!nextPosition.isNaN()) {
			iterator = unalignedPositions.keySet().iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				Deque<Double> currentQueue = unalignedPositions.get(name); 
				if (nextPosition.equals(currentQueue.peekFirst())) {  // Needs to be done with wrappers to handle currentQueue.peekFirst() == null.
					currentQueue.pollFirst();
					alignedPositions.get(name).add(nextPosition);
				}
				else {
					alignedPositions.get(name).add(Double.NaN);  // Add gap.
				}
			}
			nextPosition = nextMin(unalignedPositions);
		}
		return alignedPositions;
	}
	
	
	private double findPrealignedValue(Map<String, List<Double>> positions, int column) {
		Iterator<String> iterator = positions.keySet().iterator();
		while (iterator.hasNext()) {
			Double result = positions.get(iterator.next()).get(column);
			if (!result.isNaN()) {
				return result;
			}
		}
		throw new InternalError("Empty column found in prealignment.");  // Should not happen.
	}
	
	
	/**
	 * Returns a sorted multimap with position differences of the specified alignment as keys and their column
	 * index in the specified alignment as values.
	 * 
	 * @param alignedPositions the alignment to be compressed 
	 * @return the sorted multimap
	 */
	private SortedSetMultimap<Double, Integer> calculateColumnDistances(Map<String, List<Double>> alignedPositions) {
		int length = alignedPositions.values().iterator().next().size();
		SortedSetMultimap<Double, Integer> result = TreeMultimap.create();
		double current = 0.0;
		double next;
		for (int column = 0; column < length; column++) {
			next = findPrealignedValue(alignedPositions, column);
			result.put(next - current, column);
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
	
	
	private boolean columnsCombinable(Map<String, List<Double>> alignedPositions, int secondColumn) {
		if (secondColumn > 0) {  // First entry contains distance to 0 and cannot be combined.
			for (String name : alignedPositions.keySet()) {
				List<Double> list = alignedPositions.get(name);
				if (!list.get(secondColumn - 1).isNaN() && !list.get(secondColumn).isNaN()) {
					return false;
				}
			}
			return true;
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
		while (distanceIterator.hasNext()) {
			Iterator<Integer> columnIterator = columnDistances.get(distanceIterator.next()).iterator();
			while (columnIterator.hasNext()) {
				int secondColumn = columnIterator.next();
				if (columnsCombinable(alignedPositions, secondColumn)) {
					markTwoColumns(alignedPositions, secondColumn);
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
	public void performAlignment(Document document) throws Exception {
		// Calculate positions:
		Map<String, Deque<Double>> unalignedPositions = new TreeMap<String, Deque<Double>>();
		for (String name : document.getAlignments().keyList()) {
			unalignedPositions.put(name, calculateAverageIndices(document.getAlignments().get(name).getOriginal()));
		}
		
		Map<String, List<Double>> alignedPositions = alignPositions(unalignedPositions);
		compressAlignment(alignedPositions, calculateColumnDistances(alignedPositions));
	
		calculateUnalignedIndices(document, alignedPositions);
		setAveragePositions(document, alignedPositions);
	}
}
