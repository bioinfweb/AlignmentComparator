package info.bioinfweb.alignmentcomparator.document;


import java.util.List;

import info.bioinfweb.libralign.model.AlignmentModel;



public class ComparedAlignment {
	private AlignmentModel<Character> original;
	private SuperAlignedModelDecorator superAligned;
	private List<Double> averagePositions = null;
	
	
	public ComparedAlignment(AlignmentModel<Character> original) {
		super();
		this.original = original;
		superAligned = new SuperAlignedModelDecorator(this);  // Original must have been set before.
	}


	public AlignmentModel<Character> getOriginal() {
		return original;
	}
	
	
	public SuperAlignedModelDecorator getSuperAligned() {
		return superAligned;
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
