/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
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
package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.alignmentcomparator.document.undo.MoveCommentEdit;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;



public class MoveCommentAction extends DocumentAction {
	public MoveCommentAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Move comment"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
		putValue(Action.SHORT_DESCRIPTION, "Move comment"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('M', 
		    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		getDocument().executeEdit(new MoveCommentEdit(getDocument(), getSelection().getComment(), getSelection().createCommentAnchor()));
	}

	
	@Override
	public void setEnabled() {
		setEnabled(getSelection().isSequenceSelected() && getSelection().isCommentSelected());
	}
}
