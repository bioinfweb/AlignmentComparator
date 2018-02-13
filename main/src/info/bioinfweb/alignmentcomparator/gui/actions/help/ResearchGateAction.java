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
package info.bioinfweb.alignmentcomparator.gui.actions.help;


import java.awt.event.KeyEvent;

import javax.swing.Action;

import info.bioinfweb.commons.swing.actions.OpenWebsiteAction;



public class ResearchGateAction extends OpenWebsiteAction {
	public ResearchGateAction() {
		super("http://r.bioinfweb.info/RGAlignmentComparator");
		
		putValue(Action.NAME, "AlignmentComparator on ResearchGate"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 25);
	  loadSymbols("ResearchGate");
	}
}
