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
import info.bioinfweb.commons.collections.SequenceIntervalPositionAdapter;



public class SingleLinePositionAdapter implements SequenceIntervalPositionAdapter<Comment> {
	@Override
	public int getFirstPos(Comment o) {
		return o.getAnchor().getFirstPos();
	}

	
	@Override
	public int getLastPos(Comment o) {
		return getFirstPos(o) + //TODO PositionData ist zum Zeitpunkt des Einf�gens in die Liste nie verf�gbar! (L�nge wird nur von Elementen, die bereits in Liste sind berechnet. MVC-Trennung sollte wohl strikter eingehalten werden.) 
				((SingleLineCommentPositionData)o.getPositionData(SingleLineCommentPositioner.class)).getLength() - 1;
	}


	@Override
	public void setFirstPos(Comment o, int newFirstPos) {
		throw new UnsupportedOperationException("SingleLinePositionAdapter does not support moving elements.");
	} 


	@Override
	public void setLastPos(Comment o, int newLastPos) {
		throw new UnsupportedOperationException("SingleLinePositionAdapter does not support moving elements.");
	}


	@Override
 	public int compare(Comment c1, Comment c2) {
		return c1.compareTo(c2);
	}
}
