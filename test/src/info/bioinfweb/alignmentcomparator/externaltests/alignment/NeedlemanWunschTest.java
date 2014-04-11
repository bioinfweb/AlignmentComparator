package info.bioinfweb.alignmentcomparator.externaltests.alignment;


import java.util.Iterator;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.NucleotideCompoundSet;

import org.biojava3.alignment.NeedlemanWunsch;
import org.biojava3.alignment.SimpleGapPenalty;
import org.biojava3.alignment.SimpleSubstitutionMatrix;
import org.biojava3.alignment.template.AlignedSequence;
import org.biojava3.alignment.template.GapPenalty;
import org.biojava3.alignment.template.Profile;
import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;



public class NeedlemanWunschTest {
	public static void align(String seq1, String seq2) {
		GapPenalty gapPenalty = new SimpleGapPenalty((short)0, (short)-1);
		SubstitutionMatrix<NucleotideCompound> substitutionMatrix = 
			  new SimpleSubstitutionMatrix<NucleotideCompound>(NucleotideCompoundSet.getNucleotideCompoundSet(), 
			  		(short)1, (short)-1);
		NeedlemanWunsch<DNASequence, NucleotideCompound> needlemanWunsch = 
			  new NeedlemanWunsch<DNASequence, NucleotideCompound>();
		needlemanWunsch.setGapPenalty(gapPenalty);
		needlemanWunsch.setSubstitutionMatrix(substitutionMatrix);
		needlemanWunsch.setQuery(new DNASequence(seq1));
		needlemanWunsch.setTarget(new DNASequence(seq2));
		Profile<DNASequence, NucleotideCompound> profile = needlemanWunsch.getProfile();
		
		Iterator<AlignedSequence<DNASequence, NucleotideCompound>> iterator = profile.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
	
	
  public static void main(String[] args) {
  	//align("ATCG", "AGG");
  	align("ATCGATTATTATTATTACGTTGAC", "ATCGATTATTATTACGTTGAC");
  	//TODO Manuelle Implementierung unter http://www.java-uni.de/index.php?Seite=87 vergleichen!
	}
}
