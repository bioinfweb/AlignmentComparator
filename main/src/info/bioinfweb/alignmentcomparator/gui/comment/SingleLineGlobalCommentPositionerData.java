/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
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
package info.bioinfweb.alignmentcomparator.gui.comment;


import info.bioinfweb.alignmentcomparator.document.comment.Comment;
import info.bioinfweb.alignmentcomparator.document.comment.CommentList;
import info.bioinfweb.commons.collections.SequenceIntervalList;



public class SingleLineGlobalCommentPositionerData {
  private int maxColumn = 0;
  private int maxLine = 0;
  private SequenceIntervalList<Comment> commentList;
  
  
	public SingleLineGlobalCommentPositionerData(int sequenceLength) {
		super();
		commentList = new SequenceIntervalList<Comment>(new SingleLinePositionAdapter(), sequenceLength, CommentList.INTERVAL_LENGTH);
	}


	public int getMaxLine() {
		return maxLine;
	}
  
  
	public void setMaxLine(int maxLine) {
		this.maxLine = maxLine;
	}
  
  
	public int getMaxColumn() {
		return maxColumn;
	}
  
  
	public void setMaxColumn(int maxColumn) {
		this.maxColumn = maxColumn;
	}


	public SequenceIntervalList<Comment> getCommentList() {
		return commentList;
	}
}
