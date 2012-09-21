package info.bioinfweb.alignmentcomparator.document;


import info.webinsel.util.Math2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



public class CommentList {
  private List<Comment> list = new LinkedList<Comment>();

  
  private int indexAfter(Comment comment) {
  	Iterator<Comment> iterator = iterator();
  	int index = 0;
  	while (iterator.hasNext()) {
  		Comment current = iterator.next();
  		if (((current.getFistPos() == comment.getFistPos()) && (current.getLastPos() >= comment.getLastPos())) ||
  				(current.getFistPos() > comment.getFistPos())) {
  			return index;
  		}
  		index++;
  	}
  	return -1;
  }
  
  
	public void add(Comment comment) {
		int index = indexAfter(comment);
		if (index == -1) {
			list.add(comment);
		}
		else {
			list.add(index, comment);
		}
	}
	
	
	public List<Comment> getOverlappingElements(int firstPos, int lastPos) {
		List<Comment> result = new LinkedList<Comment>();
		Iterator<Comment> iterator = iterator();
		while (iterator.hasNext()) {
			Comment current = iterator.next();
			if (Math2.overlaps(firstPos, lastPos, current.getFistPos(), current.getLastPos())) {
				result.add(current);
			}
		}
		return result;
	}

	
	public void clear() {
		list.clear();
	}

	
	public boolean contains(Object o) {
		return list.contains(o);
	}

	
	public boolean isEmpty() {
		return list.isEmpty();
	}

	
	public Iterator<Comment> iterator() {
		return list.iterator();
	}

	
	public boolean remove(Object o) {
		return list.remove(o);
	}

	
	public int size() {
		return list.size();
	}
}
