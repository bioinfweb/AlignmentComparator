package info.bioinfweb.alignmentcomparator.document.pairalgorithms.superalignment;



public class CharSequenceSuperAlignment implements SuperAlignment {
  private CharSequence[] sequences;

  
	public CharSequenceSuperAlignment(CharSequence[] sequences) {
		super();
		this.sequences = sequences;
	}


	@Override
	public boolean containsGap(int seqIndex, int charIndex) {
		return sequences[seqIndex].charAt(charIndex) == GAP_CHAR;
	}


	@Override
	public int sequenceCount() {
		return sequences.length;
	}


	@Override
	public int sequenceLength(int seqIndex) {
		return sequences[seqIndex].length();
	}
}
