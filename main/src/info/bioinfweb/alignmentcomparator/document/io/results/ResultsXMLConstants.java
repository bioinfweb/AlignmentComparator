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
package info.bioinfweb.alignmentcomparator.document.io.results;


import javax.xml.namespace.QName;

import info.bioinfweb.commons.io.FormatVersion;



public interface ResultsXMLConstants {
	public static final String NAMESPACE_URI = "http://bioinfweb.info/xmlns/AlignmentComparator/Results";
	public static final FormatVersion VERSION = new FormatVersion(1, 0);
	public static final String FULL_SCHEMA_LOCATION = NAMESPACE_URI + " " + NAMESPACE_URI + "/" + VERSION + ".xsd";
	
	public static final QName TAG_ROOT = new QName(NAMESPACE_URI, "alignment_comparison");
	
	public static final QName TAG_NAMES = new QName(NAMESPACE_URI, "names");
	public static final QName TAG_NAME = new QName(NAMESPACE_URI, "name");
	
	public static final QName TAG_ALTERNATIVES = new QName(NAMESPACE_URI, "alternatives");
	public static final QName TAG_ALTERNATIVE = new QName(NAMESPACE_URI, "alternative");
	public static final QName TAG_ALIGNMENT = new QName(NAMESPACE_URI, "alignment");
	public static final QName TAG_SEQUENCE = new QName(NAMESPACE_URI, "sequence");
	public static final QName TAG_GAP_PATTERN = new QName(NAMESPACE_URI, "gap_pattern");
	
	public static final char TOKEN_GAP = '-';
	public static final char TOKEN_CHARACTER = 'N';

	public static final QName TAG_COMMENTS = new QName(NAMESPACE_URI, "comments");
	public static final QName TAG_COMMENT = new QName(NAMESPACE_URI, "comment");
  public static final QName ATTR_COMMENT_FIRST_POS = new QName("first");
  public static final QName ATTR_COMMENT_LAST_POS = new QName("last");
}
