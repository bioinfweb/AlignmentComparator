package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.FastaWriter;
import org.biojava3.core.sequence.io.FastaWriterHelper;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.alignmentcomparator.document.Document;



public class MuscleProfileAligner implements SuperAlignmentAlgorithm {
	@Override
	public void performAlignment(Document alignments) {
		//FastaWriterHelper.writeSequence(file, sequence);
		// TODO Sollen neue Dateien geschrieben werden oder Dateinamen und MUSCLE weitergegeben werden? (Lieber als FASTA schreiben, da dann zukünftig unterstützte Formate nicht von MUSCLE u.a. abhängen.)
		
	}

}
