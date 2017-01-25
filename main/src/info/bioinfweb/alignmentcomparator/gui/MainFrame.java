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
package info.bioinfweb.alignmentcomparator.gui;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.gui.actions.ActionManagement;
import info.bioinfweb.libralign.multiplealignments.SwingMultipleAlignmentsContainer;
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.WindowConstants;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;



/**
 * The main window of AlignmentComporator.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class MainFrame extends JFrame {
	public static final String TITLE_PREFIX = "AlignmentComparator";
	
	private static final long serialVersionUID = 1L;
	
	
	private WindowListener windowListener = null;
	private Document document = new Document();  //TODO Instance can only be created after the token type has been determined from the file.
	private ActionManagement actionManagement = new ActionManagement(this);
	
	private JPanel jContentPane = null;
	private AlignmentComparisonComponent comparisonComponent = null;
	private JMenuBar mainMenu = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenu undoMenu = null;
	private JMenu redoMenu = null;

	
	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
		addWindowListener(getWindowListener());
	}

	
	public void updateTitle() {
		String title = getDocument().getDefaultNameOrPath();
		if (getDocument().hasChanged()) {
			title = "*" + title;
		}
		setTitle(TITLE_PREFIX + " - " + title);
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle(TITLE_PREFIX);
		this.setSize(640, 480);
		this.setJMenuBar(getMainMenu());
		this.setContentPane(getJContentPane());
		getDocument().addDocumentListener(getComparisonComponent());
		getActionManagement().refreshActionStatus();
	}

	
	private WindowListener getWindowListener() {
		if (windowListener == null) {
			windowListener = new WindowAdapter() {
					@Override
					public void windowActivated(WindowEvent e) {
						getComparisonPanel().requestFocus();
					}
	
					@Override
					public void windowClosing(WindowEvent e) {
						if (getDocument().askToSave()) {
							//CurrentDirectoryModel.getInstance().removeFileChooser(getDocument().getFileChooser());
							//ExtendedScrollPaneSelector.uninstallScrollPaneSelector(getScrollPane());
							setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						}
						else {
							setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						}
					}
				};
		}
		return windowListener;
	}

	
	/**
	 * Asks the user whether to save all opened documents and closes the application if the user did not
	 * cancel the process. 
	 */
	public void close() {
		processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}


	public Document getDocument() {
		return document;
	}
	
	
	public ActionManagement getActionManagement() {
		return actionManagement;
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
			jContentPane.add(getComparisonPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}
	
	
	public AlignmentComparisonComponent getComparisonComponent() {
		if (comparisonComponent == null) {
			comparisonComponent = new AlignmentComparisonComponent(this);
		}
		return comparisonComponent;
	}
	
	
	public AlignmentComparisonSelection getSelection() {
		return getComparisonComponent().getSelection();
	}
	
	
	private SwingMultipleAlignmentsContainer getComparisonPanel() {
		return (SwingMultipleAlignmentsContainer)SwingComponentFactory.getInstance().getSwingComponent(getComparisonComponent());
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
			mainMenu.add(getEditMenu());
			mainMenu.add(getHelpMenu());
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
			fileMenu.add(getActionManagement().get("file.compareAlignments"));
			fileMenu.add(getActionManagement().get("file.openResults"));
			fileMenu.add(getActionManagement().get("file.save"));
			fileMenu.add(getActionManagement().get("file.saveAs"));
		}
		return fileMenu;
	}


	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getUndoMenu());
			editMenu.add(getRedoMenu());
			editMenu.addSeparator();
			editMenu.add(new JCheckBoxMenuItem(getActionManagement().get("edit.syncSelection")));
		}
		return editMenu;
	}


	public JMenu getUndoMenu() {
		if (undoMenu == null) {
			undoMenu = new JMenu("Undo");
			undoMenu.setMnemonic(KeyEvent.VK_U);
			undoMenu.setIcon(new ImageIcon(Object.class.getResource("/resources/symbols/Undo16.png")));
		}
		return undoMenu;
	}
	
	
	public JMenu getRedoMenu() {
		if (redoMenu == null) {
			redoMenu = new JMenu("Redo");
			redoMenu.setMnemonic(KeyEvent.VK_R);
			redoMenu.setIcon(new ImageIcon(Object.class.getResource("/resources/symbols/Redo16.png")));
		}
		return redoMenu;
	}
	
	
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu("Help");
			helpMenu.add(getActionManagement().get("help.website"));
			helpMenu.add(getActionManagement().get("help.about"));
		}
		return helpMenu;
	}
}
