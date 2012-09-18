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
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.data.Alignments;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;



public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	
	private Alignments alignments = new Alignments();
	
	private JPanel jContentPane = null;
	private JMenuBar mainMenu = null;
	private JMenu fileMenu = null;
	private JScrollPane scrollPane = null;
	private AlignmentComparisonPanel comparisonPanel = null;

	
	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400, 300);
		this.setJMenuBar(getMainMenu());
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	
	public Alignments getAlignments() {
		return alignments;
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}


	/**
	 * This method initializes mainMenu	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenu() {
		if (mainMenu == null) {
			mainMenu = new JMenuBar();
			mainMenu.add(getFileMenu());
		}
		return mainMenu;
	}


	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
		}
		return fileMenu;
	}


	/**
	 * This method initializes scrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getComparisonPanel());
		}
		return scrollPane;
	}


	/**
	 * This method initializes comparisonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private AlignmentComparisonPanel getComparisonPanel() {
		if (comparisonPanel == null) {
			comparisonPanel = new AlignmentComparisonPanel(getAlignments());
			comparisonPanel.setLayout(new GridBagLayout());
		}
		return comparisonPanel;
	}
}
