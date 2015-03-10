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
package info.bioinfweb.alignmentcomparator.document.superalignment;


import info.bioinfweb.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;



public abstract class ExternalProgramAligner implements SuperAlignmentAlgorithm {
	public static final String CMD_SUBFOLDER = "cmd";
	
	
  protected File createTempFile(String prefix, String extension) throws IOException {
		return File.createTempFile(prefix, "." + extension);
  }
  
  
  public String cmdFolder() {
  	return IOUtils.getClassDir(getClass()) + System.getProperty("file.separator") + CMD_SUBFOLDER + System.getProperty("file.separator");
  }
  
  
  public boolean is64BitJRE() {
  	return System.getProperty("os.arch").contains("64");
  }
  
  
  /**
   * Should return the full path to the external application to be used dependent on the current operating system.
   */
  public abstract String getApplicationName();
}
