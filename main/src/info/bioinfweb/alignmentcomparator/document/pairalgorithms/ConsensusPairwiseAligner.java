package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.util.ConsensusSequenceCreator;

import org.biojava3.alignment.NeedlemanWunsch;
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
		
		for (int i = 0; i < globalAlignment.getSize(); i++) {
			AlignedSequence<Sequence<NucleotideCompound>, NucleotideCompound> sequence = 
				  globalAlignment.getAlignedSequence(i);
		  int[] indexList = new int[sequence.getLength()];
		  
		  int unalignedPos = 0;
		  for (int seqPos = 0; seqPos < indexList.length; seqPos++) {
				if (sequence.getCompoundAt(seqPos).getBase().equals("-")) {  //TODO Gap Zeichen aus entsprechenden BioJava Klassen bestimmen
					indexList[seqPos] = -1;
				}
				else {
					indexList[seqPos] = unalignedPos;
					unalignedPos++;
				}
			}
		  alignments.setUnalignedIndexList(0, indexList);  
		}
	}
	
  
	@Override
	public void performAlignment(Document alignments) {
		//TODO restliche Parameter für Aligner setzen
		aligner.setQuery(consensusSequence(alignments, 0));
		aligner.setTarget(consensusSequence(alignments, 1));
		//TODO Muss Alignierung noch durch speziellen Befehl durchgeführt werden?
		addSuperGaps(aligner.getProfile(), alignments);
	}
}
