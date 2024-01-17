/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 août 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.task;

import java.io.File;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSLoadAnnotationFileTask extends AbstractTask {
	private File selectedFile;
	private int fromLine, annotationsColumn, targetColumn;
	private CnSPartition partition;
	private CnSAFTreeRootNode treeRootNode;
	private char columnSeparator, annotationSeparator;
	
	/**
	 * @param treeRootNode 
	 * @param
	 * @return
	 */
	public CnSLoadAnnotationFileTask(File selectedFile, int fromLine, int annotationsColumn, int targetColumn, char columnSeparator, char annotationSeparator, CnSPartition partition, CnSAFTreeRootNode treeRootNode) {
		this.selectedFile = selectedFile;
		this.fromLine = fromLine;
		this.partition = partition;
		this.treeRootNode = treeRootNode;
		this.annotationsColumn = annotationsColumn;
		this.targetColumn = targetColumn;
		this.columnSeparator = columnSeparator;
		this.annotationSeparator = annotationSeparator;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Loading annotation file " + selectedFile.getName() + " ...");
		taskMonitor.setProgress(0.0);
		
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.LOAD_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		ev.addParameter(CnSNodeAnnotationManager.FILE, selectedFile);
		ev.addParameter(CnSNodeAnnotationManager.FROM_LINE, fromLine);
		ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
		ev.addParameter(CnSNodeAnnotationManager.ANNOTATIONS_COLUMN, annotationsColumn);
		ev.addParameter(CnSNodeAnnotationManager.TARGET_COLUMN, targetColumn);
		ev.addParameter(CnSNodeAnnotationManager.COLUMN_SEPARATOR, columnSeparator);
		ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_SEPARATOR, annotationSeparator);
		CnSNodeAnnotationFile annotationFile = (CnSNodeAnnotationFile)CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL, this.getClass());
		if (partition != null) ev.addParameter(CnSPartitionPanel.PARTITION, partition);
		CnSEventManager.handleMessage(ev, true);
		treeRootNode.getTreeModel().addAnnotationFile(treeRootNode, annotationFile, annotationFile.getAllAnnotations().size(), annotationFile.getAllTargets().size());
		
		ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL, this.getClass());
		CnSEventManager.handleMessage(ev, true);
		
		ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL, this.getClass());
		CnSEventManager.handleMessage(ev, true);
	}
}
