/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 27 sept. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.task;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSDeleteAnnotationFileTask extends AbstractTask {
	private CnSNodeAnnotationFile annotationFile;
	private CnSPanelTreeNode networkRootNode;
	private CnSAFTreeFileNode treeFileNode;
	/**
	 * @param cnSAFTreeFileNode 
	 * @param
	 * @return
	 */
	public CnSDeleteAnnotationFileTask(CnSNodeAnnotationFile af, CnSPanelTreeNode rootNode, CnSAFTreeFileNode cnSAFTreeFileNode) {
		annotationFile = af;
		networkRootNode = rootNode;
		treeFileNode = cnSAFTreeFileNode;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Deleting annotation file " + annotationFile.toString() + " ...");
		taskMonitor.setProgress(-1.0);
		
		for (int k = 0;  k < networkRootNode.getChildCount(); k++) {
			CyNetwork nw = (CyNetwork)networkRootNode.getChildAt(k).getData(CnSAFTreeNetworkNetnameNode.NETWORK);
			CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, annotationFile);
			ev.addParameter(CnSNodeAnnotationManager.NETWORK, nw);
			CnSEventManager.handleMessage(ev, true);
				
			System.err.println("------ " + nw);
		}
		
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.UNLOAD_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, annotationFile);
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSNodeAnnotationManager.REFRESH_CLUSTER_HASMAP, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL, this.getClass());
		CnSEventManager.handleMessage(ev, true);
	
		ev = new CnSEvent(CnSControlPanel.REMOVE_ANNOTATION_FILE, CnSEventManager.CONTROL_PANEL, this.getClass());
		ev.addParameter(CnSControlPanel.TREE_FILE_NODE, treeFileNode);
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL, this.getClass());
		CnSEventManager.handleMessage(ev, true);
		System.err.println("OK");
	}

}
