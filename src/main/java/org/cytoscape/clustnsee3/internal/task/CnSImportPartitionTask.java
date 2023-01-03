/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 nov. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.task;

import java.io.File;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSImportPartitionTask extends AbstractTask {
	private Vector<Vector<Long>> imported_partition;
	private Vector<Vector<String>> imported_annotation;
	private CnSAlgorithm algo;
	private CyNetwork network;
	private String scope;
	
	public CnSImportPartitionTask(Vector<Vector<Long>> imported_partition, Vector<Vector<String>> imported_annotation, CnSAlgorithm algo, CyNetwork network, String scope) {
		this.imported_annotation = imported_annotation;
		this.imported_partition = imported_partition;
		this.algo = algo;
		this.network = network;
		this.scope = scope;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Importing partition ...");
		taskMonitor.setProgress(-1.0);
		
		CnSEvent ev = new CnSEvent(CnSResultsPanel.IMPORT_PARTITION, CnSEventManager.RESULTS_PANEL);
		ev.addParameter(CnSResultsPanel.RESULT, imported_partition);
		ev.addParameter(CnSResultsPanel.ANNOTATION, imported_annotation);
		ev.addParameter(CnSResultsPanel.ALGO, algo);
		ev.addParameter(CnSResultsPanel.NETWORK, network);
		ev.addParameter(CnSResultsPanel.SCOPE, scope);
		ev.addParameter(CnSResultsPanel.TASK_MONITOR, taskMonitor);
		
		CnSEventManager.handleMessage(ev);
	}

}
