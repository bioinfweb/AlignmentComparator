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

import java.util.HashMap;
import java.util.Map;



public class Comment implements Comparable<Comment> {
  private CommentPosition position;
  private String text;
  private Map<Class<? extends CommentPositioner>, Object> positionData = new HashMap<Class<? extends CommentPositioner>, Object>();
  
  
	public Comment(int firstPos, int lastPos, String text) {
		super();
		this.text = text;
		this.position = new CommentPosition(firstPos, lastPos);
	}


	public Comment(CommentPosition position, String text) {
		super();
		this.text = text;
		this.position = position;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public CommentPosition getPosition() {
		return position;
	}
	
	
	public void setPosition(CommentPosition position) {
		this.position = position;
		//TODO Sicherstellen, dass Kommentar entsprechend der neue Position in CommentList neu eingeordnet wird
	}


	public Object getPositionData(Class<? extends CommentPositioner> type) {
		return positionData.get(type);
	}
	
	
	public void setPositionData(Class<? extends CommentPositioner> type, Object data) {
		positionData.put(type, data);
	}


	@Override
	public int compareTo(Comment other) {
		int result = getPosition().compareTo(other.getPosition());
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
			result = getPosition().equals(c.getPosition()) && getText().equals(c.getText());
		}
		return result;
	}


	@Override
	public int hashCode() {
		return getPosition().hashCode() * 13 + getText().hashCode();
	}
}
