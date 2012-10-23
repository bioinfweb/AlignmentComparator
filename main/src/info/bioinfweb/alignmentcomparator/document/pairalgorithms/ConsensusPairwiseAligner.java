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
package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.util.ConsensusSequenceCreator;

import org.biojava3.alignment.template.AbstractPairwiseSequenceAligner;
import org.biojava3.alignment.template.AlignedSequence;
import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class ConsensusPairwiseAligner implements SuperAlignmentAlgorithm {
	private AbstractPairwiseSequenceAligner<Sequence<NucleotideCompound>, NucleotideCompound> aligner = null;
	
	
  public ConsensusPairwiseAligner(
			AbstractPairwiseSequenceAligner<Sequence<NucleotideCompound>, NucleotideCompound> aligner) {
  	
		super();
		this.aligner = aligner;
	}


	private Sequence<NucleotideCompound> consensusSequence(Document alignments, int alignmentIndex) {
		return ConsensusSequenceCreator.getInstance().majorityRuleConsensus(
				alignments.getSingleAlignment(alignmentIndex));
  }

	
	private void addSuperGaps(Profile<Sequence<NucleotideCompound>, NucleotideCompound> globalAlignment, 
			Document alignments) {
		
		for (int i = 1; i <= globalAlignment.getSize(); i++) {  // biojava indices start at 1 [...]
			AlignedSequence<Sequence<NucleotideCompound>, NucleotideCompound> sequence = 
				  globalAlignment.getAlignedSequence(i);
		  int[] indexList = new int[sequence.getLength()];
		  
		  int unalignedPos = 1;  // biojava indices start at 1 [...]
		  for (int seqPos = 0; seqPos < indexList.length; seqPos++) {  // biojava indices start at 1 [...]
				if (sequence.getCompoundAt(seqPos + 1).getBase().equals("-")) {  //TODO Gap Zeichen besser aus entsprechenden BioJava Klassen bestimmen
					indexList[seqPos] = -1;
				}
				else {
					indexList[seqPos] = unalignedPos;
					unalignedPos++;
				}
			}
		  alignments.setUnalignedIndexList(i - 1, indexList);  
		}
	}
	
  
	@Override
	public void performAlignment(Document document) {
		//TODO restliche Parameter für Aligner setzen
		aligner.setQuery(consensusSequence(document, 0));
		aligner.setTarget(consensusSequence(document, 1));
		
		//TODO Muss Alignierung noch durch speziellen Befehl durchgeführt werden?
		addSuperGaps(aligner.getProfile(), document);
	}
}
