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
package info.bioinfweb.alignmentcomparator.gui.dialogs;


import info.bioinfweb.alignmentcomparator.Main;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.lang3.SystemUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ConsoleOutputDialog extends JDialog {
	private static final long serialVersionUID = 8577239488073529842L;
	
	private static ConsoleOutputDialog firstInstance = null;
	

	private JTextArea textArea;
	private JButton closeButton;
	
	
	public static ConsoleOutputDialog getInstance() {
		if (firstInstance == null) {
			firstInstance = new ConsoleOutputDialog(Main.getInstance().getMainFrame());
		}
		return firstInstance;
	}
	
	
	/**
	 * Create the dialog.
	 */
	private ConsoleOutputDialog(Frame owner) {
		super(owner, false);
		setSize(500, 400);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Console output");
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
		panel.add(closeButton);
	}


	protected JTextArea getTextArea() {
		return textArea;
	}
	
	
	protected JButton getCloseButton() {
		return closeButton;
	}
	
	
	public void showEmpty() {
		setVisible(true);
	  clearOutput();
	  setAllowClose(false);
	}
	
	
	public void clearOutput() {
		getTextArea().setText("");
	}
	
	
	public void addLine(String text) {
		getTextArea().append(text + SystemUtils.LINE_SEPARATOR);
	}
	
	
	public void addStream(InputStream stream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				addLine(line);
	    }
		}
		finally {
			reader.close();
		}
	}
	
	
	public void setAllowClose(boolean flag) {
		getCloseButton().setEnabled(flag);
	}
}
