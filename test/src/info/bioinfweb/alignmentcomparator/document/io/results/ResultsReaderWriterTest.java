/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
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


import java.util.ArrayList;

import info.bioinfweb.alignmentcomparator.document.SuperAlignmentSequenceView;

import org.junit.* ;

import static org.junit.Assert.* ;



public class ResultsReaderWriterTest {
	private static final int GAP = SuperAlignmentSequenceView.GAP_INDEX;
	public static final Integer[] INDICES = {0, 1, GAP, 2, 3, GAP, GAP, GAP, 4, 5, GAP, 6, 7, 8, GAP, 9};
	public static final String GAP_PATTERN = "NN-NN---NN-NNN-N";
	
	
	@Test
	public void test_decodeGapPattern() {
		ArrayList<Integer> list = ResultsReader.decodeGapPattern(GAP_PATTERN);
		assertArrayEquals(INDICES, list.toArray(new Integer[list.size()]));
	}
	
	
//	@Test
//	public void test_encodeGapPattern() {
//		assertEquals(GAP_PATTERN, ResultsWriter.encodeGapPattern(INDICES));
//	}
}
