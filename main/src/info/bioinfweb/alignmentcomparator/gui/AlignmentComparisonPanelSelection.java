/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
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
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.document.comments.Comment;



public class AlignmentComparisonPanelSelection {
	private int firstPos = -1;
	private int lastPos = -1;
  private Comment comment = null;
  
  
	public int getFirstPos() {
		return firstPos;
	}
	
	
	public void setFirstPos(int firstPos) {
		this.firstPos = firstPos;
	}
	
	
	public int getLastPos() {
		return lastPos;
	}
	
	
	public void setLastPos(int lastPos) {
		this.lastPos = lastPos;
	}
	
	
	public Comment getComment() {
		return comment;
	}
	
	
	public void setComment(Comment comment) {
		this.comment = comment;
	}
}
