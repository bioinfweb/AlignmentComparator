/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/Software>
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
import java.util.List;



public class MaxSeqPairMatchGraph {
	public static final int DEFAULT_NODES_PER_COLUMN = 4;
	
	
	private List<List<MaxSeqPairMatchNode>> nodes;
	private ArrayList<MaxSeqPairMatchNode> previousNodesToEnd;

	
	public MaxSeqPairMatchGraph(int firstColumnCount) {
		super();
		
		nodes = new ArrayList<>(firstColumnCount);
		for (int column = 0; column < firstColumnCount; column++) {
			nodes.add(new ArrayList<MaxSeqPairMatchNode>(DEFAULT_NODES_PER_COLUMN));
		}
		
		previousNodesToEnd = new ArrayList<>(DEFAULT_NODES_PER_COLUMN);
	}


	public List<List<MaxSeqPairMatchNode>> getNodes() {
		return nodes;
	}


	public ArrayList<MaxSeqPairMatchNode> getPreviousNodesToEnd() {
		return previousNodesToEnd;
	}
}
