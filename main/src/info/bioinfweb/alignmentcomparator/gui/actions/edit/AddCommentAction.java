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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.alignmentcomparator.document.comment.Comment;
import info.bioinfweb.alignmentcomparator.document.undo.AddCommentEdit;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;



public class AddCommentAction extends DocumentAction {
	public AddCommentAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Add comment"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		putValue(Action.SHORT_DESCRIPTION, "Add comment"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', 
		    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	  loadSymbols("NewComment");
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String text = JOptionPane.showInputDialog(getMainFrame(), "Comment text:");
		if (text != null) {
			getDocument().executeEdit(new AddCommentEdit(getDocument(), new Comment(text, getSelection().createCommentAnchor())));
		}
	}

	
	@Override
	public void setEnabled() {
		setEnabled(getSelection().isSequenceSelected());
	}
}
