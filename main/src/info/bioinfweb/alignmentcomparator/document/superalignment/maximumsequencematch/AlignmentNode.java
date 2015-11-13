package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencematch;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;



public class AlignmentNode {
	private int[] positions = new int[2];  //TODO Allow for more then two sequences later?
	private Set<Integer> sequences = new TreeSet<Integer>();  //TODO Replace by more memory efficient set implementation similar to leafSet in TreeGraph in later versions. Maybe a boolean array is already more efficient?
	private Collection<AlignmentNode> linkedNodes = new ArrayList<AlignmentNode>();
	
	
	public int getPosition(int index) {
		return positions[index];
	}
	
	
	public void setPosition(int index, int value) {
		positions[index] = value;
	}
	
	
	public Set<Integer> getSequences() {
		return sequences;
	}
	
	
	public Collection<AlignmentNode> getLinkedNodes() {
		return linkedNodes;
	}
}
