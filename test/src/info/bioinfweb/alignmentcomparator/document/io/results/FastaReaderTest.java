/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
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
package info.bioinfweb.alignmentcomparator.document.io.results;


import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.DNASequenceCreator;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;
import org.biojava3.core.sequence.io.template.FastaHeaderParserInterface;
import org.biojava3.core.sequence.template.Sequence;



public class FastaReaderTest {
	private static File file = new File("C:\\Users\\BenStoever\\Documents\\Studium\\Projekte\\Promotion\\AlignmentEvaluation\\Testdaten\\Alignment1.fas");
	
	
	public static void helperDNASequence_test() {
		try {
			Map<String, DNASequence> map = FastaReaderHelper.readFastaDNASequence(file);
      output(map);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void readerDNASequence_test() {
    try{ 
    	FastaReader<DNASequence, NucleotideCompound> fastaReader = 
    	    new FastaReader<DNASequence, NucleotideCompound>(
	    	  		new BufferedInputStream(new FileInputStream(file)),
	    	  		new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
//	    	  		new FastaHeaderParserInterface<DNASequence, NucleotideCompound>() {  // Funktioniert nicht
//								@Override
//								public void parseHeader(String text, DNASequence sequence) {}
//							},
	            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was w�rde DNASequenceCreator anders machen? 
	    
			Map<String, DNASequence> map = fastaReader.process();
	    output(map);
    }
    catch (Exception e) {
    	e.printStackTrace();
    }
	}
	
	
	public static void readerSequence_test() {
    try{ 
    	FastaReader<Sequence<NucleotideCompound>, NucleotideCompound> fastaReader = 
    	    new FastaReader<Sequence<NucleotideCompound>, NucleotideCompound>(
	    	  		new BufferedInputStream(new FileInputStream(file)),
	    	  		//new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
	    	  		new FastaHeaderParserInterface<Sequence<NucleotideCompound>, NucleotideCompound>() {
	    	  			public void parseHeader(String header, Sequence<NucleotideCompound> sequence) {}
							},
	            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was w�rde DNASequenceCreator anders machen? 
	    
			Map<String, Sequence<NucleotideCompound>> map = fastaReader.process();
	    output(map);
    }
    catch (Exception e) {
    	e.printStackTrace();
    }
	}
	
	
	public static void output(Map map) {
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			System.out.println(key + ": " + map.get(key));
		}
	}
	
	
	public static void main(String[] args) {
		//helperDNASequence_test();
		//readerSequence_test();
		readerDNASequence_test();
	}
}
