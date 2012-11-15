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
package info.bioinfweb.alignmentcomparator.document.io;


import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.DNASequenceCreator;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;



public class FastaReaderTools {
	public static Map<String, DNASequence> readAlignment(File file) throws IOException {
		return readAlignment(new FileInputStream(file));
	}
	
	
	public static Map<String, DNASequence> readAlignment(InputStream stream) throws IOException {
  	FastaReader<DNASequence, NucleotideCompound> fastaReader = 
  	    new FastaReader<DNASequence, NucleotideCompound>(
    	  		new BufferedInputStream(stream),
    	  		new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet())); 
		return fastaReader.process();
	}
}
