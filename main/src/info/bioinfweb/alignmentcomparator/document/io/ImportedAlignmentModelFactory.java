package info.bioinfweb.alignmentcomparator.document.io;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.AbstractAlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



public class ImportedAlignmentModelFactory extends AbstractAlignmentModelFactory<Character> {  //TODO If future versions support custom token sets, String would be an alternative token type.
	private CharacterStateSetType tokenType = CharacterStateSetType.NUCLEOTIDE;
	
	
	public ImportedAlignmentModelFactory(SequenceIDManager sharedIDManager) {
		super(sharedIDManager);
	}


	public CharacterStateSetType getTokenType() {
		return tokenType;
	}


	public void setTokenType(CharacterStateSetType tokenType) {
		this.tokenType = tokenType;
	}


	@Override
	public AlignmentModel<Character> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		TokenSet<Character> tokenSet;
		switch (tokenType) {
			case NUCLEOTIDE:
				tokenSet = CharacterTokenSet.newNucleotideInstance();
				break;
			case AMINO_ACID:
				tokenSet = CharacterTokenSet.newAminoAcidInstance();
				break;
			//TODO Support other types in future versions.
			default:
				throw new InternalError("An unsupported token type was specified for this comparison. Please inform the AlignmentComparator developers on this error.");
		}
		return new PackedAlignmentModel<Character>(tokenSet, getSharedIDManager(), tokenSet.size());
	}
}
