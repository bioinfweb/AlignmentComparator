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


import java.util.Arrays;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexTranslator;

import org.junit.*;

import static org.junit.Assert.*;
import static info.bioinfweb.libralign.test.LibrAlignTestTools.*;



public class SuperalingedModelIndexTranslatorTest {
	@Test
	public void test_getAlignedIndex() {
		AlignmentModel<Character> model = new PackedAlignmentModel<>(CharacterTokenSet.newDNAInstance(false));
		String id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("-A-TGC-", model.getTokenSet()));
		ComparedAlignment comparedAlignment = new ComparedAlignment(model);
		// 01234567891
		// .-A-T..GC.-
		comparedAlignment.createSuperaligned(Arrays.asList(SuperalignedModelDecorator.SUPER_GAP_INDEX, 0, 1, 2, 3, 
				SuperalignedModelDecorator.SUPER_GAP_INDEX, SuperalignedModelDecorator.SUPER_GAP_INDEX, 4, 5, 
				SuperalignedModelDecorator.SUPER_GAP_INDEX, 6));
		
		IndexTranslator<Character> t = comparedAlignment.getSuperaligned().getIndexTranslator();
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, 0, t.getUnalignedIndex(id, 0));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, 0, t.getUnalignedIndex(id, 1));
		assertIndexRelation(0, 0, 0, t.getUnalignedIndex(id, 2));
		assertIndexRelation(0, IndexRelation.GAP, 1, t.getUnalignedIndex(id, 3));
		assertIndexRelation(1, 1, 1, t.getUnalignedIndex(id, 4));
		assertIndexRelation(1, IndexRelation.GAP, 2, t.getUnalignedIndex(id, 5));
		assertIndexRelation(1, IndexRelation.GAP, 2, t.getUnalignedIndex(id, 6));
		assertIndexRelation(2, 2, 2, t.getUnalignedIndex(id, 7));
		assertIndexRelation(3, 3, 3, t.getUnalignedIndex(id, 8));
		assertIndexRelation(3, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, t.getUnalignedIndex(id, 9));
		assertIndexRelation(3, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, t.getUnalignedIndex(id, 10));
	}
}
