/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 14 août 2020
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
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSDiscardPartitionTask extends AbstractTask {

	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Discarding partition ...");
		taskMonitor.setProgress(0.0);
		
		// get the selected partition
		CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true);
		
		if (partition != null) {
			taskMonitor.setStatusMessage("Removing cluster networks from Cytoscape.");
		
			// remove it from the results panel
			ev = new CnSEvent(CnSResultsPanel.DISCARD_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
			ev.addParameter(CnSResultsPanel.PARTITION, partition);
			ev.addParameter(CnSResultsPanel.TASK_MONITOR, taskMonitor);
			CnSEventManager.handleMessage(ev, true);
		
			// remove it from the partition controller
			ev = new CnSEvent(CnSPartitionManager.REMOVE_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
			ev.addParameter(CnSPartitionManager.PARTITION, partition);
			CnSEventManager.handleMessage(ev, true);
		
			taskMonitor.setStatusMessage("Removing annotation enrichment.");
		
			// remove annotation enrichment of the partition
			ev = new CnSEvent(CnSNodeAnnotationManager.REMOVE_ENRICHMENT, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSPartitionManager.PARTITION, partition);
			CnSEventManager.handleMessage(ev, true);
		}
		taskMonitor.setProgress(1.0);
	}
}
