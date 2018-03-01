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
package info.bioinfweb.alignmentcomparator.gui.comment;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comment.Comment;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;

import java.awt.Graphics2D;



public interface CommentPositioner {
  /**
   * Implementing classes should perform the screen positioning of the comments here and set the maximal x and y 
   * positions to determine the size if the {@link AlignmentComparisonPanel}.
   * 
   * @param comments - the comment list containing the objects to be positioned
   */
  public void position(Document document);
  
  public void paint(AlignmentComparisonComponent comparisonComponent, int alignmentLength, 
  		Graphics2D g, float x, float y);
  
  public double getNeededHeight(AlignmentComparisonComponent comparisonComponent);
  
  public double getNeededLengthAfterEnd(AlignmentComparisonComponent comparisonComponent);
  
  /**
   * Implementing classes should return the comment painted at the specified mouse position here.
   * 
   * @param comparisonComponent - the component displaying the comments
   * @param paintX - the x offset where the comment area is painted in the component where the mouse click happened
   * @param paintY - the y offset where the comment area is painted in the component where the mouse click happened
   * @param mouseX - the x position of the mouse click
   * @param mouseY - the y position of the mouse click 
   * @return the comment at the specified position or <code>null</code>, if no comment is present at that location.
   */
  public Comment getCommentByMousePosition(AlignmentComparisonComponent comparisonComponent,	
  		float paintX, float paintY,	int mouseX, int mouseY);
}
