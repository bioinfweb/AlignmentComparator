package info.bioinfweb.alignmentcomparator.document.io;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;



public class ComparisonDocumentMetadataListener implements JPhyloIOEventListener, IOConstants {
	private CharacterStateSetType tokenType = null;
	private List<List<Integer>> unalignedIndicesList = new ArrayList<List<Integer>>();
	
	
	public CharacterStateSetType getTokenType() {
		return tokenType;
	}


	public List<List<Integer>> getUnalignedIndicesList() {
		return unalignedIndicesList;
	}


	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) throws Exception {
		if (event.getType().getContentType().equals(EventContentType.ALIGNMENT) && (tokenType == null)) {  // Alignment definitions start before a token type was specified.
			throw new IOException("This document does not contain an " + Main.APPLICATION_NAME
					+ " token type declaration. Maybe it is not an " + Main.APPLICATION_NAME + " document?");
		}
		else if (event.getType().getContentType().equals(EventContentType.META_LITERAL_CONTENT)) {
			if (source.getParentInformation().isParentSequence(EventContentType.META_LITERAL, EventContentType.DOCUMENT)) {
				if (PREDICATE_TOKEN_TYPE.equals(source.getParentInformation().getDirectParent().asLiteralMetadataEvent().getPredicate().getURI())) {
					try {
						tokenType = CharacterStateSetType.valueOf(event.asLiteralMetadataContentEvent().getObjectValue().toString().trim());  // The object value should be a string.
					}
					catch (NullPointerException | IllegalArgumentException e) {
						throw new IOException("The specified " + Main.APPLICATION_NAME + " token type declaration \"" 
								+ event.asLiteralMetadataContentEvent().getObjectValue() + "\" is invalid.", e);
					}
				}
				
			  //TODO Also check format.
			}
			else if (source.getParentInformation().isParentSequence(EventContentType.META_LITERAL, EventContentType.ALIGNMENT)) {
				//TODO Read superaligmment data
			}
		}
	}
}
