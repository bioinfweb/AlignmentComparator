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
package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.io.FastaReaderTools;
import info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels.ConsoleOutputDialog;
import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;



public class MuscleProfileAligner extends ExternalProgramAligner implements SuperAlignmentAlgorithm {
	public static final int HEADER_PREFIX_LENGTH = 2;
	
	
	public static final FastaHeaderFormatInterface<DNASequence, NucleotideCompound> FIRST_HEADER_FORMAT = 
			new FastaHeaderFormatInterface<DNASequence, NucleotideCompound>() {
				public String getHeader(DNASequence sequence) {
					return "0 " + sequence.getOriginalHeader();  
				}
			};
	
	
	public static final FastaHeaderFormatInterface<DNASequence, NucleotideCompound> SECOND_HEADER_FORMAT = 
			new FastaHeaderFormatInterface<DNASequence, NucleotideCompound>() {
				public String getHeader(DNASequence sequence) {
					return "1 " + sequence.getOriginalHeader();
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

	
	private File writeInputFile(Document document, int alignmentIndex) throws Exception {
		String prefix = "First_";
		FastaHeaderFormatInterface<DNASequence, NucleotideCompound> headerFormat = FIRST_HEADER_FORMAT;
		if (alignmentIndex == 1) {
			prefix = "Second_";
			headerFormat = SECOND_HEADER_FORMAT;
		}
		File result = createTempFile(prefix, "fasta");
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(result)); 
		FastaWriter<DNASequence, NucleotideCompound> fastaWriter = new FastaWriter<DNASequence, NucleotideCompound>(
		    stream, document.getUnalignedSequences(alignmentIndex),
    		headerFormat);
		fastaWriter.process();
		stream.close();
		return result;
	}
	

	private List<DNASequence> extractSuperAlignment(Map<String, DNASequence> map, String prefix) {
		Iterator<String> iterator = map.keySet().iterator();
		List<DNASequence> result = new ArrayList<>(map.size() / 2);
		while (iterator.hasNext()) {
			DNASequence sequence = map.get(iterator.next());
			if (sequence.getOriginalHeader().startsWith(prefix)) {
				result.add(sequence);
			}
		}
		return result;
	}
	
	
	private void addSuperGaps(Document document, List<DNASequence> superAlignment, int alignmentIndex) {
		// Es reicht nicht nur eine Sequenz mit dem Original zu verleichen, da dann Superlücken innerhalb vorher bestehender Lücken nicht genau bestimmt werden können.
		for (int i = 1; i <= superAlignment.get(0).getLength(); i++) {
			Iterator<DNASequence> iterator = superAlignment.iterator();
			boolean gap = true;
			while (iterator.hasNext()) {
				String base = iterator.next().getCompoundAt(i).getBase(); 
				if (!base.equals("" + AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER)) {
					System.out.println("no gap " + base + " " + i);
					gap = false;
					break;
				}
			}
			
			if (gap) {
				System.out.println("gap");
				document.insertSuperGap(alignmentIndex, i);
			}
		}
	}
	
	
	private Thread runMuscle(Document paramDocument, InputStream stream) throws IOException {
		final BufferedInputStream fastaStream = new BufferedInputStream(stream);
		final Document document = paramDocument;
		
		Thread result = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							try {
								Map<String, DNASequence> resultMap = FastaReaderTools.readAlignment(fastaStream);
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
		File first = writeInputFile(document, 0);
		File second = writeInputFile(document, 1);
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdFolder() + getApplicationName(), 
					"-in1",	first.getAbsolutePath(),
					"-in2",  second.getAbsolutePath(), 
					"-profile");
			pb.directory(new File(cmdFolder()));
			Process process = pb.start();
			
			Thread muscleThread = runMuscle(document, process.getInputStream());
			
			ConsoleOutputDialog dialog = ConsoleOutputDialog.getInstance();
			dialog.showEmpty();
			dialog.addStream(process.getErrorStream());

//			if (muscleThread.isAlive()) {
//				muscleThread.wait();  // In principle not additionally needed, if process.waitFor() is called too. 
//			}
			dialog.addLine("");
			dialog.addLine("Exit code of MUSCLE: " + process.waitFor());
			dialog.setAllowClose(true);
		}
		finally {
			first.delete();
			second.delete();
		}
	}
}
