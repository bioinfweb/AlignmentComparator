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


import info.bioinfweb.commons.collections.SequenceIntervalPositionAdapter;



public class SequencePositionAdapter implements SequenceIntervalPositionAdapter<Comment> {
	@Override
	public int getFirstPos(Comment comment) {
		return comment.getAnchor().getFirstPos();
	}

	
	@Override
	public int getLastPos(Comment comment) {
  	return comment.getAnchor().getLastPos();
	}


	@Override
	public void setFirstPos(Comment comment, int newFirstPos) {
		comment.getAnchor().setFirstPos(newFirstPos);
	}


	@Override
	public void setLastPos(Comment comment, int newLastPos) {
		comment.getAnchor().setLastPos(newLastPos);
	}


	@Override
	public int compare(Comment c1, Comment c2) {
		return c1.compareTo(c2);
	}
}
