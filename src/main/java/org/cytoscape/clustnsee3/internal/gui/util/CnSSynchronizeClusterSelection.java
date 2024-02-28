/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 nov. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;

/**
 * 
 */
public class CnSSynchronizeClusterSelection implements CnSEventListener {
	public static final int SYNCHRONIZE = 1;
	
	public static final int PARTITION = 1001;
	public static final int CLUSTER = 1002;
	
	public CnSSynchronizeClusterSelection() {
		super();
	}
	
	public String getActionName(int k) {
		switch(k) {
			case SYNCHRONIZE : return "SYNCHRONIZE";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case PARTITION : return "PARTITION";
			case CLUSTER : return "CLUSTER";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
		final CnSCluster cluster;
		final CnSPartition partition;
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
		switch (event.getAction()) {
			case SYNCHRONIZE :
				partition = (CnSPartition)event.getParameter(PARTITION);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				
				CnSEvent e = new CnSEvent(CnSViewManager.SELECT_CLUSTER, CnSEventManager.VIEW_MANAGER, this.getClass());
				e.addParameter(CnSViewManager.CLUSTER, cluster);
				CnSEventManager.handleMessage(e, true);
				
				e = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL, this.getClass());
				e.addParameter(CnSResultsPanel.CLUSTER, cluster.getCyNode().getSUID());
				e.addParameter(CnSResultsPanel.PARTITION, partition);
				CnSEventManager.handleMessage(e, true);
				
				e = new CnSEvent(CnSPartitionPanel.SELECT_CLUSTER, CnSEventManager.PARTITION_PANEL, this.getClass());
				e.addParameter(CnSResultsPanel.CLUSTER, cluster);
				e.addParameter(CnSResultsPanel.PARTITION, partition);
				CnSEventManager.handleMessage(e, true);
				break;
		}
		return ret;
	}
}
