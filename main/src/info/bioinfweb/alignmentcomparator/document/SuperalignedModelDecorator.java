/*
	 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document;


import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.implementations.decorate.AbstractAlignmentModelDecorator;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



/**
 * Alignment model decorator that provides the superaligned version of another alignment model.
 * <p>
 * Instances of this class do not allow direct editing of sequences or tokens. The only valid
 * modification is changing the index mapping by inserting or removing super gaps from
 * {@link #getUnalignedIndices()}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class SuperalignedModelDecorator extends AbstractAlignmentModelDecorator<Character, Character> implements TranslatableAlignment {
	public static final char SUPER_ALIGNMENT_GAP = '.';
	public static final int SUPER_GAP_INDEX = -1;  //TODO Replace by IndexRelation.GAP
	
	
	private ComparedAlignment owner;
	private List<Integer> unalignedIndices;
	private SuperalingedModelIndexTranslator translator;
	
	
	public SuperalignedModelDecorator(ComparedAlignment owner, List<Integer> unalignedIndices) {
		super(owner.getOriginal().getTokenSet().clone(), owner.getOriginal());
		this.owner = owner;
		this.unalignedIndices = unalignedIndices;
		convertUnalignedIndices();
		getTokenSet().add(SUPER_ALIGNMENT_GAP);
		translator = new SuperalingedModelIndexTranslator(this, unalignedIndices);
	}
	
	
	private void convertUnalignedIndices() {
		int lastPosition = IndexRelation.OUT_OF_RANGE;
		for (int i = 0; i < unalignedIndices.size(); i++) {
			if (unalignedIndices.get(i) == SUPER_GAP_INDEX) {
				unalignedIndices.set(i, lastPosition);
			}
			else {
				lastPosition = unalignedIndices.get(i);
			}
		}
	}
	
	
	@Override
	public OriginalAlignment getUnderlyingModel() {
		return (OriginalAlignment)super.getUnderlyingModel();
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
	 * Subsequent entries refering to the same column, indicate that all but the first entry are part
	 * of a supergap.
	 * 
	 * @return an unmodifiable list
	 */
	public List<Integer> getUnalignedIndices() {
		return Collections.unmodifiableList(unalignedIndices);
	}
	
	
	private Collection<Character> createTokenCollection(int size) {
		Collection<Character> result = new ArrayList<Character>();
		for (int column = 0; column < size; column++) {
			result.add(SUPER_ALIGNMENT_GAP);
		}
		result = Collections.unmodifiableCollection(result);
		return result;
	}
	
	
	public void insertSupergap(int start, int length) {
		// Add super gap:
		int previousIndex = IndexRelation.OUT_OF_RANGE;
		if ((start > 0) && !unalignedIndices.isEmpty()) {
			previousIndex = unalignedIndices.get(start - 1);
		}
		for (int pos = start; pos <= start + length - 1; pos++) {
			unalignedIndices.add(pos,	previousIndex);
		}
		
		// Fire change events:
		Collection<Character> addedTokens = createTokenCollection(length);
		Iterator<String> iterator = sequenceIDIterator();
		while (iterator.hasNext()) {  //TODO Will all sequences be repainted for each event or only the affected sequence area?
			fireAfterTokenChange(TokenChangeEvent.newInsertInstance(this, iterator.next(), start, addedTokens));
		}
	}


	public void removeSupergap(int start, int length) {
		// Add super gap:
		for (int pos = start; pos <= start + length - 1; pos++) {
			if (containsSupergap(start)) {
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
		Iterator<String> iterator = sequenceIDIterator();
		while (iterator.hasNext()) {  //TODO Will all sequences be repainted for each event or only the affected sequence area?
			fireAfterTokenChange(TokenChangeEvent.newRemoveInstance(this, iterator.next(), start, removedTokens));
		}
	}
	
	
	public boolean containsSupergap(int column) {
		int unalignedIndex = unalignedIndices.get(column);
		return (unalignedIndex == IndexRelation.OUT_OF_RANGE) || ((column > 0) && (unalignedIndices.get(column - 1) == unalignedIndex));
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
	public int getSequenceLength(String sequenceID) {
		return unalignedIndices.size();
	}


	@Override
	public int getMaxSequenceLength() {
		return unalignedIndices.size();
	}


	@Override
	public Character getTokenAt(String sequenceID, int index) {
		if (containsSupergap(index)) {
			return SUPER_ALIGNMENT_GAP;
		}
		else {
			return getUnderlyingModel().getTokenAt(sequenceID, unalignedIndices.get(index));
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
	public String renameSequence(String sequenceID, String newSequenceName) throws AlignmentSourceNotWritableException,
			SequenceNotFoundException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokenAt(String sequenceID, int index, Character token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(String sequenceID, int beginIndex,	Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void appendToken(String sequenceID, Character token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void appendTokens(String sequenceID, Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(String sequenceID, int index, Character token)	throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends Character> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(String sequenceID, int index) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public IndexTranslator<Character> getIndexTranslator() {
		return translator;
	}
}
