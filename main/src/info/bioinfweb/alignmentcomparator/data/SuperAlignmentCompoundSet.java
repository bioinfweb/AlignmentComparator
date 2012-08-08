package info.bioinfweb.alignmentcomparator.data;


import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;



public class SuperAlignmentCompoundSet extends AlignmentAmbiguityNucleotideCompoundSet {
	public static final String SUPER_ALIGNMENT_GAP = ".";
	
	private static SuperAlignmentCompoundSet sharedInstance = null;
	
	
	/**
	 * Returns a new instance of this class.
	 */
	public SuperAlignmentCompoundSet() {
		super();
		addNucleotideCompound(SUPER_ALIGNMENT_GAP, SUPER_ALIGNMENT_GAP);
	}

	
	/**
	 * Returns a shared instance of this class.
	 */
	public static SuperAlignmentCompoundSet getSuperAlignmentCompoundSet() {
		if (sharedInstance == null) {
			sharedInstance = new SuperAlignmentCompoundSet();
		}
		return sharedInstance;
	}
}
