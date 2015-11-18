package info.bioinfweb.alignmentcomparator.document.superalignment.maxsequencepairmatch;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;



public class ShiftQueues {
	private Map<Integer, Queue<int[]>> map = new TreeMap<Integer, Queue<int[]>>();
	
	
	public Queue<int[]> getQueue(int sequenceID) {
		Queue<int[]> result = map.get(sequenceID);
		if (result == null) {
			result = new ArrayDeque<int[]>();
			map.put(sequenceID, result);
		}
		return result;
	}
	
	
	public boolean isEmpty() {
		for (Queue<int[]> queue : map.values()) {
			if (!queue.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	
	public int nextSequenceID() {
		Integer result = null;
		for (Integer sequenceID : map.keySet()) {
			Queue<int[]> queue = getQueue(sequenceID);
			if (!queue.isEmpty()) {
				if ((result == null) || leftOf(queue.peek(), getQueue(result).peek())) {
					result = sequenceID;
				}
			}
		}
		
		if (result == null) {
			return -1;
		}
		else {
			return result;
		}
	}
	
	
	public static boolean leftOf(int[] left, int[] right) {
		return (left[0] < right[0]) || ((left[0] == right[0]) && (left[1] < right[1]));
	}


	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{");
		Iterator<Integer> idIterator = map.keySet().iterator();
		while (idIterator.hasNext()) {
			int sequenceID = idIterator.next();
			result.append(sequenceID);
			result.append("=[");
			Iterator<int[]> posIterator = map.get(sequenceID).iterator();
			while (posIterator.hasNext()) {
				int[] positions = posIterator.next();
				result.append("[");
				result.append(positions[0]);
				result.append(", ");
				result.append(positions[1]);
				result.append("]");
				if (posIterator.hasNext()) {
					result.append(", ");
				}
			}
			result.append("]");
			if (idIterator.hasNext()) {
				result.append(", ");
			}
		}
		result.append("}");
		
		return result.toString();
	}
}
