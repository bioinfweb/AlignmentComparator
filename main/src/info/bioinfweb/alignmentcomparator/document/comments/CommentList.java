/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document.comments;


import info.webinsel.util.Math2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;



public class CommentList {
	private TreeMap<CommentPosition, Comment> map = new TreeMap<CommentPosition, Comment>(new CommentPositionComparator());

  
	public CommentPositionComparator comparator() {
		return (CommentPositionComparator)map.comparator();
	}
	
	
	public void add(int firstPos, int lastPos, String text) {
		add(new CommentPosition(firstPos, lastPos), text);
	}
	
	
	public void add(CommentPosition position, String text) {
		add(new Comment(position, text));
	}
	
	
	public void add(Comment comment) {
		map.put(comment.getPosition(), comment);
	}
	
	
	public List<Comment> getOverlappingElements(int firstPos, int lastPos) {
		List<Comment> result = new LinkedList<Comment>();
		Iterator<CommentPosition> posIterator = map.keySet().iterator();
		while (posIterator.hasNext()) {
			CommentPosition pos = posIterator.next();
			if (Math2.overlaps(firstPos, lastPos, pos.getFirstPos(), pos.getLastPos())) {
				result.add(map.get(pos));
			}
			else if (pos.getFirstPos() > lastPos) {
				break;
			}
		}
		return result;
	}
	
	
	public Iterator<CommentPosition> positionIterator() {
		return map.keySet().iterator();
	}
	
	
	public Iterator<Comment> commentIterator() {
		return new Iterator<Comment>() {
			private Iterator<CommentPosition> positionIterator = positionIterator(); 
			
			
			@Override
			public boolean hasNext() {
				return positionIterator.hasNext();
			}
			

			@Override
			public Comment next() {
				return get(positionIterator.next());
			}
			

			@Override
			public void remove() {
				positionIterator.remove();
			}
		};
	}

	
	public Comment get(CommentPosition pos) {
		return map.get(pos);
	}


	public void clear() {
		map.clear();
	}

	
	public Comment remove(Object o) {
		return map.remove(o);
	}


	public boolean isEmpty() {
		return map.isEmpty();
	}

	
	public int size() {
		return map.size();
	}
}
