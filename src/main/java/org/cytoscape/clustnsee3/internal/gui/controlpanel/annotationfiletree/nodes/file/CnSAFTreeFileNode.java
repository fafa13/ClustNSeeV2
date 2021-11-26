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

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAFTreeFileNode extends CnSPanelTreeNode {
	public final static int ANNOTATION_FILE = 1;
	public final static int NB_ANNOTATIONS = 2;
	public final static int NB_NODES = 3;
	public final static int NETWORKS = 4;
	
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
		String val;
		if (e.getSource() instanceof CnSButton)
			if (((CnSButton)e.getSource()).getActionCommand().equals("delete")) {
				val = getData(CnSAFTreeFileNode.ANNOTATION_FILE).toString();
				System.out.println("Pressed: delete " + val);
			}
			else if (((CnSButton)e.getSource()).getActionCommand().equals("annotate")) {
				val = getData(CnSAFTreeFileNode.ANNOTATION_FILE).toString();
				System.out.println("Pressed: annotate " + val);
				CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				CyNetwork network = cam.getCurrentNetwork();
				ev = new CnSEvent(CnSNetworkManager.GET_NETWORK, CnSEventManager.NETWORK_MANAGER);
				ev.addParameter(CnSNetworkManager.NETWORK, network);
				CnSNetwork cn = (CnSNetwork)CnSEventManager.handleMessage(ev);
				if (cn != null) {
					ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.NETWORK, cn);
					CnSPartition p = (CnSPartition)CnSEventManager.handleMessage(ev);
					//network = p.getInputNetwork();
				}
				ev = new CnSEvent(CnSNodeAnnotationManager.ANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
				ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
				CnSEventManager.handleMessage(ev);
			}
	}
}
