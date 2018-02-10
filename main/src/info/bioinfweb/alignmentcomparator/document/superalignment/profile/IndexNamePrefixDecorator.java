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
package info.bioinfweb.alignmentcomparator.document.superalignment.profile;


import java.util.Set;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.decorate.DelegatedAlignmentModelView;



/**
 * Alignment model adapter to be used in profile-profile-alignment comparison to create input files for the 
 * alignment software that contain different sequence names each.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class IndexNamePrefixDecorator extends DelegatedAlignmentModelView<Character> {
	private String prefix;
	
	
	public IndexNamePrefixDecorator(AlignmentModel<Character> underlyingModel, String prefix) {
		super(underlyingModel);
		this.prefix = prefix;
	}
	

	@Override
	public Set<String> sequenceIDsByName(String sequenceName) {
		return super.sequenceIDsByName(sequenceName.substring(prefix.length()));
	}

	
	@Override
	public String sequenceNameByID(String sequenceID) {
		return prefix + super.sequenceNameByID(sequenceID);
	}
}
