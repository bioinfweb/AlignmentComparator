package info.bioinfweb.alignmentcomparator.gui.actions;


import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.DNASequenceCreator;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.template.FastaHeaderParserInterface;
import org.biojava3.core.sequence.template.Sequence;



public class CompareAlignmentsAction extends AlignmentComparatorAction {
  private JFileChooser fileChooser = null;
  
  
  public CompareAlignmentsAction(MainFrame mainFrame) {
		super(mainFrame);
	}


	private JFileChooser getFileChooser() {
  	if (fileChooser == null) {
  		fileChooser = new JFileChooser();
  		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTA alignment", "fasta", "fas"));
  	}
  	return fileChooser;
  }

  
	@Override
	public void actionPerformed(ActionEvent e) {
		getFileChooser().setDialogTitle("First alignment");
		if (getFileChooser().showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File firstAlignment = getFileChooser().getSelectedFile();
			getFileChooser().setDialogTitle("Second alignment");		
			if (getFileChooser().showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File secondAlignment = getFileChooser().getSelectedFile();
				//TODO load Alignments
				//TODO align Alignments
				//TODO store Alignments in class Alignments
			}
		}		
	}
	
	
	private void processAlignments(File firstFile, File secondFile) {
		try {
			Map<String, Sequence<NucleotideCompound>> firstAlignment = readAlignment(firstFile);
			Map<String, Sequence<NucleotideCompound>> secondAlignment = readAlignment(secondFile);
			
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(getMainFrame(), "Error", "An IO error occurred while loading the files.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	private Map<String, Sequence<NucleotideCompound>> readAlignment(File file) throws IOException {
    FastaReader<Sequence<NucleotideCompound>, NucleotideCompound> fastaReader = 
    	  new FastaReader<Sequence<NucleotideCompound>, NucleotideCompound>(
    	  		new BufferedInputStream(new FileInputStream(file)),
    	  		new FastaHeaderParserInterface<Sequence<NucleotideCompound>, NucleotideCompound>() {
    	  			public void parseHeader(String header, Sequence<NucleotideCompound> sequence) {}
						},
            new DNASequenceCreator(new AlignmentAmbiguityNucleotideCompoundSet()));  //TODO Was würde DNASequenceCreator anders machen? 
    
		return fastaReader.process();
	}
}
