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
package info.bioinfweb.alignmentcomparator.document.comment;


import info.bioinfweb.alignmentcomparator.gui.comment.CommentPositioner;

import java.util.HashMap;
import java.util.Map;



public class Comment implements Comparable<Comment> {
  private String text;
  private CommentAnchor anchor;
  private Map<Class<? extends CommentPositioner>, Object> positionData = new HashMap<Class<? extends CommentPositioner>, Object>();
  
  
	public Comment(String text, CommentAnchor anchor) {
		super();
		this.text = text;
		this.anchor = anchor;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public CommentAnchor getAnchor() {
		return anchor;
	}


	public Object getPositionData(Class<? extends CommentPositioner> type) {
		return positionData.get(type);
	}
	
	
	public void setPositionData(Class<? extends CommentPositioner> type, Object data) {
		positionData.put(type, data);
	}


	@Override
	public int compareTo(Comment other) {
		int result = anchor.compareTo(other.getAnchor());
		if (result == 0) {
			result = getText().compareTo(getText());
		}
		return result;
	}


	@Override
	public boolean equals(Object other) {
		boolean result = (other instanceof Comment);
		if (result) {
			Comment c = (Comment)other;
			result = getAnchor().equals(c.getAnchor()) && getText().equals(c.getText());
		}
		return result;
	}


	@Override
	public int hashCode() {
		return getAnchor().hashCode() * 13 + getText().hashCode();
	}
}
