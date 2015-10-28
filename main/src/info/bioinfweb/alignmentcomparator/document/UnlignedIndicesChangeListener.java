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
