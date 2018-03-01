/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.gui.comment;


import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.tic.input.TICMouseAdapter;
import info.bioinfweb.tic.input.TICMouseEvent;

import java.awt.SystemColor;
import java.util.EnumSet;
import java.util.Set;



/**
 * Application specific LibrAlign data area that displays comments in AlignmentComparator.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class CommentArea extends DataArea {
	private CommentPositioner commentPositioner = new SingleLineCommentPositioner();  // Default strategy as long as there is no factory
	
	
	public CommentArea(AlignmentContentArea owner) {
		super(owner, null);  //TODO Specify labeled area
		
		addMouseListener(new TICMouseAdapter() {
			@Override
			public boolean mousePressed(TICMouseEvent event) {
				if (event.getClickCount() == 1) {
					if (event.isMouseButton1Down()) {
						getComparisonComponent().getSelection().setComment(getCommentPositioner().getCommentByMousePosition(
								getComparisonComponent(), 0, 0, event.getComponentX(), event.getComponentY()));
						return true;
					}
					else if (event.isMouseButton3Down()) {
						getComparisonComponent().getSelection().clearComment();
						return true;
					}
				}
				return false;
			}
		});
	}
	
	
	private AlignmentComparisonComponent getComparisonComponent() {
		return (AlignmentComparisonComponent)getOwner().getOwner().getContainer();
	}
	
	
	public CommentPositioner getCommentPositioner() {
		return commentPositioner;
	}  //TODO Future versions might also need a setter.
	
	
	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM);
	}
	

	@Override
	public double getHeight() {
		return getCommentPositioner().getNeededHeight(getComparisonComponent());
	}
	

	@Override
	public void paintPart(AlignmentPaintEvent event) {
		event.getGraphics().setColor(SystemColor.control);
		event.getGraphics().fill(event.getRectangle());
		commentPositioner.paint(getComparisonComponent(), getOwner().getOwner().getGlobalMaxSequenceLength(), event.getGraphics(), 0, 0);
	}


	@Override
	public double getLengthAfterEnd() {
		return getCommentPositioner().getNeededLengthAfterEnd(getComparisonComponent());
	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {}
	

	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}
	

	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {}
	

	@Override
	public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {}
}
