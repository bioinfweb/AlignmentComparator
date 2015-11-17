package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencepairmatch;


import java.util.ArrayDeque;
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
}
