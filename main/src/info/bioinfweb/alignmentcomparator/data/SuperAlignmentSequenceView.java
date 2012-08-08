package info.bioinfweb.alignmentcomparator.data;


import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.SequenceProxyView;
import org.biojava3.core.sequence.template.SequenceView;



public class SuperAlignmentSequenceView extends SequenceProxyView<NucleotideCompound> 
    implements SequenceView<NucleotideCompound> {
	
	private Alignments parent;
	private int alignmentIndex;
	

	public SuperAlignmentSequenceView(Alignments parent, int alignmentIndex, int sequenceIndex) {
		super(parent.getSingleAlignment(alignmentIndex)[sequenceIndex]);
		this.alignmentIndex = alignmentIndex;
		this.parent = parent;
	}


	@Override
	public NucleotideCompound getCompoundAt(int position) {
		int index = parent.getUnalignedIndex(alignmentIndex, position); 
		if (index == -1) {
			return SuperAlignmentCompoundSet.getSuperAlignmentCompoundSet().getCompoundForString(
					SuperAlignmentCompoundSet.SUPER_ALIGNMENT_GAP);
		}
		else {
			return super.getCompoundAt(index);
		}
	}

	
	@Override
	public int getLength() {
		return parent.getAlignedLength();
	}
}
