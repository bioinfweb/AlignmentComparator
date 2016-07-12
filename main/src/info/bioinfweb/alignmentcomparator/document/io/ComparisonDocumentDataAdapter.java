package info.bioinfweb.alignmentcomparator.document.io;


import java.io.IOException;
import java.util.Iterator;

import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.EmptyDocumentDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;



public class ComparisonDocumentDataAdapter extends EmptyDocumentDataAdapter implements IOConstants {
	private Document document;
	
	
	public ComparisonDocumentDataAdapter(Document document) {
		super();
		this.document = document;
	}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		receiver.add(new LiteralMetadataEvent(DOCUMENT_TAG_ID, null, new URIOrStringIdentifier(null, PREDICATE_IS_COMPARISON), 
				new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_BOOLEAN), LiteralContentSequenceType.SIMPLE));
		receiver.add(new LiteralMetadataContentEvent(true, null));
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.META_LITERAL));
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
