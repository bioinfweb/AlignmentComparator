/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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
	
	
	public ImportedAlignmentModelFactory() {
		super(new SequenceIDManager(), true);  // Reusing IDs is set to true in order to have equal IDs in all alignments for sequences with the same name.
	}


	public CharacterStateSetType getTokenType() {
		return tokenType;
	}


	public void setTokenType(CharacterStateSetType tokenType) {
		this.tokenType = tokenType;
	}


	@Override
	public AlignmentModel<Character> doCreateNewModel(NewAlignmentModelParameterMap parameterMap) {
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
		return new PackedAlignmentModel<Character>(tokenSet, getSharedIDManager(), true, tokenSet.size());
	}
}
