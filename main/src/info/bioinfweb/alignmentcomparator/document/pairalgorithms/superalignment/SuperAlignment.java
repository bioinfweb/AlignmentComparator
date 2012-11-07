package info.bioinfweb.alignmentcomparator.document.pairalgorithms.superalignment;



public interface SuperAlignment {
	public static final char GAP_CHAR = '-';
	
  public boolean containsGap(int seqIndex, int charIndex);
  
  public int sequenceCount();
  
  public int sequenceLength(int seqIndex);
}
