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
package info.bioinfweb.alignmentcomparator.document.event;



/**
 * Classes implementing this interface can listen to changes made in a TreeGraph 2 document.
 * 
 * @author Ben St&ouml;ver
 */
public interface DocumentListener {
  /** Called every time changes were made to the document. */
  public void changeHappened(DocumentEvent e);
  
  /** 
   * Called every time changes were made to the sequence names. 
   * ({@link #changeHappened()} is called additionally in that case.)
   */
  public void namesChanged(DocumentEvent e);
}