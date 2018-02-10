/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document.superalignment.profile;


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.alignmentcomparator.document.superalignment.ExternalProgramAligner;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.alignmentcomparator.gui.dialogs.ConsoleOutputDialog;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.fasta.FASTAEventReader;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.IOTools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;



public class MuscleProfileAligner extends ExternalProgramAligner implements SuperAlignmentAlgorithm {
	private static final String ALIGNMENT_INDEX_SEPARATOR = "_";

	
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
	
	
	private String alignmentIndexPrefix(int index) {
		return index + ALIGNMENT_INDEX_SEPARATOR;
	}
	
	
	private File writeInputFile(Document document, int alignmentIndex) throws Exception {
		File result = createTempFile("Input" + alignmentIndexPrefix(alignmentIndex), "fasta");
		IOTools.writeSingleAlignment(new IndexNamePrefixDecorator(document.getAlignments().getValue(alignmentIndex).getOriginal(), 
				alignmentIndexPrefix(alignmentIndex)), null, result, JPhyloIOFormatIDs.FASTA_FORMAT_ID);
		return result;
	}
	

	private void addSuperGaps(Document document, AlignmentModel<Character> combinedModel, int alignmentIndex) {
		String sequencePrefix = alignmentIndexPrefix(alignmentIndex);
		int superalignedLength = combinedModel.getMaxSequenceLength();
		AlignmentModel<Character> originalAlignment = document.getAlignments().getValue(alignmentIndex).getOriginal();
		ArrayList<Integer> indexList = new ArrayList<Integer>(superalignedLength);
		int unalignedPos = 0;
		for (int superIndex = 0; superIndex < superalignedLength; superIndex++) {
			Iterator<String> iterator = combinedModel.sequenceIDIterator();
			boolean gap = false;
			while (iterator.hasNext()) {
				String superAlignedSequenceID = iterator.next();
				String superAlignedSequenceName = combinedModel.sequenceNameByID(superAlignedSequenceID);
				if (superAlignedSequenceName.startsWith(sequencePrefix)) {
					Character newBase = combinedModel.getTokenAt(superAlignedSequenceID, superIndex); 
					
					Character oldBase = null;
					String originalSequenceID = 
							originalAlignment.sequenceIDsByName(superAlignedSequenceName.substring(sequencePrefix.length())).iterator().next();  //TODO Handle case of multiple results, e.g., by throwing an exception.
					
					if (originalAlignment.getSequenceLength(originalSequenceID) > unalignedPos) {  // Otherwise terminal super gaps need to inserted from now on.
						oldBase = originalAlignment.getTokenAt(originalSequenceID, unalignedPos);
					}
					
					if (!newBase.equals(oldBase)) {  // Just checking if a column consists only of gaps does not work, if the input alignments already contains columns only consisting of gaps.
						gap = true;
						break;
					}
				}
			}
			
			if (gap) {
			  indexList.add(SuperalignedModelDecorator.SUPER_GAP_INDEX);
			}
			else {
			  indexList.add(unalignedPos);
				unalignedPos++;
			}
		}
		document.getAlignments().getValue(alignmentIndex).createSuperaligned(indexList);
	}
	
	
	private Thread runMuscle(Document paramDocument, InputStream stream) throws IOException {
		final BufferedInputStream fastaStream = new BufferedInputStream(stream);
		final Document document = paramDocument;
		
		Thread result = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							try {
								AlignmentDataReader mainReader = new AlignmentDataReader(new FASTAEventReader(fastaStream, new ReadWriteParameterMap()), 
										new BioPolymerCharAlignmentModelFactory());
								mainReader.readAll();

								AlignmentModel<Character> combinedAlignment = 
										(AlignmentModel<Character>)mainReader.getAlignmentModelReader().getCompletedModels().get(0);
								for (int i = 0; i < 2; i++) {
									addSuperGaps(document, combinedAlignment, i);
								}
							}
							finally {
								fastaStream.close();
							}
						}
						catch (Exception e) {
							JOptionPane.showMessageDialog(ConsoleOutputDialog.getInstance(),  
									"An error occurred while trying to read the muscle output." + SystemUtils.LINE_SEPARATOR + 
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
		if (document.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
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
}
