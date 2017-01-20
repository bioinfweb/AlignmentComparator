/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012 - 2016  Ben Stöver
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;
import info.bioinfweb.alignmentcomparator.document.comments.SingleCommentAnchor;
import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;



public class SingleLineCommentPositioner implements CommentPositioner {
	public static final float MARGIN = 1f;
	public static final Font NO_ZOOM_FONT = new Font(Font.SANS_SERIF, Font.PLAIN,  //TODO Determine font name and style from paint settings here or allow to customize?
			(int)Math.round(SingleColorTokenPainter.DEFAULT_HEIGHT * SingleColorTokenPainter.FONT_SIZE_FACTOR));  //TODO Could become a problem if the compound width and height are changed independently which is allowed in LibrAlign. => Make sure the AC user cannot do this.
	public static final Color FONT_COLOR = SystemColor.controlText;  //TODO Allow to customize or determine from token painter?
	public static final Color COMMENT_BACKGROUND_COLOR = Color.WHITE;  //TODO Allow to customize or determine from token painter?
	
	
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
		return Math.max(comment.getPosition().sequenceLength(),	(int)Math2.roundUp((2 * MARGIN + 
				FontCalculator.getInstance().getWidth(NO_ZOOM_FONT, comment.getText())) / SingleColorTokenPainter.DEFAULT_WIDTH));
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
		SingleCommentAnchor pos = comment.getPosition();
		AlignmentArea area = comparisonComponent.getFirstAlignmentArea();
		PaintSettings paintSettings = area.getPaintSettings();
		
		Color fontColor;
		if (comment.equals(comparisonComponent.getSelection().getComment())) {
			g.setColor(paintSettings.getSelectionColor());
			fontColor = GraphicsUtils.blend(FONT_COLOR, paintSettings.getSelectionColor());
		}
		else {
			g.setColor(COMMENT_BACKGROUND_COLOR);
			fontColor = FONT_COLOR;
		}
		
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 
				(int)Math.round(paintSettings.getTokenHeight() * SingleColorTokenPainter.FONT_SIZE_FACTOR)));
		
		final double tokenWidth = paintSettings.getTokenWidth(0);
		final double lineWidth = 1f;  //TODO Evtl. sinvolleren Wert (aus Stroke?)
		double x1 = x + pos.getFirstPos() * tokenWidth;  //TODO Is column 0 always present? 
		double y1 = y + data.getLine() * paintSettings.getTokenHeight();
		double y2 = y1 + paintSettings.getTokenHeight() - lineWidth;

		Rectangle2D.Double r = new Rectangle2D.Double(x1, y1, data.getLength() * tokenWidth - lineWidth, y2 - y1);
		g.fill(r);
		if (pos.sequenceLength() <= data.getLength()) {
			g.setColor(fontColor);  //TODO Is this currently always the selection color? Can this be used as the border color?
			g.draw(r);   
			
			g.setColor(FONT_COLOR);
			double seqX2 = x + pos.getLastPos() * tokenWidth - lineWidth;
			Path2D.Float path = new Path2D.Float();
			path.moveTo(seqX2, y1);
			path.lineTo(x1, y1);
			path.lineTo(x1, y2);
			path.lineTo(seqX2, y2);
			g.draw(path);
		}
		else {
			g.setColor(FONT_COLOR);
			g.draw(r);
		}
		FontMetrics fm = g.getFontMetrics();
		g.setColor(fontColor);
		g.drawString(comment.getText(), (float)(x1 + MARGIN * paintSettings.getZoomX()), (float)(y1 + fm.getAscent()));
	}


	@Override
	public void paint(AlignmentComparisonComponent comparisonComponent, int alignmentLength, 
			Graphics2D g, float x, float y) {
		
		Rectangle visibleRect = ((JComponent)comparisonComponent.getCommentArea().getToolkitComponent()).getVisibleRect();
		double tokenWidth = comparisonComponent.getFirstAlignmentArea().getPaintSettings().getTokenWidth(0);
		Iterator<Comment> iterator = 
				getGlobalPositionerData(comparisonComponent.getDocument()).getCommentList().getOverlappingElements(
						Math.max(0, (int)Math.round((visibleRect.getMinX() - x) / tokenWidth) - 1), 
						Math.min(alignmentLength - 1,	(int)Math.round((visibleRect.getMaxX() - x) / tokenWidth))).iterator();
		while (iterator.hasNext()) {
			paintComment(comparisonComponent, g, iterator.next(), x, y);
		}
	}


//	@Override
//	public Dimension getCommentDimension(AlignmentComparisonComponent comparisonComponent) {
//		SingleLineGlobalCommentPositionerData data = getGlobalPositionerData(comparisonComponent.getDocument());
//		PaintSettings paintSettings = comparisonComponent.getFirstAlignmentArea().getPaintSettings();
//		return new Dimension((int)Math2.roundUp(data.getMaxColumn() * paintSettings.getTokenWidth(0)),  // BioJava indices start with 1   //TODO Possible substract 1 because no BioJava indices used
//				(int)Math2.roundUp((data.getMaxLine() + 1) * paintSettings.getTokenHeight()));
//	}


	@Override
	public int getNeededHeight(AlignmentComparisonComponent comparisonComponent) {
		SingleLineGlobalCommentPositionerData data = getGlobalPositionerData(comparisonComponent.getDocument());
		return (int)Math2.roundUp((data.getMaxLine() + 1) * comparisonComponent.getFirstAlignmentArea().getPaintSettings().getTokenHeight());
	}


	@Override
	public int getNeededLengthAfterEnd(AlignmentComparisonComponent comparisonComponent) {
		SingleLineGlobalCommentPositionerData data = getGlobalPositionerData(comparisonComponent.getDocument());
		return Math.max(0, (int)Math2.roundUp((data.getMaxColumn() - comparisonComponent.getDocument().getAlignedLength()) * comparisonComponent.getFirstAlignmentArea().getPaintSettings().getTokenWidth(0)));
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
			double tokenHeight = area.getPaintSettings().getTokenHeight();
			while (iterator.hasNext()) {
				Comment comment = iterator.next();
				SingleLineCommentPositionData data = getData(comment);
				if (Math2.isBetween(mouseY, paintY + data.getLine() * tokenHeight, paintY + (data.getLine() + 1) * tokenHeight)) {
			  	return comment;
			  }
			}
		}
		return null;
	}
}
