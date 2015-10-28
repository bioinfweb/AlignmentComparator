package info.bioinfweb.alignmentcomparator.gui.comments;


import java.util.EnumSet;
import java.util.Set;

import info.bioinfweb.alignmentcomparator.gui.AlignmentComparisonComponent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.tic.TICPaintEvent;



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
	public int getHeight() {
		return getCommentPositioner().getCommentDimension(getComparisonComponent()).height;
	}
	

	@Override
	public void paint(TICPaintEvent e) {
		commentPositioner.paint(getComparisonComponent(), getOwner().getOwner().getGlobalMaxSequenceLength(), e.getGraphics(), 0, 0);
	}
	
	
	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {
		// TODO Auto-generated method stub
	}
}
