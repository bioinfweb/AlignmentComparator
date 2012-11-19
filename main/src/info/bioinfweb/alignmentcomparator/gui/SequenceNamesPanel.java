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
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.DocumentListener;
import info.webinsel.util.Math2;
import info.webinsel.util.graphics.FontCalculator;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;

import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;



public class SequenceNamesPanel extends AlignmentComparisonHeaderPanel 
    implements Scrollable, AlignmentComparisonPanelListener, DocumentListener {
	
	/** The distance of the labels to the border of the component */
	public static final float LABEL_DISTANCE = 2f;

	
	private Document document;
	
	
  public SequenceNamesPanel(AlignmentComparisonPanel alignmentComparisonPanel,
			Document document) {
	
  	super(alignmentComparisonPanel);
		this.document = document;
		document.addDocumentListener(this);
    sizeChanged(new ChangeEvent(this));  // Can't be moved to AlignmentComparisonHeaderPanel, because document is still null than.
	}


  protected Document getDocument() {
		return document;
	}


	/**
	 * Calculates the necessary with of the component depending on the maximal label length.
	 */
	private float calculateWidth() {
		Font font = getAlignmentComparisonPanel().getFont();
		float maxLength = 0;
		for (int i = 0; i < getDocument().getSequenceCount(); i++) {
			maxLength = Math.max(maxLength, FontCalculator.getInstance().getWidth(font, getDocument().getName(i))); 
		}
  	return maxLength + 2 * LABEL_DISTANCE;
  }
  

	public void sizeChanged(ChangeEvent e) {
    Dimension d = getAlignmentComparisonPanel().getPreferredSize();
    d.width = Math2.roundUp(calculateWidth());
    setSize(d);
    setPreferredSize(d);
	}

	
	@Override
	public void changeHappened() {}


	@Override
	public void namesChanged() {
		sizeChanged(new ChangeEvent(getDocument()));
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return Math.round(getAlignmentComparisonPanel().getCompoundHeight());  //TODO
	}

	
	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return Math.round(getAlignmentComparisonPanel().getCompoundHeight());
	}


	@Override
	protected void paintComponent(Graphics g2) {
  	super.paintComponent(g2);
  	Graphics2D g = ((Graphics2D)g2); 
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);

    Rectangle visibleRect = getVisibleRect();
		g.setColor(SystemColor.menu);
		g.fill(visibleRect);
		
		g.setColor(SystemColor.menuText);
  	g.setFont(getAlignmentComparisonPanel().getCompoundFont());
		float y = paintNames(0, g);
		paintNames(y + AlignmentComparisonPanel.ALIGNMENT_DISTANCE, g);
	}
	
	
	private float paintNames(float startY, Graphics2D g) {
		float y = startY;
		FontMetrics fm = g.getFontMetrics();
		for (int i = 0; i < getDocument().getSequenceCount(); i++) {
			g.setStroke(AlignmentPositionPanel.DASH_STROKE);
			g.draw(new Line2D.Float(0, y, getWidth(), y));

  		g.drawString(getDocument().getName(i), LABEL_DISTANCE, y + fm.getAscent());
	  	y += getAlignmentComparisonPanel().getCompoundHeight();
		}
		g.draw(new Line2D.Float(0, y, getWidth(), y));
		return y;
	}
}
