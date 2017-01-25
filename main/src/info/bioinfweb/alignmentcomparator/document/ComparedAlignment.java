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
package info.bioinfweb.alignmentcomparator.document;


import java.util.List;

import info.bioinfweb.libralign.model.AlignmentModel;



public class ComparedAlignment {
	private AlignmentModel<Character> original;
	private SuperalignedModelDecorator superAligned = null;
	private List<Double> averagePositions = null;
	
	
	public ComparedAlignment(AlignmentModel<Character> original) {
		super();
		this.original = original;
	}


	public AlignmentModel<Character> getOriginal() {
		return original;
	}
	
	
	public SuperalignedModelDecorator getSuperaligned() {
		return superAligned;
	}
	
	
	public void createSuperaligned(List<Integer> unalignedIndices) {
		superAligned = new SuperalignedModelDecorator(this, unalignedIndices);  // Original must have been set before.
	}
	
	
	public boolean hasSuperaligned() {
		return superAligned != null;
	}
	
	
	public boolean hasAveragePositions() {
		return averagePositions != null;
	}


	public List<Double> getAveragePositions() {
		return averagePositions;
	}


	public void setAveragePositions(List<Double> averagePositions) {
		if ((averagePositions != null) &&  (averagePositions.size() != superAligned.getMaxSequenceLength())) {
			throw new IllegalArgumentException("The number of position indices (" + averagePositions.size() + 
					") must be equal to superalignment length (" + superAligned.getMaxSequenceLength() + ").");
		}
		else {
			this.averagePositions = averagePositions;
		}
	}
}
