/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.decorate.DelegatedAlignmentModelView;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator;
import info.bioinfweb.libralign.model.utils.indextranslation.RandomAccessIndexTranslator;



public class OriginalAlignment extends DelegatedAlignmentModelView<Character> implements TranslatableAlignment {
	private ComparedAlignment owner;
	private IndexTranslator<Character> indexTranslator;
	
	
	public OriginalAlignment(ComparedAlignment owner, AlignmentModel<Character> underlyingModel) {
		super(underlyingModel);
		this.owner = owner;
		indexTranslator = new RandomAccessIndexTranslator<Character>(underlyingModel);  // "this" could as well be used.
	}


	public ComparedAlignment getOwner() {
		return owner;
	}


	public IndexTranslator<Character> getIndexTranslator() {
		return indexTranslator;
	}
}
