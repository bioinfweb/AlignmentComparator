/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/AlignmentComparator>
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


import java.util.List;

import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;



public class UnlignedIndicesChangeListener implements ListChangeListener<Integer> {
	private List<Character> removedTokens = null;
	
	
	@Override
	public void beforeElementsAdded(ListAddEvent<Integer> event) {}
	

	@Override
	public void afterElementsAdded(ListAddEvent<Integer> event) {
		
	}
	

	@Override
	public void beforeElementReplaced(ListReplaceEvent<Integer> event) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void afterElementReplaced(ListReplaceEvent<Integer> event) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void beforeElementsRemoved(ListRemoveEvent<Integer, Object> event) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void afterElementsRemoved(ListRemoveEvent<Integer, Integer> event) {
		// TODO Auto-generated method stub
		
	}
}
