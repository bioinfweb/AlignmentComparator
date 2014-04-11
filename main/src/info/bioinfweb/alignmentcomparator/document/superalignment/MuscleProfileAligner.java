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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.FastaWriter;
import org.biojava3.core.sequence.io.template.FastaHeaderFormatInterface;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperAlignmentSequenceView;
import info.bioinfweb.alignmentcomparator.gui.dialogs.ConsoleOutputDialog;
import info.bioinfweb.commons.bio.biojava3.core.sequence.io.FastaReaderTools;
import info.bioinfweb.commons.bio.biojava3.core.sequence.views.ReplaceAbstractSequenceView;
import info.bioinfweb.commons.bio.biojava3.core.sequence.views.ReplaceNucleotideSequenceView;



public class MuscleProfileAligner extends ExternalProgramAligner implements SuperAlignmentAlgorithm {
	public static final int HEADER_PREFIX_LENGTH = 2;
	
	
	public static final FastaHeaderFormatInterface<Sequence<NucleotideCompound>, NucleotideCompound> FIRST_HEADER_FORMAT = 
			new FastaHeaderFormatInterface<Sequence<NucleotideCompound>, NucleotideCompound>() {
				public String getHeader(Sequence<NucleotideCompound> sequence) {
					return "0 " + ((ReplaceAbstractSequenceView)sequence).getOriginalHeader();  
				}
			};
	
	
	public static final FastaHeaderFormatInterface<Sequence<NucleotideCompound>, NucleotideCompound> SECOND_HEADER_FORMAT = 
			new FastaHeaderFormatInterface<Sequence<NucleotideCompound>, NucleotideCompound>() {
				public String getHeader(Sequence<NucleotideCompound> sequence) {
					return "1 " + ((ReplaceAbstractSequenceView)sequence).getOriginalHeader();  
				}
			};
	
			
	@Override
	public String getApplicationName() {
		String architecture = "32";
    if (is64BitJRE()) {  //TODO So kann nur festgestellt werden, ob es sich im ein 64 Bit JRE handelt. Wird ein 32 Bit JRE auf einem 64 Bit System ausgeführt, wird trotzdem 32 zurückgegeben.
    	architecture = "64";
    }
    
		if (SystemUtils.IS_OS_WINDOWS) {
			return "muscle3.8.31_i86win32.exe";
		}
		else if (SystemUtils.IS_OS_LINUX) {
			return "muscle3.8.31_i86linux" + architecture;
		}
		else if (SystemUtils.IS_OS_MAC) {
			return "muscle3.8.31_i86darwin" + architecture;
		}
		else {
			return null;
		}
	}
	
	
	private List<Sequence<NucleotideCompound>> createReplacedList(Document document, int alignmentIndex) {
		ArrayList<Sequence<NucleotideCompound>> result = new ArrayList<Sequence<NucleotideCompound>>(document.getSequenceCount());
		for (int i = 0; i < document.getSequenceCount(); i++) {
			result.add(new ReplaceAbstractSequenceView(document.getUnalignedSequence(alignmentIndex, i), ReplaceNucleotideSequenceView.AMBIGUITY_RNA_TO_N_DNA_MAP));
		}
		return result;
	}
	
	
	private File writeInputFile(Document document, int alignmentIndex) throws Exception {
		String prefix = "First_";
		FastaHeaderFormatInterface<Sequence<NucleotideCompound>, NucleotideCompound> headerFormat = FIRST_HEADER_FORMAT;
		if (alignmentIndex == 1) {
			prefix = "Second_";
			headerFormat = SECOND_HEADER_FORMAT;
		}
		File result = createTempFile(prefix, "fasta");
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(result)); 
		FastaWriter<Sequence<NucleotideCompound>, NucleotideCompound> fastaWriter = 
				new FastaWriter<Sequence<NucleotideCompound>, NucleotideCompound>(
				    stream, createReplacedList(document, alignmentIndex),
		    		headerFormat);
		fastaWriter.process();
		stream.close();
		return result;
	}
	

	private List<DNASequence> extractSuperAlignment(Map<String, DNASequence> map, String prefix) {
		Iterator<String> iterator = map.keySet().iterator();
		List<DNASequence> result = new ArrayList<DNASequence>(map.size() / 2);
		while (iterator.hasNext()) {
			DNASequence sequence = map.get(iterator.next());
			if (sequence.getOriginalHeader().startsWith(prefix)) {
				result.add(sequence);
			}
		}
		return result;
	}
	
	
	private void addSuperGaps(Document document, List<DNASequence> superAlignment, int alignmentIndex) {
		int superalignedLength = superAlignment.get(0).getLength();
		ArrayList<Integer> indexList = new ArrayList<Integer>(superalignedLength);
		int unalignedPos = 1;
		for (int superIndex = 1; superIndex <= superalignedLength; superIndex++) {
			Iterator<DNASequence> iterator = superAlignment.iterator();
			boolean gap = false;
			while (iterator.hasNext()) {
				DNASequence superalignedSequence = iterator.next();
				String newBase = superalignedSequence.getCompoundAt(superIndex).getBase();
				String oldBase = document.getUnalignedSequence(alignmentIndex, document.getIndexByName(
						superalignedSequence.getOriginalHeader().substring(2))).getCompoundAt(unalignedPos).getBase();
				if (!newBase.equals(oldBase)) {  // Just checking if a column consists only of gaps does not work, if the input alignments already contain columns only consisting of gaps.
					gap = true;
					break;
				}
			}
			
			if (gap) {
			  indexList.add(SuperAlignmentSequenceView.GAP_INDEX);
			}
			else {
			  indexList.add(unalignedPos);
				unalignedPos++;
			}
		}
		document.setUnalignedIndexList(alignmentIndex, indexList);
	}
	
	
	private Thread runMuscle(Document paramDocument, InputStream stream) throws IOException {
		final BufferedInputStream fastaStream = new BufferedInputStream(stream);
		final Document document = paramDocument;
		
		Thread result = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							try {
								Map<String, DNASequence> resultMap = FastaReaderTools.readDNAAlignment(fastaStream);
								for (int i = 0; i < 2; i++) {
									addSuperGaps(document, extractSuperAlignment(resultMap, i + " "), i);
								}
							}
							finally {
								fastaStream.close();
							}
						}
						catch (IOException e) {
							JOptionPane.showMessageDialog(ConsoleOutputDialog.getInstance(),  
									"An IO error occurred while trying to read the muscle output." + SystemUtils.LINE_SEPARATOR + 
									e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				});
		result.start();
		return result;
	}
	
	
	@Override
	public void performAlignment(Document document) throws Exception {
		String applicationName = getApplicationName();
		if (applicationName != null) {
			File first = writeInputFile(document, 0);
			File second = writeInputFile(document, 1);
			try {
				ProcessBuilder pb = new ProcessBuilder(cmdFolder() + getApplicationName(), 
						"-in1",	first.getAbsolutePath(),
						"-in2", second.getAbsolutePath(), 
						"-profile");
				pb.directory(new File(cmdFolder()));
				Process process = pb.start();
				
				ConsoleOutputDialog dialog = ConsoleOutputDialog.getInstance();
				dialog.showEmpty();
	
				Thread thread = runMuscle(document, process.getInputStream());
				dialog.addStream(process.getErrorStream());
				dialog.addLine("");
				dialog.addLine("Exit code of MUSCLE: " + process.waitFor());
				thread.join();  // Wait for the processing of the MUSCLE output after process is already finished.
				dialog.setAllowClose(true);
			}
			finally {
				first.delete();
				second.delete();
			}
		}
		else {
			JOptionPane.showMessageDialog(Main.getInstance().getMainFrame(), 
					"This operation is not supported on your operating system (" + System.getProperty("os.name") + ").", 
          "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
