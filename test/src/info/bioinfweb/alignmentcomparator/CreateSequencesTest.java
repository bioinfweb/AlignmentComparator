/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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
package info.bioinfweb.alignmentcomparator;


import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.RandomValues;

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
