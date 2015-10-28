/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
 * <http://bioinfweb.info/Software>
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
package info.bioinfweb.alignmentcomparator.document.superalignment.data;


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
		return profile.getAlignedSequence(seqIndex + 1).getCompoundAt(charIndex + 1).getBase().equals(GAP_CHAR);
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
