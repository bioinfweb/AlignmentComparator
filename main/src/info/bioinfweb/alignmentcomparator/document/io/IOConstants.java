package info.bioinfweb.alignmentcomparator.document.io;


import javax.xml.namespace.QName;



public interface IOConstants {
	public static final String DOCUMENT_TAG_ID = "AlignmentComparatorTag";
//	public static final String OTUS_ID = "otus";
	public static final String UNALIGNED_INDICES_ID_SUFFIX = "indices";

	public static final String SUPER_GAP_ENTITY = "-";
	
	public static final String DEFAULT_NAMESPACE_PREFIX = "ac";
	public static final String ONTOLOGY_NAMESPACE_URI = "http://bioinfweb.info/xmlns/AlignmentComparator/";  //TODO Add additional subfolder?
	public static final QName PREDICATE_IS_COMPARISON = new QName(ONTOLOGY_NAMESPACE_URI, "isComparisonDocument", DEFAULT_NAMESPACE_PREFIX);
	public static final QName PREDICATE_UNALIGNED_INDICES = new QName(ONTOLOGY_NAMESPACE_URI, "unalignedIndices", DEFAULT_NAMESPACE_PREFIX);
}
