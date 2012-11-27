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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import info.webinsel.util.graphics.FontCalculator;



public class SingleLineCommentPositioner implements CommentPositioner {
	public static final float MARGIN = 1f;
	public static final Font NO_ZOOM_FONT = new Font(AlignmentComparisonPanel.FONT_NAME, 
			AlignmentComparisonPanel.FONT_STYLE, Math.round(AlignmentComparisonPanel.FONT_SIZE_NO_ZOOM));
	
	
	private List<Comment> blockingComments;
	private int maxLine;
	
	
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
	private int calculateLine(Comment comment, int column, int length) {
		// Prepare array:
		boolean[] blockedLines = new boolean[maxLine + 1];
		for (int i = 0; i < blockedLines.length; i++) {
			blockedLines[i] = false;
		}
		
		// Fill array, remove passed comments:
		Iterator<Comment> iterator = blockingComments.iterator();
		while (iterator.hasNext()) {
			Comment currentComment = iterator.next();
			if (!currentComment.equals(comment)) {
				SingleLineCommentPositionData data = getData(currentComment);
				int firstPos = currentComment.getPosition().getFirstPos();
				int end = firstPos + data.getLength() - 1; 
				if (end < column) {
					iterator.remove();  // Remove elements that lie before the current column
				}
				else if (Math2.overlaps(column, column + length, firstPos, end)) {
					blockedLines[data.getLine()] = true;
				}
			}
		}
		
		// Calculate result:
		for (int i = 0; i < blockedLines.length; i++) {
			if (!blockedLines[i]) {
				return i;
			}
		}
		return maxLine + 1;
	}
	
	
	private int calculateLength(Comment comment) {
		return Math.max(comment.getPosition().sequenceLength(),	Math2.roundUp((2 * MARGIN + 
				FontCalculator.getInstance().getWidth(NO_ZOOM_FONT, comment.getText())) / AlignmentComparisonPanel.COMPOUND_WIDTH));
	}
	
	
	@Override
	public void position(CommentList comments) {
		blockingComments = new LinkedList<Comment>();
		maxLine = -1;
		int maxColumn = 0;
		Iterator<Comment> iterator = comments.iterator();
		while (iterator.hasNext()) {
			Comment comment = iterator.next();
			int length = calculateLength(comment);
			int line = calculateLine(comment, comment.getPosition().getFirstPos(), length);
			comment.setPositionData(SingleLineCommentPositioner.class, new SingleLineCommentPositionData(line, length));
			blockingComments.add(comment);
			maxLine = Math.max(maxLine, line);
			maxColumn = Math.max(maxColumn, comment.getPosition().getFirstPos() + length);
		}
		comments.setGlobalPositionerData(SingleLineCommentPositioner.class, 
				new SingleLineGlobalCommentPositionerData(maxColumn, maxLine));
	}
	
	
	private void paintComment(AlignmentComparisonPanel panel, Graphics2D g, Comment comment, float x, float y) {
		SingleLineCommentPositionData data = getData(comment);
		CommentPosition pos = comment.getPosition();

		Color fontColor;
		if (comment.equals(panel.getSelection().getComment())) {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.SELECTION_COLOR_ID));
			fontColor = panel.getColorMap().get(AlignmentComparisonPanel.SELECTION_FONT_COLOR_ID);
		}
		else {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.DEFAULT_BG_COLOR_ID));
			fontColor = panel.getColorMap().get(AlignmentComparisonPanel.FONT_COLOR_ID);
		}
		g.setFont(panel.getCompoundFont());
		
		final float lineWidth = 1f;  //TODO Evtl. sinvolleren Wert (aus Stroke?)
		float x1 = x + (pos.getFirstPos() - 1) * panel.getCompoundWidth();  // BioJava indices start with 1
		float y1 = y + data.getLine() * panel.getCompoundHeight();
		float y2 = y1 + panel.getCompoundHeight() - lineWidth;

		Rectangle2D.Float r = new Rectangle2D.Float(x1, y1, 
				data.getLength() * panel.getCompoundWidth() - lineWidth, y2 - y1);  // BioJava indices start with 1
		g.fill(r);
		if (pos.sequenceLength() <= data.getLength()) {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_OVERLAPPING_BORDER_COLOR_ID));
			g.draw(r);   
			
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_BORDER_COLOR_ID));
			float seqX2 = x + (pos.getLastPos()) * panel.getCompoundWidth() - lineWidth;  // BioJava indices start with 1
			Path2D.Float path = new Path2D.Float();
			path.moveTo(seqX2, y1);
			path.lineTo(x1, y1);
			path.lineTo(x1, y2);
			path.lineTo(seqX2, y2);
			g.draw(path);
		}
		else {
			g.setColor(panel.getColorMap().get(AlignmentComparisonPanel.COMMENT_BORDER_COLOR_ID));
			g.draw(r);
		}
		FontMetrics fm = g.getFontMetrics();
		g.setColor(fontColor);
		g.drawString(comment.getText(), x1 + MARGIN * panel.getZoom(), y1 + fm.getAscent());
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
	public Dimension getCommentDimension(CommentList comments, AlignmentComparisonPanel panel) {
		SingleLineGlobalCommentPositionerData data = 
				(SingleLineGlobalCommentPositionerData)comments.getGlobalPositionerData(SingleLineCommentPositioner.class);
		return new Dimension(Math2.roundUp(data.getMaxColumn() * panel.getCompoundWidth()),  // BioJava indices start with 1 
				Math2.roundUp((data.getMaxLine() + 1) * panel.getCompoundHeight()));
	}


	@Override
	public Comment getCommentByMousePosition(CommentList comments, AlignmentComparisonPanel panel, 
			float paintX, float paintY, int mouseX, int mouseY) {
		
		if ((mouseX >= paintX) && (mouseY >= paintY)) {
			Iterator<Comment> iterator = comments.iterator();  //TODO hier getOverlappingElements() benutzen
			while (iterator.hasNext()) {
				Comment comment = iterator.next();
				CommentPosition pos = comment.getPosition();
				SingleLineCommentPositionData data = getData(comment);
				float x1 = paintX + pos.getFirstPos() * panel.getCompoundWidth();
				if (x1 > panel.getVisibleRect().getMaxX()) {  // all visible comments were already checked
					return null;
				}
				else if (Math2.isBetween(mouseX, x1, 
					  		x1 + data.getLength() * panel.getCompoundWidth()) &&
			  		Math2.isBetween(mouseY, paintY + data.getLine() * panel.getCompoundHeight(), 
			  				paintY + (data.getLine() + 1) * panel.getCompoundHeight())) {
			  	
			  	return comment;
			  }
			}
		}
		return null;
	}
}
