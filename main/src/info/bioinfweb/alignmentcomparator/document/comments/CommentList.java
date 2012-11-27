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


import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner;
import info.webinsel.util.collections.MultiTreeMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;



public class CommentList {
	private MultiTreeMap<Integer, Comment> firstMap = new MultiTreeMap<Integer, Comment>();
	private MultiTreeMap<Integer, Comment> lastMap = new MultiTreeMap<Integer, Comment>();
	
	private Map<Class<? extends CommentPositioner>, Object> gloabelPositionerData = 
			new HashMap<Class<? extends CommentPositioner>, Object>();

  
	public void add(int firstPos, int lastPos, String text) {
		add(new CommentPosition(firstPos, lastPos), text);
	}
	
	
	public void add(CommentPosition position, String text) {
		add(new Comment(position, text));
	}
	
	
	public void add(Comment comment) {
		firstMap.put(comment.getPosition().getFirstPos(), comment);
		lastMap.put(comment.getPosition().getLastPos(), comment);
	}
	
	
	private void addMultiMapToSet(Map<Integer, ? extends Collection<Comment>> map, Set<Comment> set) {
		Iterator<Integer> keyIterator = map.keySet().iterator();
		while (keyIterator.hasNext()) {
			set.addAll(map.get(keyIterator.next()));
		}
	}
	
	
	public Set<Comment> getOverlappingElements(int firstPos, int lastPos) {
		TreeSet<Comment> result = new TreeSet<Comment>();
		addMultiMapToSet(firstMap.subMap(0, lastPos), result);   
		addMultiMapToSet(lastMap.subMap(firstPos, Integer.MAX_VALUE), result);
  	//TODO So werden ohnehin immer alle Elemente geprüft => Keine Ersparnis
		//TODO LastPos gibt das Sequenzende und nicht das tatsächliche Ende des Kommentars an, oder?
		return result;
	}
	
	
	public Iterator<Comment> commentIterator() {
		return new Iterator<Comment>() {
			private Iterator<Integer> keyIterator = firstMap.keySet().iterator();
			private Iterator<Comment> collectionIterator = null;
			
			
			@Override
			public boolean hasNext() {
				if ((collectionIterator != null) && collectionIterator.hasNext()) {
					return true;
				}
				else {
					return keyIterator.hasNext();
				}
			}
			

			@Override
			public Comment next() {
				if ((collectionIterator == null) || !collectionIterator.hasNext()) {
					collectionIterator = firstMap.get(keyIterator.next()).iterator();
				}
				return collectionIterator.next();
			}
			

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	
	public Collection<Comment> getByFirstPos(int firstPos) {
		return firstMap.get(firstPos);
	}


	public Collection<Comment> getByLastPos(int lastPos) {
		return lastMap.get(lastPos);
	}


	public void clear() {
		firstMap.clear();
		lastMap.clear();
	}

	
	public Comment remove(Comment comment) {
		firstMap.remove(comment.getPosition().getFirstPos(), comment);
		return lastMap.remove(comment.getPosition().getFirstPos(), comment);
	}


	public boolean isEmpty() {
		return firstMap.isEmpty();
	}

	
	public int size() {
		return firstMap.totalSize();
	}
	
	
	public void setGlobalPositionerData(Class<? extends CommentPositioner> type, Object data) {
		gloabelPositionerData.put(type, data);
	}
	
	
	public Object getGlobalPositionerData(Class<? extends CommentPositioner> type) {
		return gloabelPositionerData.get(type);
	}
}
