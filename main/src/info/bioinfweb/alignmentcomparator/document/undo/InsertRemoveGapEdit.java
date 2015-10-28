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
package info.bioinfweb.alignmentcomparator.document.undo;


import java.util.List;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperAlignedModelDecorator;



public abstract class InsertRemoveGapEdit extends DocumentEdit {
	private String alignmentName;
  private int startPos;
  private int length;
  
  
	public InsertRemoveGapEdit(Document document, String alignmentName, int startPos, int endPos) {		
		super(document);
		this.alignmentName = alignmentName;
		this.startPos = startPos;
		this.length = endPos - startPos + 1; 
	}
	
	
	protected void insert() {
		List<Integer> unalignedIndices = getDocument().getAlignments().get(
				getAlignmentName()).getSuperAligned().getUnalignedIndices();
		for (int pos = getStartPos(); pos <= getStartPos() + getLength() - 1; pos++) {
			unalignedIndices.add(pos,	SuperAlignedModelDecorator.SUPER_GAP_INDEX);
		}
	}
	
	
	protected void remove() {
		List<Integer> unalignedIndices = getDocument().getAlignments().get(
				getAlignmentName()).getSuperAligned().getUnalignedIndices();
		for (int pos = getStartPos(); pos <= getStartPos() + getLength() - 1; pos++) {
			if (unalignedIndices.get(getStartPos()) == SuperAlignedModelDecorator.SUPER_GAP_INDEX) {
				unalignedIndices.remove(getStartPos());
			}
			else {
				throw new IllegalArgumentException("Removing a supergap from index " + getStartPos() + 
						" is not possible, because there is no supergap present at this position, but a reference to position " + 
						unalignedIndices.get(getStartPos()) + " in the underlying alignment.");
			}
		}
	}


	public String getAlignmentName() {
		return alignmentName;
	}


	public int getStartPos() {
		return startPos;
	}


	public int getLength() {
		return length;
	}
}
