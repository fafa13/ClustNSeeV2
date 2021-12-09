/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.dialog.CnSAnnotationFileStatsDialog;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAFTreeFileNode extends CnSPanelTreeNode {
	public final static int ANNOTATION_FILE = 1;
	public final static int NB_ANNOTATIONS = 2;
	public final static int NB_NODES = 3;
	
	public CnSAFTreeFileNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeFileNodePanel((CnSNodeAnnotationFile)v.get(ANNOTATION_FILE));
		panel.initGraphics();
		((CnSAFTreeFileNodePanel)panel).getDeleteButton().addActionListener(this);
		((CnSAFTreeFileNodePanel)panel).getAnnotateButton().addActionListener(this);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		CnSNodeAnnotationFile af = null;
		if (e.getSource() instanceof CnSButton) {
			af = (CnSNodeAnnotationFile)getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			if (((CnSButton)e.getSource()).getActionCommand().equals("delete")) {
				System.out.println("Pressed: delete " + af);
			}
			else if (((CnSButton)e.getSource()).getActionCommand().equals("annotate")) {
				System.out.println("Pressed: annotate " + af);
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				CyNetwork network = cam.getCurrentNetwork();
				
				if (network != null) {
					ev = new CnSEvent(CnSNodeAnnotationManager.PARSE_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
					ev.addParameter(CnSNodeAnnotationManager.FILE, af.getFile());
					ev.addParameter(CnSNodeAnnotationManager.FROM_LINE, af.getFromLine());
					ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
					int[] results = (int[])CnSEventManager.handleMessage(ev);
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				
					CnSAnnotationFileStatsDialog statsDialog = new CnSAnnotationFileStatsDialog(results[0], results[1], results[2]);
					statsDialog.setLocation((screenSize.width - statsDialog.getWidth()) / 2, (screenSize.height - statsDialog.getHeight()) / 2);
					statsDialog.setVisible(true);
					if (statsDialog.getExitOption() == CnSAnnotationFileStatsDialog.OK_OPTION) {
						ev = new CnSEvent(CnSNodeAnnotationManager.ANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
						ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
						ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
						CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSControlPanel.ADD_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL);
						ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
						ev.addParameter(CnSControlPanel.NETWORK, network);
						ev.addParameter(CnSControlPanel.ANNOTATION_FILE, getData(ANNOTATION_FILE));
						ev.addParameter(CnSControlPanel.MAPPED_NODES, results[2]);
						ev.addParameter(CnSControlPanel.MAPPED_ANNOTATIONS, results[3]);
						ev.addParameter(CnSControlPanel.NETWORK_NODES, results[4]);
						ev.addParameter(CnSControlPanel.FILE_ANNOTATIONS, results[5]);
						CnSEventManager.handleMessage(ev);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "There is no network to map annotations !");
			}
		}
	}
}
