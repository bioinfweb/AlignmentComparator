/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.document.io.results;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.SuperAlignmentSequenceView;
import info.bioinfweb.alignmentcomparator.document.comments.CommentList;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.appversion.AppVersionXMLConstants;
import info.bioinfweb.commons.appversion.AppVersionXMLReadWrite;
import info.bioinfweb.commons.io.XMLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.biojava3.core.sequence.DNASequence;



public class ResultsReader implements ResultsXMLConstants {
  private XMLEventReader reader;
  
  private List<List<DNASequence>> unalignedSequences = new LinkedList<List<DNASequence>>();
  private List<ArrayList<Integer>> unalignedIndicesList = new LinkedList<ArrayList<Integer>>();
  
  
  private List<DNASequence> readAlignment() throws XMLStreamException {
  	List<DNASequence> result = new LinkedList<DNASequence>();
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_SEQUENCE)) {
        	result.add(new DNASequence(XMLUtils.readCharactersAsString(reader), 
        			AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet()));
        	XMLUtils.reachElementEnd(reader);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    return result;
  }
  
  
  public static ArrayList<Integer> decodeGapPattern(String gapPattern) {
  	int unalignedPos = 1;  // BioJava indes starts with 1
  	ArrayList<Integer> result = new ArrayList<Integer>((int)(gapPattern.length() * Document.ARRAY_LIST_SIZE_FACTOR));
  	for (int alignedPos = 0; alignedPos < gapPattern.length(); alignedPos++) {
			if (gapPattern.charAt(alignedPos) == TOKEN_GAP) {
				result.add(SuperAlignmentSequenceView.GAP_INDEX);
			}
			else {
				result.add(unalignedPos);
				unalignedPos++;
			}
		}
  	return result;
  }

  
  private void readAlternative() throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_ALIGNMENT)) {
        	unalignedSequences.add(readAlignment());
        }
        else if (element.getName().equals(TAG_GAP_PATTERN)) {
        	unalignedIndicesList.add(decodeGapPattern(XMLUtils.readCharactersAsString(reader)));
        	XMLUtils.reachElementEnd(reader);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
  }

  
  private void readAlternatives(int count) throws XMLStreamException {
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_ALTERNATIVE)) {
        	readAlternative();
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
  }

  
  private String[] readNames() throws XMLStreamException {
  	List<String> names = new LinkedList<String>();
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_NAME)) {
        	names.add(XMLUtils.readCharactersAsString(reader));
          XMLUtils.reachElementEnd(reader);  
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    return names.toArray(new String[names.size()]);
  }

  
  private int readPosAttribute(StartElement element, QName attribute) throws XMLStreamException {
  	int result= XMLUtils.readIntAttr(element, ATTR_COMMENT_FIRST_POS, Integer.MIN_VALUE);
  	if (result < 0) {
  		throw new XMLStreamException("Elements of the type " + TAG_COMMENT + " must contain the attribute " + 
  	      ATTR_COMMENT_FIRST_POS + " which must be greater or equal 0.");
  	}
  	return result;
  }
  
  
  private void readComments(CommentList list) throws XMLStreamException {
  	list.clear();
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_COMMENT)) {
        	list.add(readPosAttribute(element, ATTR_COMMENT_FIRST_POS), readPosAttribute(element, ATTR_COMMENT_LAST_POS), 
        			XMLUtils.readCharactersAsString(reader));
        	XMLUtils.reachElementEnd(reader);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
  }

  
  private void readDocument(StartElement rootElement, Document alignments) throws XMLStreamException {
  	String[] names = null;
  	
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(AppVersionXMLConstants.TAG_APP_VERSION)) {
        	AppVersionXMLReadWrite.read(element);  //TODO Store this somewhere
        }
        else if (element.getName().equals(TAG_NAMES)) {
        	names = readNames();
        }
        else if (element.getName().equals(TAG_ALTERNATIVES)) {
        	if (names == null) {
        		throw new XMLStreamException("The element " + TAG_ALTERNATIVE + " cannot be used before the element " + 
        	      TAG_NAMES + " was specified.");
        	}
        	else {
        		readAlternatives(names.length);
        	}
        }
        else if (element.getName().equals(TAG_COMMENTS)) {
        	readComments(alignments.getComments());
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    
    alignments.setAlignedData(names, 
    		//TODO Fehler nicht hier beheben, sonder Document-Datenstruktur noch mal sinnvoll überarbeiten und auf merkwürdige Array-Konstrukte verzichten.
    		unalignedSequences.toArray(new LinkedList[unalignedSequences.size()]), 
    		unalignedIndicesList.toArray(new ArrayList[unalignedIndicesList.size()]));
  }

  
  public void clear() {
  	unalignedSequences.clear();
  	unalignedIndicesList.clear();
  }
  
  
	public void read(InputStream stream, Document alignments) throws XMLStreamException, IOException {
  	reader = XMLInputFactory.newInstance().createXMLEventReader(stream);
		try {
			XMLEvent event;
			while (reader.hasNext()) {
	      event = reader.nextEvent();
	      switch (event.getEventType()) {
	        case XMLStreamConstants.START_DOCUMENT:
	        	alignments.clear();
	          break;
	        case XMLStreamConstants.START_ELEMENT:
	        	StartElement element = event.asStartElement();
	          if (element.getName().equals(TAG_ROOT)) {
	        	  readDocument(element, alignments);
	          }
	          else {
	            XMLUtils.reachElementEnd(reader);  
	          }
	          break;
	      }
	    }
		}
		finally {
			clear();
	    reader.close();
	    stream.close();
		}
	}
}
