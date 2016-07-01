package info.bioinfweb.alignmentcomparator.document.io;


import java.io.IOException;
import java.util.Iterator;

import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.libralign.model.io.AlignmentModelDataAdapter;



public class ComparedAlignmentDataAdapter extends AlignmentModelDataAdapter<Character> implements IOConstants {
	private static final int MAX_EVENT_LENGTH = 1024;
	
	
	private ComparedAlignment comparedAlignment;

	
	public ComparedAlignmentDataAdapter(String idPrefix, String alignmentName, ComparedAlignment comparedAlignment) {
		super(idPrefix, new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, idPrefix, alignmentName, OTUS_ID),  //TODO Should something be added to the ID prefix? 
				comparedAlignment.getOriginal(), true);
		this.comparedAlignment = comparedAlignment;
	}


	public ComparedAlignment getComparedAlignment() {
		return comparedAlignment;
	}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		receiver.add(new LiteralMetadataEvent(getIDPrefix() + UNALIGNED_INDICES_ID_SUFFIX, null, 
				new URIOrStringIdentifier(null, PREDICATE_UNALIGNED_INDICES), 
				new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING), LiteralContentSequenceType.SIMPLE));
		
		StringBuilder elementString = new StringBuilder();
		Iterator<Integer> iterator = comparedAlignment.getSuperaligned().getUnalignedIndices().iterator();
		while (iterator.hasNext()) {
			if (elementString.length() >= MAX_EVENT_LENGTH) {
				receiver.add(new LiteralMetadataContentEvent(elementString.toString(), iterator.hasNext()));
				elementString.delete(0, elementString.length());
			}
			
			int index = iterator.next();
			if (index == SuperalignedModelDecorator.SUPER_GAP_INDEX) {
				elementString.append(SUPER_GAP_ENTITY);
			}
			else {
				elementString.append(index);
			}
			if (iterator.hasNext()) {
				elementString.append(' ');
			}
		}
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.META_LITERAL));
	}
}
