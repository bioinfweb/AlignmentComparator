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


import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.CommentList;
import info.bioinfweb.alignmentcomparator.document.comments.CommentPosition;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonPanel;
import info.webinsel.util.Math2;



public class SingleLineCommentPositioner implements CommentPositioner {
	private List<Comment> blockingComments;
	private int maxLines;
	
	
	private SingleLineCommentPositionData getData(Comment comment) {
		return (SingleLineCommentPositionData)comment.getPositionData(SingleLineCommentPositioner.class);
	}
	
	
	/**
	 * Calculates the upper most available line for the specified column and length and removes passed comments from
	 * <code>blockingComments</code>.
	 * @param column
	 * @param blockingComments
	 * @return
	 */
	private int calculateLine(int column, int length) {
		// Prepare array:
		boolean[] blockedLines = new boolean[maxLines];
		for (int i = 0; i < blockedLines.length; i++) {
			blockedLines[i] = false;
		}
		
		// Fill array, remove passed comments:
		Iterator<Comment> iterator = blockingComments.iterator();
		while (iterator.hasNext()) {
			Comment comment = iterator.next();
			int firstPos = comment.getPosition().getFirstPos();
			SingleLineCommentPositionData data = getData(comment);
			if (comment.getPosition().getLastPos() < column) {
				iterator.remove();  // Remove elements that lie before the current column
			}
			else if (Math2.overlaps(column, column + length, firstPos, firstPos + data.getLength())) {				
				blockedLines[data.getLine()] = true;
			}
		}
		
		// Calculate result:
		for (int i = 0; i < blockedLines.length; i++) {
			if (!blockedLines[i]) {
				return i;
			}
		}
		return maxLines + 1;
	}
	
	
	private int calculateLength(String text) {
		return 0; //TODO implement
	}
	
	
	@Override
	public void position(CommentList comments) {
		blockingComments = new LinkedList<Comment>();
		maxLines = 0;
		Iterator<Comment> iterator = comments.commentIterator();
		while (iterator.hasNext()) {
			Comment comment = iterator.next();
			int length = calculateLength(comment.getText());
			comment.setPositionData(SingleLineCommentPositioner.class, new SingleLineCommentPositionData(
					calculateLine(comment.getPosition().getFirstPos(), length), length));
			blockingComments.add(comment);
		}
	}
	
	
	private void paintComment(AlignmentComparisonPanel panel, Graphics2D g, Comment comment, float x, float y) {
		SingleLineCommentPositionData data = getData(comment);
		CommentPosition pos = comment.getPosition();
		int sequenceLength = pos.getLastPos() - pos.getFirstPos() + 1;
		
		g.setBackground(panel.getColorMap().get(AlignmentComparisonPanel.DEFAULT_BG_COLOR_ID));
		g.setFont(panel.getFont());
		
		final float lineWidth = 1f;  //TODO Evtl. sinvolleren Wert (aus Stroke?)
		float x1 = x + pos.getFirstPos() * panel.getCompoundWidth();
		float y1 = y + data.getLine() * panel.getCompoundHeight();
		float seqX2 = x + (pos.getLastPos() + 1) * panel.getCompoundWidth() - lineWidth;
		float y2 = y + (data.getLine() + 1) * panel.getCompoundHeight() - lineWidth; 
		if (sequenceLength <= data.getLength()) {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_BORDER_COLOR_ID));
			g.fill(new Rectangle2D.Float(x1, y1, seqX2, y2));
		}
		else {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_OVERLAPPING_BORDER_COLOR_ID));
			g.fill(new Rectangle2D.Float(x1, y1, 
					x + (pos.getLastPos() + data.getLength()) * panel.getCompoundWidth() - lineWidth, y2));
			
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_BORDER_COLOR_ID));
			Path2D.Float path = new Path2D.Float();
			path.moveTo(seqX2, y1);
			path.lineTo(x1, y1);
			path.lineTo(x1, y2);
			path.lineTo(seqX2, y2);
			g.draw(path);
		}
		FontMetrics fm = g.getFontMetrics(); 
		g.drawString(comment.getText(), x1 + fm.getAscent() + fm.getHeight(), y1);
	}


	@Override
	public void paint(CommentList comments, AlignmentComparisonPanel panel, int alignmentLength, Graphics2D g, float x, float y) {
		Iterator<Comment> iterator = comments.getOverlappingElements(
				Math.max(0, (int)Math.round((panel.getVisibleRect().getMinX() - x) / panel.getCompoundWidth()) - 1), 
				Math.min(alignmentLength - 1, 
						(int)Math.round((panel.getVisibleRect().getMaxX() - x) / panel.getCompoundWidth()))).iterator();
		while (iterator.hasNext()) {
			paintComment(panel, g, iterator.next(), x, y);
		}
	}


	@Override
	public Comment getCommentByMousePosition(CommentList comments, AlignmentComparisonPanel panel, 
			float paintX, float paintY, int mouseX, int mouseY) {
		
		if ((mouseX >= paintX) && (mouseY >= paintY)) {
			Iterator<Comment> iterator = comments.commentIterator();
			while (iterator.hasNext()) {
				Comment comment = iterator.next();
				CommentPosition pos = comment.getPosition();
				SingleLineCommentPositionData data = getData(comment);
				float x1 = paintX + pos.getFirstPos() * panel.getCompoundWidth();
				if (x1 > panel.getVisibleRect().getMaxX()) {  // all visible comments were already checked
					return null;
				}
				else if (Math2.isBetween(mouseX, x1, 
					  		x1 + Math.max(data.getLength(), pos.getLastPos() - pos.getFirstPos() - 1) * panel.getCompoundWidth()) &&
			  		Math2.isBetween(mouseY, paintY + data.getLine() * panel.getCompoundHeight(), 
			  				paintY + (data.getLine() + 1) * panel.getCompoundHeight())) {
			  	
			  	return comment;
			  }
			}
		}
		return null;
	}
}
