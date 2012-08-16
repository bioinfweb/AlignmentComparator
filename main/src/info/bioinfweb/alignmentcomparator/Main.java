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
package info.bioinfweb.alignmentcomparator;


import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import info.bioinfweb.alignmentcomparator.gui.MainFrame;
import info.webinsel.util.ProgramMainClass;
import info.webinsel.util.appversion.ApplicationType;
import info.webinsel.util.appversion.ApplicationVersion;
import info.webinsel.wikihelp.client.SwingErrorHandler;
import info.webinsel.wikihelp.client.WikiHelp;



/**
 * The main class of this application.
 * 
 * @author Ben St&ouml;ver
 */
public class Main extends ProgramMainClass {
	public static final String APPLICATION_URL = "http://bioinfweb.info/Software/AlignmentComparator"; 
	public static final String ERROR_URL = APPLICATION_URL + "/errorreport/ApplicationReport.jsp";  //TODO zentrale bioinfweb error report URL?, da Reports ja auch zu einzelnen Bibliotheken kommen könnten und diese dann zentral gesammelt werden könnten 
	public static final String WIKI_URL = APPLICATION_URL + "/Help/";
	
	
	private static Main firstInstance = null;
	
	private WikiHelp wikiHelp = new WikiHelp(WIKI_URL, new SwingErrorHandler(WIKI_URL));
	
	
  private Main() {
		super(new ApplicationVersion(0, 0, 0, 3, ApplicationType.ALPHA));
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
						MainFrame.getInstance().setVisible(true); 
						//openInitialFile();
					}
				});
  }
	
	
	public static void main(String[] args) {
		getInstance().startUI();
	}
}
