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
package info.bioinfweb.alignmentcomparator.document;


import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;



public class SuperAlignmentCompoundSet extends AlignmentAmbiguityNucleotideCompoundSet {
	public static final String SUPER_ALIGNMENT_GAP = ".";
	
	private static SuperAlignmentCompoundSet sharedInstance = null;
	
	
	/**
	 * Returns a new instance of this class.
	 */
	public SuperAlignmentCompoundSet() {
		super();
		addNucleotideCompound(SUPER_ALIGNMENT_GAP, SUPER_ALIGNMENT_GAP);
	}

	
	/**
	 * Returns a shared instance of this class.
	 */
	public static SuperAlignmentCompoundSet getSuperAlignmentCompoundSet() {
		if (sharedInstance == null) {
			sharedInstance = new SuperAlignmentCompoundSet();
		}
		return sharedInstance;
	}
}
