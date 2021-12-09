/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAFTreeNetworkNetnameNode extends CnSPanelTreeNode {
	public final static int ANNOTATION_FILE = 1;
	public static final int NETWORK = 2;
	
	private CyNetwork network;
	private CnSNodeAnnotationFile annotationFile;
	
	public CnSAFTreeNetworkNetnameNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		network = (CyNetwork)v.get(NETWORK);
		annotationFile = (CnSNodeAnnotationFile)v.get(ANNOTATION_FILE);
		panel = new CnSAFTreeNetworkNetnameNodePanel(network.toString());
		panel.initGraphics();
		((CnSAFTreeNetworkNetnameNodePanel)panel).getDeleteButton().addActionListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof CnSButton) {
			if (((CnSButton)e.getSource()).getActionCommand().equals("remove_network")) {
				System.err.println("Removing network : " + network.toString() + " from " + annotationFile.toString());
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, annotationFile);
				CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CnSControlPanel.REMOVE_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL);
				ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
				ev.addParameter(CnSControlPanel.NETWORK, network);
				ev.addParameter(CnSControlPanel.ANNOTATION_FILE, annotationFile);
				CnSEventManager.handleMessage(ev);
			}
		}
	}
}
