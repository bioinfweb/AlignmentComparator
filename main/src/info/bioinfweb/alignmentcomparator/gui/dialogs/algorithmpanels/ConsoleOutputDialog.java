/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2012  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.gui.dialogs.algorithmpanels;


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
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;



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
		setSize(300, 200);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		closeButton = new JButton("Close");
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
	  allowClose(false);
	}
	
	
	public void clearOutput() {
		getTextArea().setText("");
	}
	
	
	public void addLine(String text) {
		getTextArea().append(text + SystemUtils.LINE_SEPARATOR);
	}
	
	
	public void addLineOutsideSwing(String text) {
		final String currentLine = text;
		SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						addLine(currentLine);
					}
				});
	}
	
	
	public Thread addStream(InputStream stream) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		Thread result = new Thread(new Runnable() {
					@Override
					public void run() {
						String line;
						try {
							try {
								while ((line = reader.readLine()) != null) {
									addLineOutsideSwing(line);
						    }
							}
							finally {
								reader.close();
							}
						}
						catch (IOException e) {
							getTextArea().append("ERROR: An error occurred when reading from the specified stream. (" + 
						      e.getMessage() + ")");
							e.printStackTrace();
						}
					}
				});
		result.start();
		return result;
	}
	
	
	public void allowClose(boolean flag) {
		getCloseButton().setEnabled(flag);
	}
}
