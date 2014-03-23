/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document.superalignment;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.data.ProfileSuperAlignment;

import org.biojava3.alignment.template.AbstractPairwiseSequenceAligner;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class BioJavaConsensusPairwiseAligner extends ConsensusPairwiseAligner implements SuperAlignmentAlgorithm {
	private AbstractPairwiseSequenceAligner<Sequence<NucleotideCompound>, NucleotideCompound> aligner = null;
	
	
  public BioJavaConsensusPairwiseAligner(
			AbstractPairwiseSequenceAligner<Sequence<NucleotideCompound>, NucleotideCompound> aligner) {
  	
		super();
		this.aligner = aligner;
	}


	@Override
	public void performAlignment(Document document) {
		aligner.setQuery(consensusSequence(document, 0));
		aligner.setTarget(consensusSequence(document, 1));
		addSuperGaps(new ProfileSuperAlignment(aligner.getProfile()), document);
	}
}
