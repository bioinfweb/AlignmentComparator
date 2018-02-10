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
		receiver.add(new CommentEvent(" This document contains information specific for " + Main.APPLICATION_NAME + 
				" and should not be edited by hand or with other software. If unsupported data is added, it may get lost when "
				+ "the file is processed by " + Main.APPLICATION_NAME + " the next time. "));
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, ReadWriteConstants.DEFAULT_META_ID_PREFIX + "1", null, 
				PREDICATE_FORMAT_VERSION, W3CXSConstants.DATA_TYPE_TOKEN, NEXML_OUTPUT_VERSION);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, ReadWriteConstants.DEFAULT_META_ID_PREFIX + "2", null, 
				PREDICATE_APPLICATION_VERSION, W3CXSConstants.DATA_TYPE_TOKEN, Main.getInstance().getVersion());
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, ReadWriteConstants.DEFAULT_META_ID_PREFIX + "3", null, 
				PREDICATE_TOKEN_TYPE, W3CXSConstants.DATA_TYPE_TOKEN, document.getTokenType().name());
		//TODO Special data types restricting the current ones could be defined.
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
