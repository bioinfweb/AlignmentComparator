package info.bioinfweb.alignmentcomparator;


import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.util.AmbiguityBaseScore;
import info.webinsel.util.RandomValues;

import org.biojava3.core.sequence.BasicSequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.junit.* ;

import static org.junit.Assert.* ;


public class CreateSequencesTest {
	@Test
  public void createDNA() {
		BasicSequence<NucleotideCompound> sequence = new BasicSequence<NucleotideCompound>("ATCG", 
				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet());
		//System.out.println(sequence);
		assertEquals("ATCG", sequence.getSequenceAsString());
	}
	
	
	@Test
  public void createAmbiguity() {
		String test = "ATCGYRWSKMBDHVN";
		BasicSequence<NucleotideCompound> sequence = new BasicSequence<NucleotideCompound>(test, 
				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet());
		//System.out.println(sequence);
		assertEquals(test, sequence.getSequenceAsString());
	}
	
	
	@Test
  public void createLongAmbiguity() {
		String test = RandomValues.randChars("ATCGYRWSKMBDHVN", 6000);
		BasicSequence<NucleotideCompound> sequence = new BasicSequence<NucleotideCompound>(test, 
				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet());
		assertEquals(test, sequence.getSequenceAsString());
		System.out.println(sequence.getCompoundAt(5000).getBase());
	}
	
	
	@Test
  public void createLongAmbiguityWithGaps() {
		String test = RandomValues.randChars("ATCGYRWSKMBDHVN-", 6000);
		BasicSequence<NucleotideCompound> sequence = new BasicSequence<NucleotideCompound>(test, 
				AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet());
		assertEquals(test, sequence.getSequenceAsString());
		System.out.println(sequence.getCompoundAt(5000).getBase());
	}
}
