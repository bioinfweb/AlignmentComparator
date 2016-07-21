/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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
		super(idPrefix, new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, idPrefix, alignmentName, null),  //TODO Should something be added to the ID prefix? 
				comparedAlignment.getOriginal(), false);
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
		if (elementString.length() > 0) {
			receiver.add(new LiteralMetadataContentEvent(elementString.toString(), false));
		}
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.META_LITERAL));
	}
}
