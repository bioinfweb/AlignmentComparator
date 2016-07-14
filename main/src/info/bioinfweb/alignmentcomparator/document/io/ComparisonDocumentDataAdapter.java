package info.bioinfweb.alignmentcomparator.document.io;


import java.io.IOException;
import java.util.Iterator;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.EmptyDocumentDataAdapter;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;



public class ComparisonDocumentDataAdapter extends EmptyDocumentDataAdapter implements IOConstants {
	private Document document;
	
	
	public ComparisonDocumentDataAdapter(Document document) {
		super();
		this.document = document;
	}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		receiver.add(new CommentEvent(" This NeXML document contains information specific for alignment comparator and should not "
				+ "be edited by hand or with other software. If unsupported data is added, it may get lost when the file is processed "
				+ "by AlignmentCoparator the next time. "));
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, ReadWriteConstants.DEFAULT_META_ID_PREFIX + "1", null, 
				PREDICATE_FORMAT_VERSION, W3CXSConstants.DATA_TYPE_STRING, NEXML_OUTPUT_VERSION);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, ReadWriteConstants.DEFAULT_META_ID_PREFIX + "2", null, 
				PREDICATE_APPLICATION_VERSION, W3CXSConstants.DATA_TYPE_STRING, Main.getInstance().getVersion());
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
