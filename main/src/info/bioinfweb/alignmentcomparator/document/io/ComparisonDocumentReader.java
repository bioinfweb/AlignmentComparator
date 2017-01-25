/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperalignedModelDecorator;
import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.io.FormatVersion;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.push.EventForwarder;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.SequenceIDManager;
import info.bioinfweb.libralign.model.io.AlignmentModelEventReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.namespace.QName;



public class ComparisonDocumentReader implements IOConstants {
	private NeXMLEventReader reader;
	private Document document;
	private EventForwarder forwarder;
	private AlignmentModelEventReader alignmentReader;
	
	
	private String readSimpleLiteralContent(QName predicate) throws IOException {
		if (reader.hasNextEvent()) {
			JPhyloIOEvent event = reader.peek();
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
				LiteralMetadataContentEvent contentEvent = event.asLiteralMetadataContentEvent();
				if (contentEvent.isContinuedInNextEvent()) {  //TODO Could this be a problem, if single events are very short on some platforms in some situations. => Possibly use tool method that concatenates part events until a maximum length.
					throw new IOException("The content of the literal metadata element \"" + predicate + 
							"\" seems to be invalid, since it has an unexpected length.");
				}
				else {
					return contentEvent.getStringValue();  //TODO Does the object value need to be used here? 
				}
			}
			else {
				throw new IOException("The literal metadata element \"" + predicate + "\" contains no content.");
			}
		}
		else {
			throw new IOException("Unexpected end of file inside a literal meta definition.");
		}
	}
	
	
	private void readDocumentMetadata() throws IOException {
		QName predicate = reader.next().asLiteralMetadataEvent().getPredicate().getURI();
		
		if (PREDICATE_FORMAT_VERSION.equals(predicate)) {
			String value = readSimpleLiteralContent(predicate);
			try {
				//TODO Throw exceptions here instead of warnings?
				if (FormatVersion.parseFormatVersion(value).geraterThan(NEXML_OUTPUT_VERSION)) {
					//TODO Log warning that newer format might not be supported and AC should be updated. (If not exception is thrown here, it is important to make sure that this waring is displayed, even if an exception occurs later, since this might be due to the unsupported new format version.
				}
			}
			catch (IllegalArgumentException e) {
				//TODO Log warning that invalid format was found.
			  // Future format versions may have a different format.
			}
		}
		else if (PREDICATE_TOKEN_TYPE.equals(predicate)) {
			String value = readSimpleLiteralContent(predicate);
			try {
				CharacterStateSetType tokenType = CharacterStateSetType.valueOf(value); 
				document.setTokenType(tokenType);
				createAlignmentReaderClasses(tokenType);
			}
			catch (NullPointerException | IllegalArgumentException e) {
				throw new IOException("The specified " + Main.APPLICATION_NAME + " token type declaration \"" 
						+ value + "\" is invalid.", e);
			}
		}
		
		JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end event and possibly contents.
	}
	
	
	private void addUnlignedIndex(List<Integer> list, String token) throws IOException {
		if (SUPER_GAP_ENTITY.equals(token)) {
			list.add(SuperalignedModelDecorator.SUPER_GAP_INDEX);
		}
		else {
			try {
				list.add(Integer.parseInt(token));
			}
			catch (NumberFormatException e) {
				throw new IOException("The token \"" + token + "\" found in the unaligned indices list is not a valid column index.", e);
			}
		}
	}
	
	
	private List<Integer> readAlignmentMetadata() throws IOException {
		List<Integer> result = null;
		if (PREDICATE_UNALIGNED_INDICES.equals(reader.next().asLiteralMetadataEvent().getPredicate().getURI())) {
			result = new ArrayList<Integer>();
			String remainingCharacters = "";
			JPhyloIOEvent event = reader.next();
			while (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
				Scanner scanner = new Scanner(remainingCharacters + event.asLiteralMetadataContentEvent().getStringValue());
				try {
					scanner.useDelimiter("\\s+");
					while (scanner.hasNext()) {
						//TODO Make sure not to process incomplete last token.
						String token = scanner.next();
						if (scanner.hasNext()) {
							addUnlignedIndex(result, token);
						}
						else {
							remainingCharacters = token;
						}
					}
				}
				finally {
					scanner.close();
				}
				
				event = reader.next();
			}  //TODO Should additionally be checked, whether the last event was really the according end event?
			
			if (remainingCharacters.length() > 0) {
				addUnlignedIndex(result, remainingCharacters);
			}
		}
		else {
			JPhyloIOReadingUtils.reachElementEnd(reader);  // Consume end event and possibly contents.
		}
		return result;
	}
	
	
	private void readAlignment() throws Exception {
		if (document.getTokenType() == null) {
			throw new IOException("An alignment was found in the document although not " + Main.APPLICATION_NAME 
					+ " token type was specified in the document metadata.");
		}
		else {
			// Read compared alignment:
			LinkedLabeledIDEvent startEvent = reader.next().asLinkedLabeledIDEvent();
			alignmentReader.processEvent(reader, startEvent);  // Consume alignment start event.
			List<Integer> unalignedIndices = null;
			
			JPhyloIOEvent event = reader.peek();   
			while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					if (event.getType().getContentType().equals(EventContentType.LITERAL_META) && 
							PREDICATE_UNALIGNED_INDICES.equals(event.asLiteralMetadataEvent().getPredicate().getURI())) {
						
						if (unalignedIndices == null) {
							unalignedIndices = readAlignmentMetadata();  // Events consumed in here are not relevant for LibrAlign classes.
						}
						else {
							throw new IOException("An alignment with more than one unaligned indices list was found.");
						}
					}
					else {
						forwarder.readCurrentNode(reader);  // Forward all other events to LibrAlign classes.
					}
				}
				else {
					alignmentReader.processEvent(reader, reader.next());  // Consume and forward SOLE events.
				}
				event = reader.peek();
			}
			alignmentReader.processEvent(reader, reader.next());  // Consume alignment end event.
			
			// Add compared alignment to document:
			if (unalignedIndices == null) {
				throw new IOException("An alignment without super alignment indices was found.");
			}
			else if (alignmentReader.getCompletedModels().isEmpty()) {
				throw new IOException("No alignment could be read from a characters in the document.");
			}
			else if (!startEvent.hasLabel()) {
				throw new IOException("One alignment in the document did not contain a label. Unique labels are necessary.");
			}
			else if (document.getAlignments().containsKey(startEvent.getLabel())) {
				throw new IOException("The alignment label \"" + startEvent.getLabel() 
						+ "\" is used more than one time. Although labels do not need to be unique in general NeXML, " 
						+ Main.APPLICATION_NAME + " requires alignment labels to be unique in its own format.");
			}
			else {
				@SuppressWarnings("unchecked")  // Factory ensures correct token type.
				ComparedAlignment alignment = 
						new ComparedAlignment((AlignmentModel<Character>)alignmentReader.getCompletedModels().remove(0));  // Only one model can be read from one ALIGNMENT event node.
				alignment.createSuperaligned(unalignedIndices);
				document.getAlignments().put(startEvent.getLabel(), alignment);
			}
		}
	}
	
	
	private void readDocument() throws Exception {
		JPhyloIOEvent event = reader.peek();   
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				switch (event.getType().getContentType()) {
					case LITERAL_META:
						readDocumentMetadata();
						break;
					//TODO Read comments, when they are written (maybe from according resource meta).
					case ALIGNMENT:
						readAlignment();
						break;
					default:  // Possible additional element, which is not read
						reader.next();  // Consume start event
						JPhyloIOReadingUtils.reachElementEnd(reader);
						break;
				}
			}
			else {
				reader.next();  // Consume SOLE events.
			}
			event = reader.peek();
		}
		
		// Document end event could be consumed here, but does not have to.
	}
	
	
	private void createAlignmentReaderClasses(CharacterStateSetType tokenType) {
		forwarder = new EventForwarder();
		ImportedAlignmentModelFactory factory = new ImportedAlignmentModelFactory(new SequenceIDManager());
		factory.setTokenType(tokenType);
		alignmentReader = new AlignmentModelEventReader(factory);
		forwarder.getListeners().add(alignmentReader);
	}
	
	
	public void read(InputStream stream, Document document) throws Exception {
		reader = new NeXMLEventReader(stream, new ReadWriteParameterMap());  //TODO Specify parameters?
		this.document = document;
		
		reader.next();  // Consume document start.
		document.clear();
		document.setTokenType(null);  // Mark that no token type was read until now.
		readDocument();
		
		forwarder = null;
		alignmentReader = null;
	}
	
	
	public void read(File file, Document document) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		try {
			read(stream, document);
		}
		finally {
			stream.close();
		}
	}
}
