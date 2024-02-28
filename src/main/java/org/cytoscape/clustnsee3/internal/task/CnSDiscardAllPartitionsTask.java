/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 4 janv. 2024
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
import java.util.Vector;
/**
 * 
 */
public class CnSDiscardAllPartitionsTask extends AbstractTask {

	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Discarding all partitions ...");
		taskMonitor.setProgress(0.0);
		
		// get the selected partition
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITIONS, CnSEventManager.PARTITION_MANAGER, this.getClass());
		Vector<CnSPartition> partitions = (Vector<CnSPartition>)CnSEventManager.handleMessage(ev, true).getValue();
		
		if (partitions != null) {
			int s = partitions.size();
			CnSPartition[] o = new CnSPartition[s];
			partitions.copyInto(o);
			for (int i = 0; i < s; i++) {
				taskMonitor.setStatusMessage("Removing cluster networks from Cytoscape.");
		
				// remove it from the results panel
				ev = new CnSEvent(CnSResultsPanel.DISCARD_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
				ev.addParameter(CnSResultsPanel.PARTITION, o[i]);
				ev.addParameter(CnSResultsPanel.TASK_MONITOR, taskMonitor);
				CnSEventManager.handleMessage(ev, true);
		
				// remove it from the partition controller
				ev = new CnSEvent(CnSPartitionManager.REMOVE_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
				ev.addParameter(CnSPartitionManager.PARTITION, o[i]);
				CnSEventManager.handleMessage(ev, true);
		
				taskMonitor.setStatusMessage("Removing annotation enrichment.");
		
				// remove annotation enrichment of the partition
				ev = new CnSEvent(CnSNodeAnnotationManager.REMOVE_ENRICHMENT, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
				ev.addParameter(CnSPartitionManager.PARTITION, o[i]);
				CnSEventManager.handleMessage(ev, true);
			}
		}
		taskMonitor.setProgress(1.0);
	}

}
