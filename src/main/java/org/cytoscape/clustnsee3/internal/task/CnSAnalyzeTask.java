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

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSAnalyzeTask extends AbstractTask {
	private CyNetwork inputNetwork;
	/**
	 * @param
	 * @return
	 */
	public CnSAnalyzeTask(CyNetwork inputNetwork) {
		this.inputNetwork = inputNetwork;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Analysis is running ...");
		taskMonitor.setProgress(-1.0);
		
		/**
		 * Get the name of the selected algorithm
		 */
		CnSEvent ev = new CnSEvent(CnSAlgorithmManager.GET_SELECTED_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER, this.getClass());
		String algoName = (String)CnSEventManager.handleMessage(ev, true).getValue();
		
		/**
		 * Get the selected algorithm
		 */
		ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER, this.getClass());
		ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algoName);
		CnSAlgorithm algo = (CnSAlgorithm)CnSEventManager.handleMessage(ev, true).getValue();
		
		taskMonitor.setStatusMessage("Cluster computation");
		/**
		 * Run the algorithm
		 */
		ev = new CnSEvent(CnSAlgorithmEngine.START, CnSEventManager.ALGORITHM_ENGINE, this.getClass());
		ev.addParameter(CnSAlgorithmEngine.ALGORITHM, algo);
		ev.addParameter(CnSAlgorithmEngine.NETWORK, inputNetwork);
		CnSAlgorithmResult result = (CnSAlgorithmResult)CnSEventManager.handleMessage(ev, true).getValue();
		
		taskMonitor.setProgress(0.0);
		
		taskMonitor.setStatusMessage("Cluster analysis");
		
		/**
		 * initialize result panel
		 */
		if (result != null) {
			ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
			CyNetwork network = cam.getCurrentNetwork();
		
			ev = new CnSEvent(CnSResultsPanel.ADD_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
			ev.addParameter(CnSResultsPanel.RESULT, result);
			ev.addParameter(CnSResultsPanel.ALGO, algo);
			ev.addParameter(CnSResultsPanel.NETWORK, network);
			ev.addParameter(CnSResultsPanel.TASK_MONITOR, taskMonitor);
			CnSEventManager.handleMessage(ev, true);
		}
		
		taskMonitor.setProgress(1.0);
	}
}
