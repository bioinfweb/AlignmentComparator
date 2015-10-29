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
