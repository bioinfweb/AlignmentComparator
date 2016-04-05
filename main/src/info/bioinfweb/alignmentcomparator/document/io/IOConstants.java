package info.bioinfweb.alignmentcomparator.document.io;


import javax.xml.namespace.QName;



public interface IOConstants {
	public static final String OTUS_ID = "otus";
	public static final String UNALIGNED_INDICES_ID_SUFFIX = "indices";

	public static final String SUPER_GAP_ENTITY = "-";
	
	public static final String ONTOLOGY_NAMESPACE_URI = "http://bioinfweb.info/xmlns/JPhyloIO";  //TODO Add additional subfolder?
	public static final QName UNALIGNED_INDICES_PREDICATE = new QName(ONTOLOGY_NAMESPACE_URI, "unalignedIndices");
}
