/*
 * AlignmentComparator - An application to efficiently visualize and annotate differences between alternative multiple sequence alignments
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.alignmentcomparator.gui.actions.help;


import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.alignmentcomparator.Main;
import info.bioinfweb.commons.swing.ExtendedAbstractAction;



/**
 * Action object that opens the AlignmentComparator website.
 * 
 * @author Ben St&ouml;ver
 */
public class WebsiteAction extends ExtendedAbstractAction {
	public WebsiteAction() {
		super();
		
		putValue(Action.NAME, "AlignmentComparator website"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
	  //loadSymbols("AlignmentComparator");
	}

	
	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URI(Main.APPLICATION_URL));
		}
		catch (URISyntaxException | IOException ex) {
			JOptionPane.showMessageDialog(Main.getInstance().getMainFrame(), 
					"An error occurred when trying open the selected link. (" + ex.getMessage() + ")", 
					"Navigation failed,", JOptionPane.ERROR_MESSAGE);
		}
	}
}
