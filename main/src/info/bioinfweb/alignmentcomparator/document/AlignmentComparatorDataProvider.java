package info.bioinfweb.alignmentcomparator.document;


import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.libralign.sequenceprovider.implementations.AbstractUnmodifyableSequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;



/**
 * The sequence data provider implementation used to display a single alignment in the super
 * alignment of AlignmentComparator.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> - the token type of the compared alignments
 */
public class AlignmentComparatorDataProvider<T extends Compound> extends AbstractUnmodifyableSequenceDataProvider<Sequence<T>, T> {
	public AlignmentComparatorDataProvider(TokenSet<T> tokenSet) {
		super(tokenSet);
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		return getSequence(sequenceID).getLength();
	}


	@Override
	public T getTokenAt(int sequenceID, int index) {
		return getSequence(sequenceID).getCompoundAt(index);
	}


	/**
	 * Always returns {@code null} since the result of this method will anyway directly be replaced by 
	 * another sequence object in {@link #addSequence(String, Object)}.
	 * 
	 * @return {@code null}
	 */
	@Override
	protected Sequence<T> createNewSequence(int sequenceID, String sequenceName) {
		return null;
	}
}
