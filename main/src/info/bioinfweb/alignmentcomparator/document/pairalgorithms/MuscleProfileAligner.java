package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.biojava3.core.sequence.io.FastaWriterHelper;

import info.bioinfweb.alignmentcomparator.document.Document;



public class MuscleProfileAligner extends ExternalProgramAligner implements SuperAlignmentAlgorithm {
	@Override
	public void performAlignment(Document document) throws Exception {
		File first = createTempFile("First_", "fasta");
		FastaWriterHelper.writeNucleotideSequence(first, document.getUnalignedSequences(0));
		File second = createTempFile("Second_", "fasta");
		FastaWriterHelper.writeNucleotideSequence(second, document.getUnalignedSequences(1));
		
		Process p = Runtime.getRuntime().exec(cmdFolder() +   "cmd /c dir");
//    BufferedReader bri = new BufferedReader
//      (new InputStreamReader(p.getInputStream()));
//    BufferedReader bre = new BufferedReader
//      (new InputStreamReader(p.getErrorStream()));
//    while ((line = bri.readLine()) != null) {
//      System.out.println(line);
//    }
//    bri.close();
//    while ((line = bre.readLine()) != null) {
//      System.out.println(line);
//    }
//    bre.close();
    p.waitFor();
//    System.out.println("Done.");
	}

}
