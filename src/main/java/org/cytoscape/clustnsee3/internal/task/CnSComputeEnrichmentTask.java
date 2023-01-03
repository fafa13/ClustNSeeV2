/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 août 2022
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
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSComputeEnrichmentTask extends AbstractTask {
	private CnSPartition partition;
	
	public CnSComputeEnrichmentTask(CnSPartition partition) {
		this.partition = partition;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.COMPUTE_ENRICHMENT, CnSEventManager.ANNOTATION_MANAGER);
		ev.addParameter(CnSNodeAnnotationManager.PARTITION, partition);
		ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
		CnSEventManager.handleMessage(ev);
	}

}
