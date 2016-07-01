package info.bioinfweb.alignmentcomparator.document.io;


import java.io.IOException;
import java.util.Iterator;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.EmptyDocumentDataAdapter;



public class ComparisonDocumentDataAdapter extends EmptyDocumentDataAdapter {
	private Document document;
	
	
	public ComparisonDocumentDataAdapter(Document document) {
		super();
		this.document = document;
	}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		//TODO Write AC marking and possibly comment data
	}
	// TODO Create OTU lists for alignments?
	
	
	@Override
	public Iterator<MatrixDataAdapter> getMatrixIterator(ReadWriteParameterMap parameters) {
		final Iterator<String> nameIterator = document.getAlignments().asList().iterator();
		
		return new Iterator<MatrixDataAdapter>() {
			private int index = -1;
			
			
			@Override
			public boolean hasNext() {
				return nameIterator.hasNext();
			}

			
			@Override
			public MatrixDataAdapter next() {
				String name = nameIterator.next();
				index++;
				return new ComparedAlignmentDataAdapter("c" + index + "_", name, document.getAlignments().get(name));
			}
		};
	}
}
