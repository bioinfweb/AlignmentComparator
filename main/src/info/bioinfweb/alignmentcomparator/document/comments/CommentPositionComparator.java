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


import java.util.Comparator;



/**
 * Compares two comment positions by their position in the super alignment.
 * 
 * @author Ben St&ouml;ver
 */
public class CommentPositionComparator implements Comparator<CommentPosition> {
	@Override
	public int compare(CommentPosition pos1, CommentPosition pos2) {
		if (pos1.getFirstPos() == pos2.getFirstPos()) {
			return pos1.getLastPos() - pos2.getLastPos(); 
 		}
		else {
			return pos1.getFirstPos() - pos2.getFirstPos(); 
		}
	}
}
