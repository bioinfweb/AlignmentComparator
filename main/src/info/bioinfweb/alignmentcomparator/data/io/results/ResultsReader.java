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
package info.bioinfweb.alignmentcomparator.data.io.results;


import info.bioinfweb.alignmentcomparator.data.Alignments;
import info.bioinfweb.alignmentcomparator.data.SuperAlignmentSequenceView;
import info.webinsel.util.appversion.AppVersionXMLConstants;
import info.webinsel.util.appversion.AppVersionXMLReadWrite;
import info.webinsel.util.io.XMLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;



public class ResultsReader implements ResultsXMLConstants {
  private XMLEventReader reader;
  
  private List<Sequence<NucleotideCompound>[]> unalignedSequences = new LinkedList<Sequence<NucleotideCompound>[]>();
  private List<int[]> unalignedIndicesList = new LinkedList<int[]>();
  
  
  private Sequence<NucleotideCompound>[] readAlignment() throws XMLStreamException {
  	List<Sequence<NucleotideCompound>> result = new LinkedList<Sequence<NucleotideCompound>>();
    XMLEvent event = reader.nextEvent();
    while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      	StartElement element = event.asStartElement();
        if (element.getName().equals(TAG_SEQUENCE)) {
        	result.add(new DNASequence(XMLUtils.readCharactersAsString(reader)));
        	XMLUtils.reachElementEnd(reader);
        }
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    return result.toArray(new Sequence[result.size()]);
  }
  
  
  public static int[] decodeGapPattern(String gapPattern) {
  	int unalignedPos = 0;
  	int[] result = new int[gapPattern.length()];
  	for (int alignedPos = 0; alignedPos < gapPattern.length(); alignedPos++) {
			if (gapPattern.charAt(alignedPos) == TOKEN_GAP) {
				result[alignedPos] = SuperAlignmentSequenceView.GAP_INDEX;
			}
			else {
				result[alignedPos] = unalignedPos;
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

  
  private void readDocument(StartElement rootElement, Alignments alignments) throws XMLStreamException {
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
        else {  // evtl. zusätzlich vorhandenes Element, dass nicht gelesen wird
          XMLUtils.reachElementEnd(reader);  
        }
      }
      event = reader.nextEvent();
    }
    
    alignments.setAlignedData(names, 
    		unalignedSequences.toArray(new Sequence[unalignedSequences.size()][]), 
    		unalignedIndicesList.toArray(new int[unalignedIndicesList.size()][]));
  }

  
  public void clear() {
  	unalignedSequences.clear();
  	unalignedIndicesList.clear();
  }
  
  
	public void read(InputStream stream, Alignments alignments) throws XMLStreamException, IOException {
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
