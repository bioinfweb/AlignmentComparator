package info.bioinfweb.alignmentcomparator.document.superalignment.maximumsequencematch;


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.alignmentcomparator.document.ComparedAlignment;
import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.superalignment.SuperAlignmentAlgorithm;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.tokenset.AbstractTokenSet;
import info.bioinfweb.libralign.model.utils.DegapedIndexCalculator;



public class MaximumSequenceMatchAligner implements SuperAlignmentAlgorithm {
	private Iterator<Integer> createSequenceIDIterator(Document alignments) {
		return alignments.getAlignments().getValue(0).getOriginal().sequenceIDIterator();
	}
	
	
	private int minAlignmentLength(Document alignments) {
		int result = 0;
		for (ComparedAlignment alignment : alignments.getAlignments().values()) {
			result = Math.min(result, alignment.getOriginal().getMaxSequenceLength());
		}
		return result;
	}
	
	
	private AlignmentNode createGraph(Document alignments) {  //TODO Can this method also be efficiently implemented with more than two models? 
		// Create index calculators:
		DegapedIndexCalculator<Character>[] calculators = new DegapedIndexCalculator[alignments.getAlignments().size()];
		for (int i = 0; i < calculators.length; i++) {
			calculators[i] = new DegapedIndexCalculator<Character>(alignments.getAlignments().get(
					alignments.getAlignments().get(i)).getOriginal()); 
		}
		
		// Create map of current column positions:
//		Map<Integer, int[]> positionsMap = new TreeMap<Integer, int[]>();
//		int alignmentCount = alignments.getAlignments().size();
//		Iterator<Integer> idIterator = 
//				alignments.getAlignments().get(alignments.getAlignments().get(0)).getOriginal().sequenceIDIterator();
//		while (idIterator.hasNext()) {
//			int[] positions = new int[alignmentCount];
//			for (int i = 0; i < positions.length; i++) {
//				positions[i] = 0;
//			}
//			positionsMap.put(idIterator.next(), positions);
//		}

		//TODO Algorithmus wie im Wiki beschrieben implementieren und dabei eine Datenstruktur füllen, die 
		//     Positionen der Supergaps und Verknüpfungen der alternativen Alignierungen abbildet.
		
		Map<Integer, AlignmentNode> currentNodes = new TreeMap<Integer, AlignmentNode>();
		
		// Create start node:
		AlignmentNode start = new AlignmentNode();
		start.setPosition(0, -1);
		start.setPosition(1, -1);
		Iterator<Integer> idIterator = createSequenceIDIterator(alignments);
		while (idIterator.hasNext()) {  // Add all sequences to the start node
			start.getSequences().add(idIterator.next());
		}
		
		//TODO Create end node here?
		
		// Set start node as the current node for all sequences:
		idIterator = createSequenceIDIterator(alignments);
		while (idIterator.hasNext()) {  // Add all sequences to the start node
			currentNodes.put(idIterator.next(), start);
		}
		
		// Add all possible superalignments to graph:
		int minColumnCount = minAlignmentLength(alignments);
		int[] unalignedPositions = new int[alignments.getAlignments().size()];
		int[] alignedPositions = new int[alignments.getAlignments().size()];
		boolean[] gaps = new boolean[alignments.getAlignments().size()];
		for (int column = 0; column < minColumnCount; column++) {
			idIterator = createSequenceIDIterator(alignments);
			while (idIterator.hasNext()) {
				int sequenceID = idIterator.next();
				for (int i = 0; i < alignments.getAlignments().size(); i++) {
					alignedPositions[i] = Math.max(column, currentNodes.get(sequenceID).getPosition(i)); 
				}
				
				if (!((alignedPositions[0] > column) && (alignedPositions[1] > column))) {
					for (int i = 0; i < alignments.getAlignments().size(); i++) {
						char token = alignments.getAlignments().getValue(i).getOriginal().getTokenAt(sequenceID, alignedPositions[i]);
						gaps[i] = (token == AbstractTokenSet.DEFAULT_GAP_REPRESENTATION);
						unalignedPositions[i] = calculators[i].degapedIndex(sequenceID, alignedPositions[i]);
					}
					
					if (gaps[0] && !gaps[1]) {
						//TODO Add node for new supergap (if there is not an according node present from another sequence)
						// Evtl. ist es besser Knoten zu erzeugen und dessen Indices in kommenden Schritten zu erhöhen, direkt bis zum 
						// Ende einer Supergap zu laufen?
						// - Kann es zu Problemen kommen, wenn eine "Vereinigungskante" zu einem Knoten erstellt wird, der danach noch
						//   vergrößert wird?
						// - Weiteres Problem: Koten kann (ohne Vergleich mit vorherigem) nicht angesehen werden, in welchem Alignment 
						//   eine Supergap eingefügt wurde (ob der selbe Fall wie momentan vorlag). Bei Vereingingungen können sogar
						//   veschiedene Fälle zu diesem Knoten geführt haben. => Doppelte Verkettung (also zweite Zeigerliste pro Knoten)
						//   kann eine Lösung sein.
						// => Ist dieses Modell zu kompliziert und bietet zu wenig Vorteile im Vergleich zu einer DP-Matrix?
					}
					else if (!gaps[0] && gaps[1]) {
						//TODO Add node for new supergap
					}
					else {
						
					}
					//TODO Check three cases and possibly add new node to graph and link it to the previous node(s)
				}
			}
		}
		
		return start;
	}
	
	
	@Override
	public void performAlignment(Document alignments) throws Exception {
		if (alignments.getAlignments().size() != 2) {
			throw new IllegalArgumentException("This algorithm currently only supports comparing two alignments.");
		}
		else {
			//TODO implement
		}
	}
}
