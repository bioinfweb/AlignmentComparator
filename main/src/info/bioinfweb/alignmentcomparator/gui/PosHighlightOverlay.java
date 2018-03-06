/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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


import java.awt.Color;

import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.SuperalingedModelIndexTranslator;
import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.ColorOverlay;



public class PosHighlightOverlay implements ColorOverlay {
	@Override
	public Color getColor(AlignmentArea alignmentArea, String sequenceID, int columnIndex) {
		// The focused area seems to be set after this method is called the first time (after the first click in an area). 
		// Therefore the wrong areas would contain highlights then. This should not be a problem, since the first click can 
		// only generate a selection of with 0, which does not lead to a highlighting.
		
		AlignmentArea focusedArea = alignmentArea.getContainer().getFocusedAlignmentArea();
		
		if ((focusedArea != null) && (focusedArea != alignmentArea) && (focusedArea.getSelection().getWidth() > 0)) {
			SuperalingedModelIndexTranslator otherTranslator = 
					((SuperalignedModelDecorator)focusedArea.getAlignmentModel()).getIndexTranslator();
			int start = otherTranslator.getUnalignedIndex(sequenceID, focusedArea.getSelection().getFirstColumn()).getAfter();
			int end = otherTranslator.getUnalignedIndex(sequenceID, focusedArea.getSelection().getLastColumn()).getBefore();
			
			if ((start > 0) && (end > 0)) {  // There cannot be a respective position if after the start or before the end are out of range.
				SuperalingedModelIndexTranslator ownTranslator = 
						((SuperalignedModelDecorator)alignmentArea.getAlignmentModel()).getIndexTranslator();
				if (Math2.isBetween(columnIndex, ownTranslator.getAlignedIndex(sequenceID, start), ownTranslator.getAlignedIndex(sequenceID, end))) {
					return AlignmentComparisonComponent.HIGHLIGHT_COLOR;
				}
			}
		}
		return null;
	}
}
