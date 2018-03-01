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
package info.bioinfweb.alignmentcomparator.gui.actions.edit;


import info.bioinfweb.alignmentcomparator.document.comment.Comment;
import info.bioinfweb.alignmentcomparator.document.undo.EditCommentTextEdit;
import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.alignmentcomparator.gui.actions.DocumentAction;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;



public class EditCommentTextAction extends DocumentAction {
	public EditCommentTextAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Change comment text"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		putValue(Action.SHORT_DESCRIPTION, "Change comment text"); 
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('T', 
		    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		Comment comment = getSelection().getComment();
		String newText = JOptionPane.showInputDialog(getMainFrame(), "Comment text:", comment.getText());
		if (newText != null) {
			getDocument().executeEdit(new EditCommentTextEdit(getDocument(), newText, comment));
		}
	}


	@Override
	public void setEnabled() {
		setEnabled(getSelection().isCommentSelected());
	}
}
