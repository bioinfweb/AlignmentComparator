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


import java.io.File;

import javax.swing.filechooser.FileFilter;



public class ResultsFileFilter extends FileFilter {
	public static final String EXTENSION = ".xml"; 
	
	private static ResultsFileFilter firstInstance = null;
	
	
	protected ResultsFileFilter() {
		super();
	}
	
	
	public static ResultsFileFilter getInstance() {
		if (firstInstance == null) {
			firstInstance = new ResultsFileFilter();
		}
		return firstInstance;
	}
	
	
	@Override
	public String getDescription() {
		return "AlignmentComparator results (*" + EXTENSION + ")";
	}

	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getAbsolutePath().toLowerCase().endsWith(EXTENSION);
		//TODO XML Namespace auslesen und nur entsprechende XML-Dateien zulassen
	}
}
