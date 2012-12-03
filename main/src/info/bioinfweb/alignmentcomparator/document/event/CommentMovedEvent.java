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
package info.bioinfweb.alignmentcomparator.document.event;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.CommentPosition;



public class CommentMovedEvent extends CommentEvent {
  private CommentPosition oldPosition;
  private CommentPosition newPosition;
	
  
  public CommentMovedEvent(Document source, Comment comment,
			CommentPosition oldPosition, CommentPosition newPosition) {
  	
		super(source, comment);
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}


	public CommentPosition getOldPosition() {
		return oldPosition;
	}


	public CommentPosition getNewPosition() {
		return newPosition;
	}
}
