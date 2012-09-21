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
package info.bioinfweb.alignmentcomparator.document;



public class Comment {
  private String text;
  private int fistPos;
  private int lastPos;
  
  
	public Comment(String text, int fistPos, int lastPos) {
		super();
		this.text = text;
		this.fistPos = fistPos;
		this.lastPos = lastPos;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public int getFistPos() {
		return fistPos;
	}


	public void setFistPos(int fistPos) {
		this.fistPos = fistPos;
	}


	public int getLastPos() {
		return lastPos;
	}


	public void setLastPos(int lastPos) {
		this.lastPos = lastPos;
	}
}
