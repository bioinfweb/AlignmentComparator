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


import info.bioinfweb.commons.io.FormatVersion;

import javax.xml.namespace.QName;



public interface IOConstants {
	public static final FormatVersion NEXML_OUTPUT_VERSION = new FormatVersion(1, 0);
	// 1.0 since reintroduction of comments. (0.0 before.)
	
	public static final String UNALIGNED_INDICES_ID_SUFFIX = "indices";

	public static final String SUPER_GAP_ENTITY = "-";
	
	public static final String DEFAULT_NAMESPACE_PREFIX = "ac";
	public static final String ONTOLOGY_NAMESPACE_URI = "http://bioinfweb.info/xmlns/AlignmentComparator/";  //TODO Add additional subfolder?
	
	public static final QName PREDICATE_APPLICATION_VERSION = new QName(ONTOLOGY_NAMESPACE_URI, "applicationVersion", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_FORMAT_VERSION = new QName(ONTOLOGY_NAMESPACE_URI, "formatVersion", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_TOKEN_TYPE = new QName(ONTOLOGY_NAMESPACE_URI, "tokenType", DEFAULT_NAMESPACE_PREFIX);
	
	public static final QName PREDICATE_COMMENT_LIST = new QName(ONTOLOGY_NAMESPACE_URI, "comments", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_COMMENT = new QName(ONTOLOGY_NAMESPACE_URI, "comment", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_COMMENT_TEXT = new QName(ONTOLOGY_NAMESPACE_URI, "text", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_COMMENT_FIRST_POS = new QName(ONTOLOGY_NAMESPACE_URI, "start", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_COMMENT_LAST_POS = new QName(ONTOLOGY_NAMESPACE_URI, "end", DEFAULT_NAMESPACE_PREFIX);
	
	public static final QName PREDICATE_UNALIGNED_INDICES = new QName(ONTOLOGY_NAMESPACE_URI, "unalignedIndices", DEFAULT_NAMESPACE_PREFIX);
}
