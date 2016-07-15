package info.bioinfweb.alignmentcomparator.document.io;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.push.EventForwarder;
import info.bioinfweb.libralign.model.io.AlignmentModelEventReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;



public class ComparisonDocumentReader {
	public void read(File file, Document alignments) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		try {
			read(stream, alignments);
		}
		finally {
			stream.close();
		}
	}
	
	
	public void read(InputStream stream, Document alignments) throws Exception {
		NeXMLEventReader reader = new NeXMLEventReader(stream, new ReadWriteParameterMap());  //TODO Specify parameters?
		EventForwarder forwarder = new EventForwarder();
		AlignmentModelEventReader alignmentReader = new AlignmentModelEventReader();  //TODO Specify factory
		forwarder.getListeners().add(alignmentReader);
		//TODO Add listener for document metadata and superalignment metadata
		//TODO Add data model listener for comments
		
		forwarder.readAll(reader);
		
		//TODO Edit document according to collected information. 
	}
}
