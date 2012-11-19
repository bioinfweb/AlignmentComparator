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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;



public class SequenceNamesPanel extends AlignmentComparisonHeaderPanel 
    implements Scrollable, AlignmentComparisonPanelListener, DocumentListener {
	
	/** The distance of the labels to the border of the component */
	public static final float LABEL_DISTANCE = 2f;

	
	private Document document = null;
	
	
  public SequenceNamesPanel(AlignmentComparisonPanel alignmentComparisonPanel,
			Document document) {
	
  	super(alignmentComparisonPanel);
		this.document = document;
		document.addDocumentListener(this);
	}


  protected Document getDocument() {
		return document;
	}


	private float calculateWidth(Graphics2D  g) {
		FontMetrics fm = g.getFontMetrics();
		float maxLength = 0;
		for (int i = 0; i < getDocument().getSequenceCount(); i++) {
			maxLength = Math.max(maxLength, fm.stringWidth(getDocument().getName(i))); 
		}
  	return maxLength + 2 * LABEL_DISTANCE;
  }
  

	public void sizeChanged(ChangeEvent e) {
    Dimension d = getAlignmentComparisonPanel().getPreferredSize();
    //TODO Breite entsprechend des länsten Namens setzen
    setSize(d);
    setPreferredSize(d);
	}

	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
