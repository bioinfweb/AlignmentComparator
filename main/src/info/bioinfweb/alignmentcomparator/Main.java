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
package info.bioinfweb.alignmentcomparator;


import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.bioinfweb.commons.ProgramMainClass;
import info.bioinfweb.commons.appversion.ApplicationType;
import info.bioinfweb.commons.appversion.ApplicationVersion;
import info.bioinfweb.wikihelp.client.WikiHelp;



/**
 * The main class of this application.
 * 
 * @author Ben St&ouml;ver
 */
public class Main extends ProgramMainClass {
	public static final String APPLICATION_NAME = "AlignmentComparator"; 
	public static final String APPLICATION_URL = "http://bioinfweb.info/AlignmentComparator"; 
	public static final String ERROR_URL = APPLICATION_URL + "/errorreport/ApplicationReport.jsp";  //TODO zentrale bioinfweb error report URL?, da Reports ja auch zu einzelnen Bibliotheken kommen k�nnten und diese dann zentral gesammelt werden k�nnten 
	public static final String WIKI_URL = APPLICATION_URL + "/Help/";
	
	
	private static Main firstInstance = null;
	
	private MainFrame mainFrame = null;
	private WikiHelp wikiHelp = new WikiHelp(WIKI_URL);
	
	
  private Main() {
		super(new ApplicationVersion(0, 0, 0, 78, ApplicationType.ALPHA));
	}
  
  
  public static Main getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new Main();
  	}
  	return firstInstance;
  }

  
	public WikiHelp getWikiHelp() {
		return wikiHelp;
	}


	public MainFrame getMainFrame() {
		return mainFrame;
	}


	private void startUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//checkUpdate();
		SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mainFrame = new MainFrame();
						mainFrame.setVisible(true); 
					}
				});
  }
	
	
	public static void main(String[] args) {
		getInstance().startUI();
	}
}
