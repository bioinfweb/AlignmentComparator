/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/AlignmentComparator>
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


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.tic.TICPaintEvent;



public class AveragePositionArea extends DataArea {
	private static final int BORDER_WIDTH = 1;
	
	private String alignmentName;
	private DecimalFormat format = new DecimalFormat("0.000");
	
	
	public AveragePositionArea(AlignmentContentArea owner, AlignmentArea labeledArea, String alignmentName) {
		super(owner, labeledArea);
		this.alignmentName = alignmentName;
	}

	
	protected DecimalFormat getFormat() {
		return format;
	}


	protected void setFormat(DecimalFormat format) {
		this.format = format;
		//TODO Update size and repaint.
	}


	protected String getAlignmentName() {
		return alignmentName;
	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {}

	
	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}

	
	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {}

	
	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}

	
	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM);
	}
	
	
	private PaintSettings getPaintSettings() {
		return ((AlignmentComparisonComponent)getOwner().getOwner().getContainer()).getFirstAlignmentArea().getPaintSettings();
	}

	
	@Override
	public int getHeight() {
		Font font = getPaintSettings().getTokenHeightFont();
		return Math2.roundUp(FontCalculator.getInstance().getWidth(font, format.format(0.0))) + 2 * BORDER_WIDTH;
	}
	

	@Override
	public void paint(TICPaintEvent event) {
		PaintSettings paintSettings = getPaintSettings(); 
		double columnWidth = paintSettings.getTokenWidth(0);
		Font font = paintSettings.getTokenHeightFont();
		List<Double> positions = 
				Main.getInstance().getMainFrame().getDocument().getAlignments().get(alignmentName).getAveragePositions();
		
		// Paint background:
		Graphics2D g = event.getGraphics();
		g.setColor(SystemColor.control);
		g.fill(event.getRectangle());

		if (positions != null) {
			// Determine area to be painted:
			int firstIndex = Math.max(0, getLabeledAlignmentArea().getContentArea().columnByPaintX((int)event.getRectangle().getMinX()));
			int lastIndex = getLabeledAlignmentArea().getContentArea().columnByPaintX((int)event.getRectangle().getMaxX());
			int lastColumn = getLabeledAlignmentModel().getMaxSequenceLength() - 1;
			if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
				lastIndex = lastColumn;
			}
	
			// Paint output:
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	  	double y = -getLabeledAlignmentArea().getContentArea().paintXByColumn(firstIndex);  // Coordinates in rotated system.
			g.setColor(SystemColor.menuText);
	  	g.setFont(font);
	  	g.rotate(0.5 * Math.PI);
			for (int column = firstIndex; column <= lastIndex; column++) {
				Double position = positions.get(column);
				if (!position.isNaN()) {
					g.drawString(format.format(position), BORDER_WIDTH, (float)y);
				}
				y -= columnWidth;
			}
		}
	}
}
