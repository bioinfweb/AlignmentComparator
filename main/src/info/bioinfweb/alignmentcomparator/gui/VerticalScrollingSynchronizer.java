/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.scrolling.ScrollingTICComponent;
import info.bioinfweb.tic.scrolling.TICScrollEvent;
import info.bioinfweb.tic.scrolling.TICScrollListener;



/**
 * Used to synchronize vertical scrolling of the {@link AlignmentArea}s displaying the single alignments.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class VerticalScrollingSynchronizer implements TICScrollListener {
	private AlignmentComparisonComponent owner;
	private boolean syncOngoing = false;

	
	public VerticalScrollingSynchronizer(AlignmentComparisonComponent owner) {
		super();
		this.owner = owner;
	}


	@Override
	public void contentScrolled(TICScrollEvent event) {
		if (!syncOngoing) {  // Avoid to recursively react on scroll events triggered by synchronization. 
			syncOngoing = true;
			try {
				ScrollingTICComponent source = event.getSource().getIndependentComponent();
				for (AlignmentArea area : owner.getComparisonAlignmentAreas()) {
					if (source != area) {
						area.setScrollOffsetY(source.getScrollOffsetY());
					}
				}
			}
			finally {
				syncOngoing = false;
			}
		}
	}
}
