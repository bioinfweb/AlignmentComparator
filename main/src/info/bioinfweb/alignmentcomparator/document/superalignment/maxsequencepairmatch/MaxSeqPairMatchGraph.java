/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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


import java.util.Comparator;
import java.util.TreeMap;



public class MaxSeqPairMatchGraph extends TreeMap<int[], MaxSeqPairMatchNode> {
	// Using the array directly as the key instead of creating a separate class with an array property and 
	// a compare method saves a pointer and therefore 8 bytes per node.
	//TODO Using two int properties instead of an array would work as well.
	
	private static final int[] END_NODE_KEY = {Integer.MAX_VALUE, Integer.MAX_VALUE};
	
	
	public MaxSeqPairMatchGraph() {
		super(new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				int result = o1[0] - o2[0];
				if (result == 0) {
					result = o1[1] - o2[1];
				}
				return result;
			}
		});
		
		addEndNode();
	}
	
	
	private void addEndNode() {
		add(new MaxSeqPairMatchNode(END_NODE_KEY));
	}


	@Override
	public MaxSeqPairMatchNode put(int[] key, MaxSeqPairMatchNode value) {
		if (key.length == 2) {
			return super.put(key, value);
		}
		else {
			throw new IllegalArgumentException("A key array must contain exactly two values.");
		}
	}
	

	@Override
	public void clear() {
		super.clear();
		addEndNode();
	}


	public void add(MaxSeqPairMatchNode node) {
		put(node.getPositions(), node);
	}
	
	
	public MaxSeqPairMatchNode getEndNode() {
		return get(END_NODE_KEY);
	}
	
	
	public boolean isEndNode(MaxSeqPairMatchNode node) {
		return (node.getPosition(0) == Integer.MAX_VALUE) && (node.getPosition(0) == Integer.MAX_VALUE);
	}
}
