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
package info.bioinfweb.alignmentcomparator.document.undo;


import info.bioinfweb.alignmentcomparator.document.Document;



public abstract class InsertRemoveGapEdit extends DocumentEdit {
	private int activeAlignment;
	private int passiveAlignment;
  private int startPos;
  private int length;
  
  
	public InsertRemoveGapEdit(Document document, boolean inFirstAlignment,
			int startPos, int endPos) {
		
		super(document);
		if (inFirstAlignment) {
			activeAlignment = 0;
			passiveAlignment = 1;
		}
		else {
			activeAlignment = 1;
			passiveAlignment = 0;
		}
		this.startPos = startPos;
		length = endPos - startPos + 1; 
	}
	
	
	protected void insert() {
		for (int pos = getStartPos(); pos <= getStartPos() + getLength() - 1; pos++) {
			getDocument().insertSuperGap(getActiveAlignment(), pos);
		}
	}
	
	
	protected void remove() {
		for (int pos = getStartPos(); pos <= getStartPos() + getLength() - 1; pos++) {
			getDocument().removeSuperGap(getActiveAlignment(), getStartPos());
		}
	}


	public int getActiveAlignment() {
		return activeAlignment;
	}


	public int getPassiveAlignment() {
		return passiveAlignment;
	}


	public int getStartPos() {
		return startPos;
	}


	public int getLength() {
		return length;
	}
}
