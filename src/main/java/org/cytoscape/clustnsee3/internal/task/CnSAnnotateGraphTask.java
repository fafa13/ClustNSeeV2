/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 5 août 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.task;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSAnnotateGraphTask extends AbstractTask {
	private CnSNodeAnnotationFile af;
	private CyNetwork network;
	private int nbAnnotations, mappedNodes, mappedAnnotations, networkNodes, fileAnnotations;
	private TaskMonitor taskMonitor;
	private CnSAFTreeFileNode treeFileNode;
	
	/**
	 * @param treeFileNode 
	 * @param
	 * @return
	 */
	public CnSAnnotateGraphTask(CnSNodeAnnotationFile af, CyNetwork network, int[] annotationsInfo, CnSAFTreeFileNode treeFileNode) {
		this.af = af;
		this.network = network;
		nbAnnotations = annotationsInfo[1];
		mappedNodes = annotationsInfo[2];
		mappedAnnotations = annotationsInfo[3];
		networkNodes = annotationsInfo[4];
		fileAnnotations = annotationsInfo[5];
		this.treeFileNode = treeFileNode;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		taskMonitor.setTitle("Graph annotation is running ...");
		taskMonitor.setProgress(-1.0);
		
		taskMonitor.setTitle("Annotating network " + network.toString() + " ...");
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.ANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, af);
		ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
		ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSPartitionManager.GET_PARTITIONS, CnSEventManager.PARTITION_MANAGER, this.getClass());
		Vector<CnSPartition> parts = (Vector<CnSPartition>)CnSEventManager.handleMessage(ev, true);
		
		for (CnSPartition p : parts) {
			taskMonitor.setTitle("Computing enrichment for partition " + p.getName() + " ...");
			ev = new CnSEvent(CnSNodeAnnotationManager.COMPUTE_ENRICHMENT, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.PARTITION, p);
			ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
			CnSEventManager.handleMessage(ev, true);
		}
		ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
		CnSPartition selectedPartition = (CnSPartition)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL, this.getClass());
		if (selectedPartition != null) ev.addParameter(CnSPartitionPanel.PARTITION, selectedPartition);
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSControlPanel.ADD_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL, this.getClass());
		ev.addParameter(CnSControlPanel.TREE_FILE_NODE, treeFileNode);
		ev.addParameter(CnSControlPanel.NETWORK, network);
		ev.addParameter(CnSControlPanel.MAPPED_NODES, mappedNodes);
		ev.addParameter(CnSControlPanel.MAPPED_ANNOTATIONS, mappedAnnotations);
		ev.addParameter(CnSControlPanel.NETWORK_NODES, networkNodes);
		ev.addParameter(CnSControlPanel.FILE_ANNOTATIONS, fileAnnotations);
		ev.addParameter(CnSControlPanel.ANNOTATION_FILE, af);
		CnSEventManager.handleMessage(ev, true);
		
		((CnSAFTreeDetailsNodePanel)treeFileNode.getChildAt(0).getPanel()).getNetworksTree().expandRow(0);
		ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL, this.getClass());
		CnSEventManager.handleMessage(ev, true);
	}
	
	public void setAnnotationIndex(int index) {
		taskMonitor.setProgress((double)index/(double)nbAnnotations);
	}
	public void setProgress(double progress) {
		taskMonitor.setProgress(progress);
	}
}
