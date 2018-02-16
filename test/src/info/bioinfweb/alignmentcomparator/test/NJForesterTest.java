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
package info.bioinfweb.alignmentcomparator.test;


import org.forester.evoinference.distance.NeighborJoining;
import org.forester.evoinference.distance.NeighborJoiningF;
import org.forester.evoinference.distance.NeighborJoiningR;
import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;



public class NJForesterTest {
	private static void printNode(PhylogenyNode node, String prefix) {
		System.out.println(prefix + node.getId() + " " + node.getName() + " " + node.getDistanceToParent());
		for (PhylogenyNode child : node.getDescendants()) {
			printNode(child, prefix + "  ");
		}
	}
	
	
	private static PhylogenyNode findLongestBranch(PhylogenyNode parent, PhylogenyNode result, double length) {
		for (PhylogenyNode child : parent.getDescendants()) {
			if (child.getDistanceToParent() > length) {
				result = child;
				length = child.getDistanceToParent();
			}
			result = findLongestBranch(child, result, length);
		}
		return result;
	}
	
	
	/**
	 * Makes sure the tree is rooted on its longest branch.
	 * 
	 * @param tree the tree to be possibly rerooted
	 */
	private static void reroot(Phylogeny tree) {
		PhylogenyNode root = tree.getRoot();
		if (root.getDescendants().size() != 2) {
			throw new InternalError("Unexpected NJ tree.");
		}
		
		double length = root.getChildNode(0).getDistanceToParent() + root.getChildNode(1).getDistanceToParent();  // Assumes that there are always two nodes under the root.
		root = findLongestBranch(root, null, length);  // The direct children of root will not be selected, since they must have a samller distance than their combined branches.
		if (root != null) {
			tree.reRoot(root);
		}
	}
	
	
	public static void main(String[] args) {
		BasicSymmetricalDistanceMatrix matrix = new BasicSymmetricalDistanceMatrix(4);
		matrix.setIdentifier(0, "Cat");
		matrix.setIdentifier(1, "Dog");
		matrix.setIdentifier(2, "Human");
		matrix.setIdentifier(3, "Opossum");
		matrix.setValue(0, 1, 3);
		matrix.setValue(0, 2, 5);
		matrix.setValue(0, 3, 8);
		matrix.setValue(1, 2, 7);
		matrix.setValue(1, 3, 6);
		matrix.setValue(2, 3, 4);

		Phylogeny tree = NeighborJoining.createInstance().execute(matrix);
		printNode(tree.getRoot(), "");
		System.out.println();

		reroot(tree);
		printNode(tree.getRoot(), "");
//		printNode(NeighborJoiningF.createInstance().execute(matrix).getRoot(), "");
//		printNode(NeighborJoiningR.createInstance().execute(matrix).getRoot(), "");
	}
}
