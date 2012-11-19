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
import info.bioinfweb.alignmentcomparator.gui.comments.CommentPositioner;
import info.bioinfweb.alignmentcomparator.gui.comments.SingleLineCommentPositioner;
import info.webinsel.util.Math2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class AlignmentComparisonPanel extends JPanel implements DocumentListener {
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BG_COLOR_ID = "DEFAULT";
	public static final String COMPOUND_BORDER_COLOR_ID = "COMPOUND_BORDER";
	public static final String FONT_COLOR_ID = "FONT";
	public static final String COMMENT_BORDER_COLOR_ID = "COMMENT_BORDER";
	public static final String COMMENT_OVERLAPPING_BORDER_COLOR_ID = "COMMENT_BORDER_2";
	
	public static final float COMPOUND_WIDTH = 10f;
	public static final float COMPOUND_HEIGHT = 14f;
	public static final float ALIGNMENT_DISTANCE = 7f;
	public static final float COMMENTS_DISTANCE = 7f;
	
	
	private Map<String, Color> colorMap = createColorMap();
	private float zoom = 1f;
	private float compoundWidth = COMPOUND_WIDTH;
	private float compoundHeight = COMPOUND_HEIGHT;
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(COMPOUND_HEIGHT * 0.7f));
	private Document document = null;
	private AlignmentComparisonPanelSelection selection = new AlignmentComparisonPanelSelection();
	private List<AlignmentComparisonPanelListener> listeners = new LinkedList<AlignmentComparisonPanelListener>();
	private CommentPositioner commentPositioner = new SingleLineCommentPositioner();  // Default strategy as long as there is no factory
	

	private static Map<String, Color> createColorMap() {
		Map<String, Color> result = new TreeMap<String, Color>();
		result.put("A", new Color(90, 228, 93));
		result.put("T", new Color(230, 90, 90));
		result.put("C", new Color(90, 90, 230));
		result.put("G", new Color(226, 230, 90));
		result.put("-", Color.GRAY);
		result.put(".", Color.LIGHT_GRAY);
		result.put(DEFAULT_BG_COLOR_ID, Color.LIGHT_GRAY.brighter());
		result.put(COMPOUND_BORDER_COLOR_ID, Color.WHITE);
		result.put(FONT_COLOR_ID, Color.BLACK);
		result.put(COMMENT_BORDER_COLOR_ID, Color.BLUE.brighter());
		result.put(COMMENT_OVERLAPPING_BORDER_COLOR_ID, Color.GRAY);
		return result;
		// Bei Bedarf können alternative Farbsätze später aus einer Datei gelesen werden.
	}
	
	
	/**
	 * This is the default constructor
	 */
	public AlignmentComparisonPanel(Document document) {
		super();
		this.document = document;  //TODO Bleibt es während der gesamten Laufzeit die selbe Instanz?
	}

	
	public Map<String, Color> getColorMap() {
		return colorMap;
	}

	
	public AlignmentComparisonPanelSelection getSelection() {
		return selection;
	}


	public CommentPositioner getCommentPositioner() {
		return commentPositioner;
	}  //TODO Future versions might also need a setter.


	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
		compoundWidth = COMPOUND_WIDTH * zoom;
		compoundHeight = COMPOUND_HEIGHT * zoom;
		font = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(zoom * COMPOUND_HEIGHT * 0.7f));
		assignPaintSize();
		fireZoomChanged();
	}


	public float getCompoundWidth() {
		return compoundWidth;
	}


	public float getCompoundHeight() {
		return compoundHeight;
	}
	
	
	public Font getFont() {
		return font;
	}


	@Override
	public void changeHappened() {
		assignPaintSize();
		repaint();
	}


	private void assignPaintSize() {
		Dimension commentSize = getCommentPositioner().getCommentDimension(document.getComments(), this);
		setSize(Math.max(Math2.roundUp(document.getAlignedLength() * getCompoundWidth()), commentSize.width), 
				Math2.roundUp(2 * document.getSequenceCount() * getCompoundHeight() + 
						getZoom() * (ALIGNMENT_DISTANCE + COMMENTS_DISTANCE) + commentSize.height));
		setPreferredSize(getSize());  // Show everything, if possible
		fireSizeChanged();
	}
	
	
  @Override
	protected void paintComponent(Graphics g) {
  	super.paintComponent(g);
  	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);
  	paintAlignments((Graphics2D)g);
  	paintComments((Graphics2D)g);
	}


	public void paintPreview(Graphics2D g, double scale) {
  	float zoom = getZoom();
  	try {  // Wenn nebenläufige Prozesse während der Ausführung dieses Blocks den Zoom auslesen würden, käme es zu Fehlern.
  		setZoom(zoom * (float)scale);
  		paintComponent(g);
  	}
  	finally {
  		setZoom(zoom);
  	}
	}
	
	
	private void paintAlignments(Graphics2D g) {
		paintAlignment(g, 0, 0, 0);
		paintAlignment(g, 1, 0, document.getSequenceCount() * getCompoundHeight() + ALIGNMENT_DISTANCE * getZoom());
	}
	

	private void paintAlignment(Graphics2D g, int alignmentIndex, float x, float y) {
		int firstIndex = Math.max(0, (int)Math.round((getVisibleRect().getMinY() - y) / getCompoundHeight()) - 1);
		int lastIndex = Math.min(document.getSequenceCount() - 1, (int)Math.round((getVisibleRect().getMaxY() - y) / getCompoundHeight()));
		for (int i = firstIndex; i <= lastIndex; i++) {
			paintSequence(g, document.getAlignedSequence(alignmentIndex, i), lastIndex, y);
	    y += getCompoundHeight();
    }
	}
	
	
	private void paintSequence(Graphics2D g, Sequence<NucleotideCompound> sequence, float x, float y) {
		int firstIndex = Math.max(1, (int)Math.round((getVisibleRect().getMinX() - x) / getCompoundWidth()) - 1);  // BioJava index starts with 1
		int lastIndex = Math.min(sequence.getLength() - 1, (int)Math.round((getVisibleRect().getMaxX() - x) / getCompoundWidth()));
  	x += firstIndex * getCompoundWidth();
		for (int i = firstIndex; i <= lastIndex; i++) {
	    paintCompound(g, sequence.getCompoundAt(i), x, y);
	    x += getCompoundWidth();
    }
	}


  private void paintCompound(Graphics2D g, NucleotideCompound compound, float x, float y) {
  	g.setColor(getColorMap().get(COMPOUND_BORDER_COLOR_ID));
  	g.draw(new Rectangle2D.Float(x, y, getCompoundWidth(), getCompoundHeight()));
  	
  	Set<NucleotideCompound> consituents = compound.getConsituents();
  	final float height = getCompoundHeight() / (float)consituents.size();
  	Iterator<NucleotideCompound> iterator = consituents.iterator();
  	float bgY = y;
  	while (iterator.hasNext()) {  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used.
  		Color color = getColorMap().get(iterator.next().getUpperedBase());
  		if (color == null) {
  			color = getColorMap().get(DEFAULT_BG_COLOR_ID);
  		}
  		g.setColor(color);
    	g.fill(new Rectangle2D.Float(x, bgY, getCompoundWidth(), height));
    	bgY += height;
  	}
  	
  	g.setColor(getColorMap().get(FONT_COLOR_ID));
  	g.setFont(getFont());
		FontMetrics fm = g.getFontMetrics();
  	g.drawString(compound.getBase(), x + 0.5f * (getCompoundWidth() - fm.charWidth(compound.getBase().charAt(0))), y + fm.getAscent());
  }
  
  
  public void paintComments(Graphics2D g) {
  	//TODO Make sure that positioning was done
  	getCommentPositioner().paint(document.getComments(), this, document.getAlignedLength(), g, 0, 
  			2 * document.getSequenceCount() * getCompoundHeight() +	getZoom() * (ALIGNMENT_DISTANCE + COMMENTS_DISTANCE));
  }


	public boolean addListener(AlignmentComparisonPanelListener listener) {
	  return listeners.add(listener);
  }


	public boolean removeListener(Object listener) {
	  return listeners.remove(listener);
  }


	public void fireZoomChanged() {
		Iterator<AlignmentComparisonPanelListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().zoomChanged(new ChangeEvent(this));
		}
	}
	
	
	public void fireSizeChanged() {
		Iterator<AlignmentComparisonPanelListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().sizeChanged(new ChangeEvent(this));
		}
	}
}
