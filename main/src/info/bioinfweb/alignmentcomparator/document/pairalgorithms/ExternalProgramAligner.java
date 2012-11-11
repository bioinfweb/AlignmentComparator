package info.bioinfweb.alignmentcomparator.document.pairalgorithms;


import info.webinsel.util.io.IOUtils;

import java.io.File;
import java.io.IOException;



public abstract class ExternalProgramAligner implements SuperAlignmentAlgorithm {
	public static final String CMD_SUBFOLDER = "cmd";
	
	
  protected File createTempFile(String prefix, String extension) throws IOException {
		return File.createTempFile(IOUtils.getClassDir(getClass()) + System.getProperty("file.separator") + prefix, "." + extension);
  }
  
  
  public String cmdFolder() {
  	return IOUtils.getClassDir(getClass()) + System.getProperty("file.separator") + CMD_SUBFOLDER + System.getProperty("file.separator");  
  }
}
