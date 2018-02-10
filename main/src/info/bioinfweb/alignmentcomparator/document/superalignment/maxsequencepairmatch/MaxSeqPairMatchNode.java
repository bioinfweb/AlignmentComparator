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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MaxSeqPairMatchNode {
	private int[] positions = new int[2];
	private List<MaxSeqPairMatchNode> previousNodes = new ArrayList<>();
	private long score = -1;
	
	
	public MaxSeqPairMatchNode(int[] positions) {
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
	
	
	public List<MaxSeqPairMatchNode> getPreviousNodes() {
		return previousNodes;
	}

	
	public MaxSeqPairMatchNode optimalPreviousNode() {
		MaxSeqPairMatchNode optimalNode = null;
		if (!previousNodes.isEmpty()) {
			Iterator<MaxSeqPairMatchNode> iterator = previousNodes.iterator();
			optimalNode = iterator.next();
			while (iterator.hasNext()) {
				MaxSeqPairMatchNode currentNode = iterator.next();
				if (optimalNode.getScore() < currentNode.getScore()) {  // Alternative optimal superalignments could be handled here of scores are equal.
					optimalNode = currentNode;
				}
			}
		}
		return optimalNode;
	}
	
	
	public void setScore(long score) {
		this.score = score;
	}


	public long getScore() {
		return score;
	}


	@Override
	public String toString() {
		return "(" + getPosition(0) + ", " + getPosition(1) + "):" + getScore();  //TODO Refactor method, if more than two indices should be supported in the future.
	}
}
