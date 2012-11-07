package info.bioinfweb.alignmentcomparator.document.pairalgorithms.superalignment;

import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class ProfileSuperAlignment implements SuperAlignment {
	private Profile<Sequence<NucleotideCompound>, NucleotideCompound> profile;
	

	public ProfileSuperAlignment(
			Profile<Sequence<NucleotideCompound>, NucleotideCompound> profile) {
		super();
		this.profile = profile;
	}


	@Override
	public boolean containsGap(int seqIndex, int charIndex) {
		return profile.getAlignedSequence(seqIndex + 1).getCompoundAt(charIndex + 1).getBase().equals(GAP_CHAR);  //TODO Gap Zeichen besser aus entsprechenden BioJava Klassen bestimmen
	}


	@Override
	public int sequenceCount() {
		return profile.getSize();
	}


	@Override
	public int sequenceLength(int seqIndex) {
		return profile.getAlignedSequence(seqIndex + 1).getLength();
	}
}
