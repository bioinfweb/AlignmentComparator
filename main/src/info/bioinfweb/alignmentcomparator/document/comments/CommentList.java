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


import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner;
import info.bioinfweb.commons.collections.SequenceIntervalList;
import info.bioinfweb.commons.collections.SequenceIntervalPositionAdapter;

import java.util.HashMap;
import java.util.Map;



public class CommentList extends SequenceIntervalList<Comment> {
	public static final int INITIAL_EXPECTED_SEQUENCE_LENGTH = 5000;
	public static final int INTERVAL_LENGTH = 100;
	
	
	private Map<Class<? extends CommentPositioner>, Object> gloabelPositionerData = 
			new HashMap<Class<? extends CommentPositioner>, Object>();

  
	public CommentList(SequenceIntervalPositionAdapter<Comment> positionAdapter) {
		super(positionAdapter, INITIAL_EXPECTED_SEQUENCE_LENGTH, INTERVAL_LENGTH);
	}


	public boolean add(int firstPos, int lastPos, String text) {
		return add(new SingleCommentAnchor(firstPos, lastPos), text);
	}
	
	
	public boolean add(SingleCommentAnchor position, String text) {
		return add(new Comment(position, text));
	}
	
	
	public void setGlobalPositionerData(Class<? extends CommentPositioner> type, Object data) {
		gloabelPositionerData.put(type, data);
	}
	
	
	public Object getGlobalPositionerData(Class<? extends CommentPositioner> type) {
		return gloabelPositionerData.get(type);
	}
}
