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


import java.util.ArrayList;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperAlignmentSequenceView;
import info.bioinfweb.alignmentcomparator.document.superalignment.data.SuperAlignment;
import info.bioinfweb.commons.bio.ConsensusSequenceCreator;



public abstract class ConsensusPairwiseAligner implements SuperAlignmentAlgorithm {
	protected void addSuperGaps(SuperAlignment globalAlignment,	Document alignments) {
		for (int i = 0; i < globalAlignment.sequenceCount(); i++) {
			ArrayList<Integer> indexList = new ArrayList<Integer>(globalAlignment.sequenceLength(i));
		  
		  int unalignedPos = 1;  // biojava indices start at 1 [...]
		  for (int seqPos = 0; seqPos < globalAlignment.sequenceLength(i); seqPos++) {
				if (globalAlignment.containsGap(i, seqPos)) {
					indexList.add(SuperAlignmentSequenceView.GAP_INDEX);
				}
				else {
					indexList.add(unalignedPos);
					unalignedPos++;
				}
			}
		  alignments.setUnalignedIndexList(i, indexList);  
		}
	}
	
	
	protected Sequence<NucleotideCompound> consensusSequence(Document alignments, int alignmentIndex) {
		//TODO reimplement if necessary
		return null;
//		return ConsensusSequenceCreator.getInstance().majorityRuleConsensus(
//				alignments.getSingleAlignment(alignmentIndex));
  }
}
