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


import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.libralign.model.AlignmentModelChangeAdapter;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.utils.indextranslation.AbstractIndexTranslator;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public class SuperalingedModelIndexTranslator extends AbstractIndexTranslator<Character, Object> {
	private List<Integer> alignedIndices;
	private List<Integer> unalignedIndices;
	private boolean autoUpdateMapping;
	private boolean updateRequired;
	
	
	public SuperalingedModelIndexTranslator(SuperalignedModelDecorator model, List<Integer> unalignedIndices) {
		super(model, new HashSet<Character>());
		getGapTokens().add(SequenceUtils.GAP_CHAR);
		getGapTokens().add(SuperalignedModelDecorator.SUPER_ALIGNMENT_GAP);
		this.unalignedIndices = unalignedIndices;
		
		autoUpdateMapping = true;
		updateRequired = true;
		recreateAlignedIndexList();
		
		getModel().getChangeListeners().add(new AlignmentModelChangeAdapter() {
			@Override
			public <T> void afterTokenChange(TokenChangeEvent<T> e) {
				updateRequired = true;
				recreateAlignedIndexList();  // Will only be executed if isAutoUpdateMapping() return true.
			}
		});
	}
	
	
	public boolean isAutoUpdateMapping() {
		return autoUpdateMapping;
	}


	public void setAutoUpdateMapping(boolean autoUpdateMapping) {
		this.autoUpdateMapping = autoUpdateMapping;
		if (autoUpdateMapping) {
			recreateAlignedIndexList();
		}
	}


	public boolean isUpdateRequired() {
		return updateRequired;
	}


	private void recreateAlignedIndexList() {
		if (isAutoUpdateMapping() && isUpdateRequired()) {
			if (alignedIndices == null) {
				alignedIndices = new ArrayList<Integer>(getModel().getUnderlyingModel().getMaxSequenceLength());
			}
			else {
				alignedIndices.clear();
			}
			
			int alignedIndex = 0;
			while (alignedIndex < getModel().getMaxSequenceLength()) {
				if (!getModel().containsSupergap(alignedIndex)) {
					alignedIndices.add(alignedIndex);
				}
				alignedIndex++;
			}
			updateRequired = false;
		}
	}


	@Override
	public SuperalignedModelDecorator getModel() {
		return (SuperalignedModelDecorator)super.getModel();
	}

	
	private IndexTranslator<Character> getUnderlyingIndexTranslator() {
		return getModel().getUnderlyingModel().getIndexTranslator();
	}
	

	@Override
	public int getUnalignedLength(String sequenceID) {
		return getUnderlyingIndexTranslator().getUnalignedLength(sequenceID);
	}

	
	@Override
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex) {
		if (getModel().containsSupergap(alignedIndex)) {
			int unalignedIndexBefore = unalignedIndices.get(alignedIndex);
			int unalignedIndexAfter;
			if (unalignedIndexBefore == IndexRelation.OUT_OF_RANGE) {
				unalignedIndexAfter = 0;
			}
			else {
				unalignedIndexBefore = getUnderlyingIndexTranslator().getUnalignedIndex(sequenceID, unalignedIndexBefore).getBefore();
				if (unalignedIndexBefore == IndexRelation.OUT_OF_RANGE) {
					unalignedIndexAfter = 0;
				}
				else {
					unalignedIndexAfter = unalignedIndexBefore + 1;
				}
			}
			if (unalignedIndexAfter >= getUnderlyingIndexTranslator().getUnalignedLength(sequenceID)) {
				unalignedIndexAfter = IndexRelation.OUT_OF_RANGE;
			}
			return new IndexRelation(unalignedIndexBefore, IndexRelation.GAP, unalignedIndexAfter);
		}
		else {
			return getUnderlyingIndexTranslator().getUnalignedIndex(sequenceID, unalignedIndices.get(alignedIndex));
		}
	}

	
	@Override
	public int getAlignedIndex(String sequenceID, int unalignedIndex) {
		return alignedIndices.get(getUnderlyingIndexTranslator().getAlignedIndex(sequenceID, unalignedIndex));
	}

	
	/**
	 * Always returns {@code null}.
	 * <p>
	 * Sequence data objects are not needed in this implementation. (The ones from the translator of the underlying 
	 * model are sufficient, since this class does not store any sequence-specific information.)
	 * 
	 * @param sequenceID the sequence ID
	 * @return always {@code null}
	 */
	@Override
	protected Object createSequenceData(String sequenceID) {
		return null;
	}
}
