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


import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Sequence;
import org.biojava3.core.sequence.template.SequenceProxyView;
import org.biojava3.core.sequence.template.SequenceView;



public class SuperAlignmentSequenceView extends SequenceProxyView<NucleotideCompound> 
    implements SequenceView<NucleotideCompound> {
	
	public static final int GAP_INDEX = -1; 
	
	private Document parent;
	private int alignmentIndex;
	

	public SuperAlignmentSequenceView(Document parent, int alignmentIndex, Sequence viewedSequence) {
		super(viewedSequence);
		this.alignmentIndex = alignmentIndex;
		this.parent = parent;
	}


	@Override
	public NucleotideCompound getCompoundAt(int position) {
		int index = parent.getUnalignedIndex(alignmentIndex, position - 1); 
		if (index == GAP_INDEX) {
			return SuperAlignmentCompoundSet.getSuperAlignmentCompoundSet().getCompoundForString(
					SuperAlignmentCompoundSet.SUPER_ALIGNMENT_GAP);
		}
		else {
			return super.getCompoundAt(index);
		}
	}

	
	@Override
	public int getLength() {
		return parent.getAlignedLength();
	}
}
