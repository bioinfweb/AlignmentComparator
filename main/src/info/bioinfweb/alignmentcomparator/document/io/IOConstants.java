package info.bioinfweb.alignmentcomparator.document.io;


import info.bioinfweb.commons.io.FormatVersion;

import javax.xml.namespace.QName;



public interface IOConstants {
	public static final FormatVersion NEXML_OUTPUT_VERSION = new FormatVersion(0, 0);  //TODO Increase to 1.x before release of AC 1.x.
	
	public static final String UNALIGNED_INDICES_ID_SUFFIX = "indices";

	public static final String SUPER_GAP_ENTITY = "-";
	
	public static final String DEFAULT_NAMESPACE_PREFIX = "ac";
	public static final String ONTOLOGY_NAMESPACE_URI = "http://bioinfweb.info/xmlns/AlignmentComparator/";  //TODO Add additional subfolder?
	
	public static final QName PREDICATE_APPLICATION_VERSION = new QName(ONTOLOGY_NAMESPACE_URI, "applicationVersion", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_FORMAT_VERSION = new QName(ONTOLOGY_NAMESPACE_URI, "formatVersion", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_UNALIGNED_INDICES = new QName(ONTOLOGY_NAMESPACE_URI, "unalignedIndices", DEFAULT_NAMESPACE_PREFIX);
}
