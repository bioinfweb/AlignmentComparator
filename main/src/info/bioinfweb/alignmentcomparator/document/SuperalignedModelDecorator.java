package info.bioinfweb.alignmentcomparator.document;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
public class SuperalignedModelDecorator extends AbstractAlignmentModelDecorator<Character, Character> {
	public static final char SUPER_ALIGNMENT_GAP = '.';
	public static final int SUPER_GAP_INDEX = -1;
	
	
	private ComparedAlignment owner;
	private List<Integer> unalignedIndices;
	
	
	public SuperalignedModelDecorator(ComparedAlignment owner, List<Integer> unalignedIndices) {
		super(owner.getOriginal().getTokenSet().clone(), owner.getOriginal());
		this.owner = owner;
		this.unalignedIndices = unalignedIndices;
		getTokenSet().add(SUPER_ALIGNMENT_GAP);
	}
	
	
	private Collection<Character> createTokenCollection(int size) {
		Collection<Character> result = new ArrayList<Character>();
		for (int column = 0; column < size; column++) {
			result.add(SUPER_ALIGNMENT_GAP);
		}
		result = Collections.unmodifiableCollection(result);
		return result;
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
	 * @return an unmodifiable list
	 */
	public List<Integer> getUnalignedIndices() {
		return Collections.unmodifiableList(unalignedIndices);
	}
	
	
	public void insertSupergap(int start, int length) {
		// Add super gap:
		for (int pos = start; pos <= start + length - 1; pos++) {
			unalignedIndices.add(pos,	SuperalignedModelDecorator.SUPER_GAP_INDEX);
		}
		
		// Fire change events:
		Collection<Character> addedTokens = createTokenCollection(length);
		Iterator<Integer> iterator = sequenceIDIterator();
		while (iterator.hasNext()) {  //TODO Will all sequences be repainted for each event or only the affected sequence area?
			fireAfterTokenChange(TokenChangeEvent.newInsertInstance(this, iterator.next(), start, addedTokens));
		}
	}


	public void removeSupergap(int start, int length) {
		// Add super gap:
		for (int pos = start; pos <= start + length - 1; pos++) {
			if (unalignedIndices.get(start) == SuperalignedModelDecorator.SUPER_GAP_INDEX) {
				unalignedIndices.remove(start);
			}
			else {
				throw new IllegalArgumentException("Removing a supergap from index " + start + 
						" is not possible, because there is no supergap present at this position, but a reference to position " + 
						unalignedIndices.get(start) + " in the underlying alignment.");
			}
		}
		
		// Fire change events:
		Collection<Character> removedTokens = createTokenCollection(length);
		Iterator<Integer> iterator = sequenceIDIterator();
		while (iterator.hasNext()) {  //TODO Will all sequences be repainted for each event or only the affected sequence area?
			fireAfterTokenChange(TokenChangeEvent.newRemoveInstance(this, iterator.next(), start, removedTokens));
		}
	}
	
	
	public boolean containsSupergap(int column) {
		return unalignedIndices.get(column) == SUPER_GAP_INDEX;
	}


	/**
	 * Checks if the specified range of columns contains only supergaps.
	 * 
	 * @param startColumn the index of the first column to be checked
	 * @param endColumn the index behind the last column to be checked
	 * @return {@code true} if all columns contains supergaps, {@code false} if at least one column does not
	 */
	public boolean containsSupergap(int startColumn, int endColumn) {
		for (int column = startColumn; column < endColumn; column++) {
			if (!containsSupergap(column)) {
				return false;
			}
		}
		return true;
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
}
