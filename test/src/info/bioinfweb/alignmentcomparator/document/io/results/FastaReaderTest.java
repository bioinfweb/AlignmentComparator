package info.bioinfweb.alignmentcomparator.document.io.results;


import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;

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
	            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was würde DNASequenceCreator anders machen? 
	    
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
	            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was würde DNASequenceCreator anders machen? 
	    
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
