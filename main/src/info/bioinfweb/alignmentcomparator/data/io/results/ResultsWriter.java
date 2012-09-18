/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
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


import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.alignmentcomparator.data.Alignments;
import info.bioinfweb.alignmentcomparator.data.SuperAlignmentSequenceView;
import info.webinsel.util.appversion.AppVersionXMLReadWrite;
import info.webinsel.util.io.XMLUtils;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



public class ResultsWriter implements ResultsXMLConstants {
	public static final String STREAM_ENCODING = "UTF8";
	public static String XML_VERSION = "1.0";
	public static String XML_ENCODING = "UTF-8";
	
	
	private XMLStreamWriter writer = null;

	
	private void writeAlignment(Alignments alignments, int alignmentIndex) throws XMLStreamException {
		writer.writeStartElement(TAG_ALIGNMENT.getLocalPart());
		for (int sequenceIndex = 0; sequenceIndex < alignments.getSequenceCount(); sequenceIndex++) {  
			writer.writeStartElement(TAG_SEQUENCE.getLocalPart());
			writer.writeCharacters(alignments.getUnalignedSequence(alignmentIndex, sequenceIndex).getSequenceAsString());
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	
	public static String encodeGapPattern(int[] indices) {
		StringBuffer pattern = new StringBuffer(indices.length);
		for (int pos = 0; pos < indices.length; pos++) {  
			if (indices[pos] == SuperAlignmentSequenceView.GAP_INDEX) {
				pattern.append(TOKEN_GAP);
			}
			else {
				pattern.append(TOKEN_CHARACTER);
			}
    }
		return pattern.toString();
	}
	
	
	private void writeGapPattern(Alignments alignments, int alignmentIndex) throws XMLStreamException {
		writer.writeStartElement(TAG_GAP_PATTERN.getLocalPart());
		writer.writeCharacters(encodeGapPattern(alignments.getUnalignedIndices(alignmentIndex)));
		writer.writeEndElement();
	}
	
	
	private void writeAlignments(Alignments alignments) throws XMLStreamException {
		writer.writeStartElement(TAG_ALTERNATIVES.getLocalPart());
		for (int alignmentIndex = 0; alignmentIndex < 2; alignmentIndex++) {  //TODO Optionally parameterize loop, if future versions allow the comparison of more than two MSAs  
			writer.writeStartElement(TAG_ALTERNATIVE.getLocalPart());
			writeAlignment(alignments, alignmentIndex);
			writeGapPattern(alignments, alignmentIndex);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	

	private void writeNames(Alignments alignments) throws XMLStreamException {
		writer.writeStartElement(TAG_NAMES.getLocalPart());
		for (int i = 0; i < alignments.getSequenceCount(); i++) {  
			writer.writeStartElement(TAG_NAME.getLocalPart());
			writer.writeCharacters(alignments.getName(i));
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	

	public void write(OutputStream stream, Alignments alignments) 
	    throws XMLStreamException, IOException {
		
		try {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
					stream, STREAM_ENCODING);
			try {
				writer.writeStartDocument(XML_ENCODING, XML_VERSION);
				writer.setDefaultNamespace(NAMESPACE_URI);
				writer.writeStartElement(TAG_ROOT.getLocalPart());
				XMLUtils.writeNamespaceXSDAttr(writer, NAMESPACE_URI, NAMESPACE_URI + "/" +	VERSION + ".xsd");

				AppVersionXMLReadWrite.write(writer, Main.getInstance().getVersion());
				writeNames(alignments);
				writeAlignments(alignments);
				
				writer.writeEndElement();
				writer.writeEndDocument();
			}
			finally {
				writer.close();
			}
		}
		finally {
			stream.close();
		}
	}
}
