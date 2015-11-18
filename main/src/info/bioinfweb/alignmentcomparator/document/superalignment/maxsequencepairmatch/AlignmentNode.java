package info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch;



public class AlignmentNode {
	private int[] positions = new int[2];
	private AlignmentNode optimalPreviousNode = null;
	private int optimalScore = -1;
	
	
	public AlignmentNode(int[] positions) {
		super();
		this.positions = positions;
	}


	public int[] getPositions() {
		return positions;
	}


	public int getPosition(int index) {
		return positions[index];
	}
	
	
	public void setPosition(int index, int value) {
		positions[index] = value;
	}
	
	
	public boolean equalsToPositions(int[] otherPositions) {
		for (int i = 0; i < positions.length; i++) {
			if (getPosition(i) != otherPositions[i]) {
				return false;
			}
		}
		return true;
	}
	
	
	public AlignmentNode getOptimalPreviousNode() {
		return optimalPreviousNode;
	}


	public void setOptimalPreviousNode(AlignmentNode optimalPreviousNode) {
		this.optimalPreviousNode = optimalPreviousNode;
	}


	public void setOptimalScore(int optimalScore) {
		this.optimalScore = optimalScore;
	}


	public int getOptimalScore() {
		return optimalScore;
	}


	@Override
	public String toString() {
		return "(" + getPosition(0) + ", " + getPosition(1) + "):" + getOptimalScore();  //TODO Refactor method, if more than two indices should be supported in the future.
	}
}
