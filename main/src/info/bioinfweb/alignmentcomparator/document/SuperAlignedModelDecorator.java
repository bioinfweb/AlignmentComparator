package info.bioinfweb.alignmentcomparator.document;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeAdapter;
import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.commons.collections.observable.ObservableList;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.implementations.decorate.AbstractAlignmentModelDecorator;



/**
 * Alignment model decorator that provides the superaligned version of another alignment model.
 * <p>
 * Instances of this class do not provide direct editing of sequences or tokens. The only allowed
 * modification is changing the index mapping but inserting or removing super gaps from
 * {@link #getUnalignedIndices()}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class SuperAlignedModelDecorator extends AbstractAlignmentModelDecorator<Character, Character> {
	public static final char SUPER_ALIGNMENT_GAP = '.';
	public static final int SUPER_GAP_INDEX = -1;
	
	
	private ComparedAlignment owner;
	private ObservableList<Integer> unalignedIndices;
	
	
	public SuperAlignedModelDecorator(ComparedAlignment owner) {
		super(owner.getOriginal().getTokenSet().clone(), owner.getOriginal());
		this.owner = owner;
		getTokenSet().add(SUPER_ALIGNMENT_GAP);
		
	}
	
	
	private void createUnlignedIndexList() {
		unalignedIndices = new ObservableList<Integer>(new ArrayList<Integer>());
		
		final SuperAlignedModelDecorator thisModel = this;
		unalignedIndices.addListChangeListener(new ListChangeAdapter<Integer>() {
			private Queue<TokenChangeEvent<Character>> waitingEvents = new ArrayDeque<TokenChangeEvent<Character>>();
			
			@Override
			public void beforeElementsRemoved(ListRemoveEvent<Integer, Object> event) {
				waitingEvents.clear();
				List<Character> removedTokens = new ArrayList<Character>();
				Iterator<Integer> idIterator = sequenceIDIterator();
				while (idIterator.hasNext()) {
					int id = idIterator.next();
					removedTokens.clear();
					int end = event.getIndex() + event.getAffectedElements().size();
					for (int column = event.getIndex(); column < end; column++) {
						removedTokens.add(getTokenAt(id, column));
					}
					waitingEvents.add(TokenChangeEvent.newRemoveInstance(thisModel, id, event.getIndex(), 
							Collections.unmodifiableCollection(removedTokens)));
				}
			}
			
			@Override
			public void afterElementsRemoved(ListRemoveEvent<Integer, Integer> event) {
				while (!waitingEvents.isEmpty()) {
					fireAfterTokenChange(waitingEvents.poll());
				}
			}
			
			@Override
			public void afterElementsAdded(ListAddEvent<Integer> event) {
				// TODO Auto-generated method stub
				//event.getIndex()
			}
			
			@Override
			public void afterElementReplaced(ListReplaceEvent<Integer> event) {
				//event.get
				// TODO Auto-generated method stub
				
			}
		});
	}


	public ComparedAlignment getOwner() {
		return owner;
	}


	@Override
	protected Iterable<TokenChangeEvent<Character>> convertTokenChangeEvent(TokenChangeEvent<Character> event) {
		throw new InternalError("Operation not supported");
	}


	/**
	 * A list that maps each column in this superalignment model to a column in the underlying model.
	 * Entries for columns filled with supergaps contain {@link #SUPER_GAP_INDEX}.
	 * 
	 * @return the index of the according row in the underlying model of {@link #SUPER_GAP_INDEX}
	 */
	public List<Integer> getUnalignedIndices() {
		return unalignedIndices;
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		return unalignedIndices.size();
	}


	@Override
	public int getMaxSequenceLength() {
		return unalignedIndices.size();
	}


	@Override
	public Character getTokenAt(int sequenceID, int index) {
		int unalignedIndex = unalignedIndices.get(index);
		if (unalignedIndex == SUPER_GAP_INDEX) {
			return SUPER_ALIGNMENT_GAP;
		}
		else {
			return getUnderlyingModel().getTokenAt(sequenceID, unalignedIndex);
		}
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return AlignmentModelWriteType.NONE;  // Although inserting whole columns of super gaps is possible. 
	}


	@Override
	public boolean isTokensReadOnly() {
		return true;
	}


	@Override
	public String renameSequence(int sequenceID, String newSequenceName) throws AlignmentSourceNotWritableException,
			DuplicateSequenceNameException, SequenceNotFoundException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokenAt(int sequenceID, int index, Character token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(int sequenceID, int beginIndex,	Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void appendToken(int sequenceID, Character token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void appendTokens(int sequenceID, Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(int sequenceID, int index, Character token)	throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(int sequenceID, int index) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public Set<AlignmentModelChangeListener> getChangeListeners() {
		return getOwner().getOriginal().getChangeListeners();  //TODO Replace by other implementation.
	}
}
