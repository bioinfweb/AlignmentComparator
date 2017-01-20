/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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



public class SingleCommentAnchor implements Comparable<SingleCommentAnchor>, CommentAnchor {
  private int firstPos;
  private int lastPos;
  
  
	public SingleCommentAnchor(int firstPos, int lastPos) {
		super();
		this.firstPos = firstPos;
		this.lastPos = lastPos;
	}
	
	
	@Override
	public int getFirstPos() {
		return firstPos;
	}
	
	
	@Override
	public int getLastPos() {
		return lastPos;
	}
	
	
	public void setFirstPos(int firstPos) {
		this.firstPos = firstPos;
	}


	public void setLastPos(int lastPos) {
		this.lastPos = lastPos;
	}


	public int sequenceLength() {
		return getLastPos() - getFirstPos() + 1;
	}


	@Override
	public int compareTo(SingleCommentAnchor other) {
		int result = getFirstPos() - other.getFirstPos();
		if (result == 0) {
			result = getLastPos() - other.getLastPos();
		}
		return result;
	}


	@Override
	public boolean equals(Object other) {
		if (other instanceof SingleCommentAnchor) {
			return compareTo((SingleCommentAnchor)other) == 0;
		}
		else {
			return false;
		}
	}


	@Override
	public int hashCode() {
		return (7 + getFirstPos()) * 59 + getLastPos(); 
	}
}
