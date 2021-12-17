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
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;

public class CnSAFTreeNetworkNetnameNode extends CnSPanelTreeNode {
	public final static int NETWORK_NAME = 1;
	public static final int NETWORK = 2;
	public static final int ANNOTATION_FILE = 3;
	
	public CnSAFTreeNetworkNetnameNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeNetworkNetnameNodePanel(getData(NETWORK).toString());
		System.err.println("CONSTRUCTOR - getData(ANNOTATION_FILE) = " + getData(ANNOTATION_FILE));
		((CnSAFTreeNetworkNetnameNodePanel)panel).getDeleteButton().addActionListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof CnSButton) {
			if (((CnSButton)e.getSource()).getActionCommand().equals("remove_network")) {
				System.err.println("ACTIONPERFORMED - getData(ANNOTATION_FILE) = " + getData(ANNOTATION_FILE));
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
				ev.addParameter(CnSNodeAnnotationManager.NETWORK, getData(NETWORK));
				CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CnSControlPanel.REMOVE_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL);
				ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
				CnSEventManager.handleMessage(ev);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(NETWORK);
	}
}
