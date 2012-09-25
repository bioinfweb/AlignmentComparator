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


import java.awt.Dimension;
import java.awt.Graphics2D;

import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.CommentList;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonPanel;



public interface CommentPositioner {
  /**
   * Implementing classes should perform the screen positioning of the comments here and set the maximal x and y 
   * positions to determine the size if the {@link AlignmentComparisonPanel}.  
   * @param comments - the comment list containing the objects to be positioned
   */
  public void position(CommentList comments);
  
  public void paint(CommentList comments, AlignmentComparisonPanel panel, int alignmentLength, Graphics2D g, float x, float y);
  
  public Dimension getCommentDimension(CommentList comments, AlignmentComparisonPanel panel);
  
  public Comment getCommentByMousePosition(CommentList comments, AlignmentComparisonPanel panel, 
  		float paintX, float paintY, int mouseX, int mouseY);
}
