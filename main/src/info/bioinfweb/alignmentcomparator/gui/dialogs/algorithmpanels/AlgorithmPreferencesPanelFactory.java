/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


import info.bioinfweb.alignmentcomparator.document.superalignment.CompareAlgorithm;

import java.util.EnumMap;



public class AlgorithmPreferencesPanelFactory {
  private static AlgorithmPreferencesPanelFactory firstInstance = null;
  
  private EnumMap<CompareAlgorithm, AlgorithmPreferencesPanel> panels = 
	  new EnumMap<CompareAlgorithm, AlgorithmPreferencesPanel>(CompareAlgorithm.class);

  
  private AlgorithmPreferencesPanelFactory() {
  	super();
  	fillList();
  }
  
  
  public static AlgorithmPreferencesPanelFactory getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new AlgorithmPreferencesPanelFactory();
  	}
  	return firstInstance;
  }

  
  private void fillList() {
  	panels.put(CompareAlgorithm.MAX_SEQUENCE_PAIR_MATCH, new MaxSequencePairMatchPanel());
  	panels.put(CompareAlgorithm.AVERAGE_UNGAPED_POSITION, new AverageDegapedPositionPanel());
  	panels.put(CompareAlgorithm.MUSCLE_PROFILE, new MuscleProfilePanel());
  }
  

  public AlgorithmPreferencesPanel getPanel(CompareAlgorithm algorithm) {
  	return panels.get(algorithm);
  }
}
