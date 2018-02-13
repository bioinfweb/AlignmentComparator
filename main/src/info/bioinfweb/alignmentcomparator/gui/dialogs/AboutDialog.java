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
package info.bioinfweb.alignmentcomparator.gui.dialogs;


import info.bioinfweb.alignmentcomparator.Main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;



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
		setSize(new Dimension(600, 400));
		//setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								setVisible(false);
							}
						});
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
						"<h1><i>AlignmentComparator</i> " + Main.getInstance().getVersion().toString() + "</h1>" +
						"<p>Copyright (C) 2012-2017 <a href='http://bioinfweb.info/People/Stoever'>Ben St&ouml;ver</a>. All rights reserved.<br>" +
						"Website: <a href='http://bioinfweb.info/AlignmentComparator/'>http://bioinfweb.info/AlignmentComparator</a><br>" +
						"ResearchGate: <a href='http://r.bioinfweb.info/RGAlignmentComparator'>http://r.bioinfweb.info/RGAlignmentComparator</a></p>" +
						
						"<p>This program is free software: you can redistribute it and/or modify it " +
						"under the terms of the <i>GNU General Public License</i> as published by the <i>Free Software " +
						"Foundation</i>, either version 3 of the License, or (at your option) any later version.</p>" +
						
						"<p>This program is distributed in the hope that it will be useful, but " +
						"WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY " +
						"or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for " +
						"more details.</p>" +
						
						"<p>You should have received a copy of the <i>GNU General Public License</i> " +
						"along with this program. If not, see " +
						"<a href='http://bioinfweb.info/AlignmentComparator/License'>http://bioinfweb.info/AlignmentComparator/License</a> " +
						"or <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.</p>" +
						
						"<p>This product includes software developed by the <i>Apache Software Foundation</i> " +
						"<a href='http://apache.org/'>http://apache.org/</a> distributed under the terms of the " +
						"<i>Apache License</i> Version 2.0. <i>Google guava-libraries</i> are also distributed under <i>Apache License</i> Version 2.0. " +
						"(<a href='http://www.apache.org/licenses/LICENSE-2.0'>http://www.apache.org/licenses/LICENSE-2.0</a>)</p>" +
						
						"<p><b>The following libraries and software packages are used by AlignmentComparator:</b></p>" +
						"<ul>" +
    				  "<li><i>bioinfweb.commons.java</i> (<a href='http://commons.bioinfweb.info/Java/'>http://commons.bioinfweb.info/Java/</a>)</li>" +
    				  "<li><i>LibrAlign</i> (<a href='http://bioinfweb.info/LibrAlign/'>http://bioinfweb.info/LibrAlign/</a>)</li>" +
    				  "<li><i>JPhyloIO</i> (<a href='http://bioinfweb.info/JPhyloIO/'>http://bioinfweb.info/JPhyloIO/</a>)</li>" +
    				  "<li><i>TIC</i> (<a href='http://bioinfweb.info/TIC/'>http://bioinfweb.info/TIC/</a>)</li>" +
    				  "<li><i>MUSCLE</i> (<a href='http://www.drive5.com/muscle/'>http://www.drive5.com/muscle/</a>)</li>" +
						  "<li><i>Apache commons</i> (<a href='http://commons.apache.org/'>http://commons.apache.org/</a>)</li>" +
						  "<li><i>guava-libraries</i> (<a href='https://github.com/google/guava'>https://github.com/google/guava</a>)</li>" +
						  "<li><i>OWL API</i> (<a href='http://owlcs.github.io/owlapi/'>http://owlcs.github.io/owlapi/</a>)</li>" +
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
