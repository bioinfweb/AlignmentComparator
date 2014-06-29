package info.bioinfweb.alignmentcomparator.gui.dialogs;


import info.bioinfweb.alignmentcomparator.Main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;



/**
 * The about dialog of AlignmentComparator
 * 
 * @author Ben St&ouml;ver
 */
public class AboutDialog extends JDialog {
	public static final String RESOURCES_PATH = "/resources/about/";  //  @jve:decl-index=0:
	
	
	public final HyperlinkListener HYPERLINK_LISTENER = 		
		  new javax.swing.event.HyperlinkListener() {
					public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
						if (e.getEventType().equals(EventType.ACTIVATED)) {
							try {
								Desktop.getDesktop().browse(e.getURL().toURI());
							}
							catch (Exception ex) {
								JOptionPane.showMessageDialog(getOwner(), 
										"An error occurred when trying open the selected link.", 
										"Navigation failed,", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				};
	
	
	private static String getResourcePath(String file) {
		return AboutDialog.class.getResource(RESOURCES_PATH + file).toString();
	}
	
	
	/**
	 * Create the dialog.
	 */
	public AboutDialog(Frame parent) {
		super(parent);  //TODO is this modal?
		
		setTitle("About AlignmentComarator");
		setSize(new Dimension(600, 600));
		//setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				JEditorPane infoPane = new JEditorPane();
				infoPane.setContentType("text/html");
				infoPane.setText("<html>" +
						"<head><link rel='stylesheet' type='text/css' href='" + 
						    getResourcePath("Style.css") + "'></head>" +
						"<body>" +
						"<h1>AlignmentComparator " + Main.getInstance().getVersion().toString() + "</h1>" +
						"<p>Copyright (C) 2012-2014 <a href='http://bioinfweb.info/People/Stoever'>Ben St&ouml;ver</a>. All rights reserved.<br>" +
						"Website: <a href='http://bioinfweb.info/AlignmentComparator/'>http://bioinfweb.info/AlignmentComparator</a></p>" +
						
						"<p>This program is free software: you can redistribute it and/or modify it " +
						"under the terms of the GNU General Public License as published by the Free Software " +
						"Foundation, either version 3 of the License, or (at your option) any later version.</p>" +
						
						"<p>This program is distributed in the hope that it will be useful, but " +
						"WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY " +
						"or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for " +
						"more details.</p>" +
						
						"<p>You should have received a copy of the GNU General Public License " +
						"along with this program. If not, see " +
						"<a href='http://bioinfweb.info/AlignmentComparator/License'>http://bioinfweb.info/AlignmentComparator/License</a> " +
						"or <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>." +
						
						"<p><b>The following libraries are used by AlignmentComparator:</b></p>" +
						"<ul>" +
    				  "<li>bioinfweb.commons.java (<a href='http://commons.bioinfweb.info/Java/'>http://commons.bioinfweb.info/Java/</a>)</li>" +
    				  //"<li>LibrAlign (<a href='http://bioinfweb.info/LibrAlign/'>http://bioinfweb.info/LibrAlign/</a>)</li>" +
  					  "<li>BioJava (<a href='http://biojava.org/'>http://biojava.org/</a>)</li>" +
						  "<li>Apache commons (<a href='http://commons.apache.org/'>http://commons.apache.org/</a>)</li>" +  //TODO Complete
						  "<li>Guava</li>" +
						  "<li>Browser Launcher (<a href='http://browserlaunch2.sourceforge.net/'>http://browserlaunch2.sourceforge.net/</a>)</li>" +
						"</ul>" +
						"</body></html>");			
				infoPane.setCaretPosition(0);
				infoPane.setEditable(false);
				infoPane.addHyperlinkListener(HYPERLINK_LISTENER);

				scrollPane.setViewportView(infoPane);
			}
		}
	}
}
