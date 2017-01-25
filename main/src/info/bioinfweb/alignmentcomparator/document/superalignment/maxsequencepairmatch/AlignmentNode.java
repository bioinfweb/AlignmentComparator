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
