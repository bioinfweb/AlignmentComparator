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
package info.bioinfweb.alignmentcomparator.gui.comments;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.CommentPosition;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



public class SingleLineCommentPositioner implements CommentPositioner {
	public static final float MARGIN = 1f;
	public static final Font NO_ZOOM_FONT = new Font(AlignmentArea.FONT_NAME, AlignmentArea.FONT_STYLE, 
			Math.round(AlignmentArea.COMPOUND_WIDTH * AlignmentArea.FONT_SIZE_FACTOR));  //TODO Could become a problem if the compound width and height are changed independently which is allowed in LibrAlign. => Make sure the AC user cannot do this.
	
	
	private List<Comment> blockingComments;
	private int maxLine;
	
	
	private SingleLineCommentPositionData getData(Comment comment) {
		return (SingleLineCommentPositionData)comment.getPositionData(SingleLineCommentPositioner.class);
	}
	
	
	/**
	 * Calculates the upper most available line for the specified column and length and removes passed comments from
	 * <code>blockingComments</code>.
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
				FontCalculator.getInstance().getWidth(NO_ZOOM_FONT, comment.getText())) / AlignmentArea.COMPOUND_WIDTH));
	}
	
	
	private SingleLineGlobalCommentPositionerData getGlobalPositionerData(Document document) {
		Object obj = document.getComments().getGlobalPositionerData(SingleLineCommentPositioner.class);
		if (obj == null) {
			document.getComments().setGlobalPositionerData(SingleLineCommentPositioner.class, 
					new SingleLineGlobalCommentPositionerData(document.getAlignedLength()));
		}
		return (SingleLineGlobalCommentPositionerData)document.getComments().getGlobalPositionerData(
				SingleLineCommentPositioner.class);
	}
	
	
	@Override
	public void position(Document document) {
		SingleLineGlobalCommentPositionerData globalData = getGlobalPositionerData(document);
		globalData.getCommentList().clear();
		
		blockingComments = new LinkedList<Comment>();
		maxLine = -1;
		int maxColumn = 0;
		Iterator<Comment> iterator = document.getComments().iterator();
		while (iterator.hasNext()) {
			Comment comment = iterator.next();
			int length = calculateLength(comment);
			int line = calculateLine(comment, comment.getPosition().getFirstPos(), length);
			comment.setPositionData(SingleLineCommentPositioner.class, new SingleLineCommentPositionData(line, length));
			globalData.getCommentList().add(comment);
			
			blockingComments.add(comment);
			maxLine = Math.max(maxLine, line);
			maxColumn = Math.max(maxColumn, comment.getPosition().getFirstPos() + length);
		}
		
		globalData.setMaxColumn(maxColumn);
		globalData.setMaxLine(maxLine);
	}
	
	
	private void paintComment(AlignmentComparisonComponent comparisonComponent, Graphics2D g, Comment comment, float x, float y) {
		SingleLineCommentPositionData data = getData(comment);
		CommentPosition pos = comment.getPosition();
		AlignmentArea area = comparisonComponent.getFirstAlignmentArea();

		Color fontColor;
		SequenceColorSchema colorSchema = area.getColorSchema();
		if (comment.equals(comparisonComponent.getSelection().getComment())) {
			g.setColor(colorSchema.getSelectionColor());
			fontColor = colorSchema.getSelectionFontColor();
		}
		else {
			g.setColor(colorSchema.getDefaultBgColor());
			fontColor = colorSchema.getFontColor();
		}
		g.setFont(area.getCompoundFont());
		
		final float lineWidth = 1f;  //TODO Evtl. sinvolleren Wert (aus Stroke?)
		float x1 = x + (pos.getFirstPos() - 1) * area.getCompoundWidth();  // BioJava indices start with 1
		float y1 = y + data.getLine() * area.getCompoundHeight();
		float y2 = y1 + area.getCompoundHeight() - lineWidth;

		Rectangle2D.Float r = new Rectangle2D.Float(x1, y1, 
				data.getLength() * area.getCompoundWidth() - lineWidth, y2 - y1);  // BioJava indices start with 1
		g.fill(r);
		if (pos.sequenceLength() <= data.getLength()) {
			g.setColor(colorSchema.getSelectionFontColor());  //TODO Can this be used as the border color?
			g.draw(r);   
			
			g.setColor(colorSchema.getTokenBorderColor());
			float seqX2 = x + (pos.getLastPos()) * area.getCompoundWidth() - lineWidth;  // BioJava indices start with 1
			Path2D.Float path = new Path2D.Float();
			path.moveTo(seqX2, y1);
			path.lineTo(x1, y1);
			path.lineTo(x1, y2);
			path.lineTo(seqX2, y2);
			g.draw(path);
		}
		else {
			g.setColor(colorSchema.getTokenBorderColor());
			g.draw(r);
		}
		FontMetrics fm = g.getFontMetrics();
		g.setColor(fontColor);
		g.drawString(comment.getText(), x1 + MARGIN * area.getZoomX(), y1 + fm.getAscent());
	}


	@Override
	public void paint(AlignmentComparisonComponent comparisonComponent, int alignmentLength, 
			Graphics2D g, float x, float y) {
		
		Rectangle visibleRect = ((JComponent)comparisonComponent.getCommentArea().getToolkitComponent()).getVisibleRect();
		
		AlignmentArea area = comparisonComponent.getFirstAlignmentArea();
		Iterator<Comment> iterator = 
				getGlobalPositionerData(comparisonComponent.getDocument()).getCommentList().getOverlappingElements(
						Math.max(0, (int)Math.round((visibleRect.getMinX() - x) / area.getCompoundWidth()) - 1), 
						Math.min(alignmentLength - 1, 
								(int)Math.round((visibleRect.getMaxX() - x) / area.getCompoundWidth()))).iterator();
		while (iterator.hasNext()) {
			paintComment(comparisonComponent, g, iterator.next(), x, y);
		}
	}


	@Override
	public Dimension getCommentDimension(AlignmentComparisonComponent comparisonComponent) {
		SingleLineGlobalCommentPositionerData data = getGlobalPositionerData(comparisonComponent.getDocument());
		AlignmentArea area = comparisonComponent.getFirstAlignmentArea();
		return new Dimension(Math2.roundUp(data.getMaxColumn() * area.getCompoundWidth()),  // BioJava indices start with 1 
				Math2.roundUp((data.getMaxLine() + 1) * area.getCompoundHeight()));
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner#getCommentByMousePosition(info.bioinfweb.alignmentcomparator.document.Document, info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonPanel, float, float, int, int)
	 */
	@Override
	public Comment getCommentByMousePosition(AlignmentComparisonComponent comparisonComponent, 
			float paintX, float paintY, int mouseX, int mouseY) {
		
		if ((mouseX >= paintX) && (mouseY >= paintY)) {
			AlignmentArea area = comparisonComponent.getFirstAlignmentArea();
			int column = area.getContentArea().columnByPaintX(Math.round(mouseX - paintX));
			Iterator<Comment> iterator = 
					getGlobalPositionerData(comparisonComponent.getDocument()).getCommentList().getOverlappingElements(column, column).iterator();  // Even if the mouse position should be behind the last column, an empty iterator will be returned here.
			while (iterator.hasNext()) {
				Comment comment = iterator.next();
				SingleLineCommentPositionData data = getData(comment);
				if (Math2.isBetween(mouseY, paintY + data.getLine() * area.getCompoundHeight(), 
			  		paintY + (data.getLine() + 1) * area.getCompoundHeight())) {
			  	
			  	return comment;
			  }
			}
		}
		return null;
	}
}
