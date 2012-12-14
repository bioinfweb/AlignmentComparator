/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben St�ver
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
package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import java.awt.event.KeyEvent;

import javax.swing.Action;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;



public class RemoveGapFirstAlignmentAction extends RemoveGapAction {
	public RemoveGapFirstAlignmentAction(MainFrame mainFrame) {
		super(mainFrame, true);
		putValue(Action.NAME, "Remove gap from first alignment"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
		putValue(Action.SHORT_DESCRIPTION, "Remove gap from first alignment"); 
	}
}
