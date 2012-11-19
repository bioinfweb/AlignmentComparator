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


import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;



public abstract class AlignmentComparisonHeaderPanel extends JPanel 
    implements Scrollable, AlignmentComparisonPanelListener {

	private AlignmentComparisonPanel alignmentComparisonPanel = null;

	
	public AlignmentComparisonHeaderPanel(
			AlignmentComparisonPanel alignmentComparisonPanel) {

		super();
		this.alignmentComparisonPanel = alignmentComparisonPanel;
    alignmentComparisonPanel.addListener(this);
    sizeChanged(new ChangeEvent(this));
	}


	protected AlignmentComparisonPanel getAlignmentComparisonPanel() {
		return alignmentComparisonPanel;
	}


	@Override
	public void zoomChanged(ChangeEvent e) {
    repaint();
  }
}
