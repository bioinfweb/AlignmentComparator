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


import info.bioinfweb.alignmentcomparator.data.Alignments;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.event.ChangeListener;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class AlignmentComparisonPanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BG_COLOR_ID = "DEFAULT";
	public static final String BORDER_COLOR_ID = "BORDER";
	public static final String FONT_COLOR_ID = "FONT";
	
	public static final float COMPOUND_WIDTH = 10f;
	public static final float COMPOUND_HEIGHT = 14f;
	public static final float ALIGNMENT_DISTANCE = 7f;
	
	
	private Map<String, Color> colorMap = createColorMap();
	private float zoom = 1f;
	private float compoundWidth = COMPOUND_WIDTH;
	private float compoundHeight = COMPOUND_HEIGHT;
	private Alignments alignments = null;
	private List<AlignmentComparisonPanelListener> listeners = new LinkedList<AlignmentComparisonPanelListener>();
	

	private static Map<String, Color> createColorMap() {
		Map<String, Color> result = new TreeMap<String, Color>();
		result.put("A", new Color(90, 228, 93));
		result.put("T", new Color(230, 90, 90));
		result.put("C", new Color(90, 90, 230));
		result.put("G", new Color(226, 230, 90));
		result.put("-", Color.GRAY);
		result.put(".", Color.LIGHT_GRAY);
		result.put(DEFAULT_BG_COLOR_ID, Color.LIGHT_GRAY.brighter());
		result.put(BORDER_COLOR_ID, Color.WHITE);
		result.put(FONT_COLOR_ID, Color.BLACK);
		return result;
		// Bei Bedarf können alternative Farbsätze später aus einer Datei gelesen werden.
	}
	
	
	/**
	 * This is the default constructor
	 */
	public AlignmentComparisonPanel(Alignments alignments) {
		super();
		this.alignments = alignments;
	}

	
	public Map<String, Color> getColorMap() {
		return colorMap;
	}
	
	
	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
		compoundWidth = COMPOUND_WIDTH * zoom;
		compoundHeight = COMPOUND_HEIGHT * zoom;
		assignPaintSize();
		fireZoomChanged();
	}


	public float getCompoundWidth() {
		return compoundWidth;
	}


	public float getCompoundHeight() {
		return compoundHeight;
	}


	@Override
  public void stateChanged(ChangeEvent e) {
		assignPaintSize();
		repaint();
  }


	private void assignPaintSize() {
		setSize(Math.round(alignments.getAlignedLength() * getCompoundWidth()), 
				Math.round(2 * alignments.getSequenceCount() * getCompoundHeight() + getZoom() * ALIGNMENT_DISTANCE));
		setPreferredSize(getSize());  //TODO Was ist der genaue Unterschied der beiden Größen?
		fireSizeChanged();
	}
	
	
  @Override
	protected void paintComponent(Graphics g) {
  	super.paintComponent(g);
  	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);
  	paintAlignments((Graphics2D)g);
	}


	public void paintPreview(Graphics2D g, double scale) {
  	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);
  	float zoom = getZoom();
  	try {  // Wenn nebenläufige Prozesse während der Ausführung dieses Blocks den Zoom auslesen würden, käme es zu Fehlern.
  		setZoom(zoom * (float)scale);
  		paintAlignments(g);
  	}
  	finally {
  		setZoom(zoom);
  	}
	}
	
	
	private void paintAlignments(Graphics2D g) {
		paintAlignment(g, 0, 0, 0);
		paintAlignment(g, 1, 0, alignments.getSequenceCount() * getCompoundHeight() + ALIGNMENT_DISTANCE * getZoom());
	}
	

	private void paintAlignment(Graphics2D g, int alignmentIndex, float x, float y) {
		int firstIndex = Math.max(0, (int)Math.round((getVisibleRect().getMinY() - y) / getCompoundHeight()) - 1);
		int lastIndex = Math.min(alignments.getSequenceCount() - 1, (int)Math.round((getVisibleRect().getMaxY() - y) / getCompoundHeight()));
		for (int i = firstIndex; i <= lastIndex; i++) {
			paintSequence(g, alignments.getAlignedSequence(alignmentIndex, i), lastIndex, y);
	    y += getCompoundHeight();
    }
	}
	
	
	private void paintSequence(Graphics2D g, Sequence<NucleotideCompound> sequence, float x, float y) {
		int firstIndex = Math.max(0, (int)Math.round((getVisibleRect().getMinX() - x) / getCompoundWidth()) - 1);
		int lastIndex = Math.min(sequence.getLength() - 1, (int)Math.round((getVisibleRect().getMaxX() - x) / getCompoundWidth()));
		for (int i = firstIndex; i <= lastIndex; i++) {
	    paintCompound(g, sequence.getCompoundAt(i), x, y);
	    x += getCompoundWidth();
    }
	}


  private void paintCompound(Graphics2D g, NucleotideCompound compound, float x, float y) {
  	g.setColor(getColorMap().get(BORDER_COLOR_ID));
  	g.draw(new Rectangle2D.Float(x, y, getCompoundWidth(), getCompoundHeight()));
  	
  	Set<NucleotideCompound> consituents = compound.getConsituents();
  	final float height = getCompoundHeight() / (float)consituents.size();
  	Iterator<NucleotideCompound> iterator = consituents.iterator();
  	while (iterator.hasNext()) {
  		Color color = getColorMap().get(iterator.next().getBase());
  		if (color == null) {
  			color = getColorMap().get(DEFAULT_BG_COLOR_ID);
  		}
    	g.setBackground(color);
    	g.fill(new Rectangle2D.Float(x, y, getCompoundWidth(), height));
    	x += height;
  	}
  	
  	g.setColor(getColorMap().get(FONT_COLOR_ID));
  	g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(getZoom() * COMPOUND_HEIGHT * 0.7f)));
  	g.drawString(compound.getBase(), x, y);  	//TODO evtl. Verschiebung addieren
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
