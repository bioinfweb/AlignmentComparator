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
package info.bioinfweb.alignmentcomparator.gui.comments;


public class SingleLineGlobalCommentPositionerData {
  private int maxColumn = 0;
  private int maxLine = 0;
  
  
	public SingleLineGlobalCommentPositionerData(int maxColumn, int maxLine) {
		super();
		this.maxColumn = maxColumn;
		this.maxLine = maxLine;
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
}
