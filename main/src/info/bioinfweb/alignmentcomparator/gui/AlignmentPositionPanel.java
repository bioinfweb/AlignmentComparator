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


import info.webinsel.util.Math2;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;



public class AlignmentPositionPanel extends AlignmentComparisonHeaderPanel 
		implements Scrollable, AlignmentComparisonPanelListener {
  	
	/** The height of this panel in pixels */
	public static final int HEIGHT = 16;
	
	/** The distance on x to the left border of the component */
	public static final int X_OFFSET = 0;
	
	/** The font used for the labels */
	public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 10);
	
	/** The stroke used to paint the dashes */
	public static final Stroke DASH_STROKE = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	
  /** The length of a dash */
  public static final float DASH_LENGTH = 5;
  
  /** The length of a dash with a label */
  public static final float LABELED_DASH_LENGTH = HEIGHT;
  
	/** The distance of the labels to the border of the component */
	public static final float LABEL_TOP_DISTANCE = 9f;

	/** The distance of the labels to their dash  */
	public static final float LABEL_LEFT_DISTANCE = 2f;

	/** 
  * This string is used to test if the interval between two main dashes is smaller than 
  * the usual label text. 
  */
  public static final String LABEL_LENGTH_STANDARD = "00000";
  
  
  public AlignmentPositionPanel(AlignmentComparisonPanel alignmentComparisonPanel) {
    super(alignmentComparisonPanel);
  }
  
  
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }
  
  
  public int getScrollableBlockIncrement(Rectangle rect, int arg1, int arg2) {
    return 20; //TODO Welcher Wert ist hier sinnvoll?
  }
  
  
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  
  
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }
  
  
  public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
    return 20; //TODO Welcher Wert ist hier sinnvoll?
  }
  
  
  public void zoomChanged(ChangeEvent e) {
    repaint();
  }
  
  
  public void sizeChanged(ChangeEvent e) {
    Dimension d = getAlignmentComparisonPanel().getPreferredSize();
   	d.height = HEIGHT;
    setSize(d);
    setPreferredSize(d);
  }
  
  
  @Override
  public Dimension getMinimumSize() {
    return new Dimension(0, HEIGHT);
  }
  
  
  @Override
  protected void paintComponent(Graphics g1) {
    Graphics2D g = (Graphics2D)g1;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
    		RenderingHints.VALUE_ANTIALIAS_ON);
  
    Rectangle visibleRect = getVisibleRect();
		g.setColor(SystemColor.menu);
		g.fill(visibleRect);
		
    float compoundWidth = getAlignmentComparisonPanel().getCompoundWidth();
		g.setColor(SystemColor.menuText);
    g.draw(new Line2D.Float(visibleRect.x, HEIGHT - 1, visibleRect.x + visibleRect.width, HEIGHT - 1));  // base line
    
    // Text data:
    g.setFont(FONT);
    int labelInterval = Math2.roundUp(
    		(g.getFontMetrics().stringWidth(LABEL_LENGTH_STANDARD) + 2 * LABEL_LEFT_DISTANCE) / compoundWidth);
    float x = Math.max(X_OFFSET + compoundWidth / 2f,  
    		X_OFFSET % compoundWidth + visibleRect.x - visibleRect.x % compoundWidth - compoundWidth / 2f);
    Stroke stroke = g.getStroke();
    try {
      while (x <= visibleRect.x + visibleRect.width) {
    		// Text output
    		float dashLength = DASH_LENGTH;
    		int compoundIndex = Math.round((x - X_OFFSET) / compoundWidth); 
    		if (compoundIndex % labelInterval == 0) {
    			g.drawString("" + compoundIndex, x + LABEL_LEFT_DISTANCE, LABEL_TOP_DISTANCE);
    			dashLength = LABELED_DASH_LENGTH;
    		}
    		
      	// dash output
    		g.setStroke(DASH_STROKE);
    		Path2D path = new  Path2D.Float();
    		path.moveTo(x - 0.5, HEIGHT);
    		path.lineTo(x + 0.5, HEIGHT);
    		path.lineTo(x + 0.5, HEIGHT - dashLength);
    		path.lineTo(x - 0.5, HEIGHT - dashLength);
    		path.closePath();
    		g.fill(path);

    		x += compoundWidth;
      }
    }
    finally {
    	g.setStroke(stroke);
    }
  }
}
