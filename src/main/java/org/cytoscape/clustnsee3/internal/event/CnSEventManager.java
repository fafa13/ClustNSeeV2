/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date Nov 12, 2018
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.event;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSSynchronizeClusterSelection;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;

/**
 * This class is an implementation of the Mediator pattern. It's goal is to organize the 
 * message flow between the main classes of the application.
 */
public class CnSEventManager {
	public static final int CLUSTNSEE_PLUGIN = 1;
	public static final int ANALYSIS_MANAGER = 2;
	public static final int CLUSTNSEE_MENU_MANAGER = 3;
	public static final int INFO_PANEL = 4;
	public static final int ALGORITHM_MANAGER = 5;
	public static final int ALGORITHM_ENGINE = 6;
	public static final int RESULTS_PANEL = 7;
	public static final int CY_ACTIVATOR = 8;
	public static final int VIEW_MANAGER = 9;
	public static final int NETWORK_MANAGER = 10;
	public static final int PARTITION_MANAGER = 11;
	public static final int STYLE_MANAGER = 12;
	public static final int PARTITION_PANEL = 13;
	public static final int CONTROL_PANEL = 14;
	public static final int ANNOTATION_MANAGER = 15;
	public static final int SYNCHRONIZE_CLUSTER_SELECTION = 16;
	
	private static CnSEventListener plugin;
	private static CnSEventListener analysisManager;
	private static CnSEventListener clustnseeMenuManager;
	private static CnSEventListener dataPanel;
	private static CnSEventListener resultsPanel;
	private static CnSEventListener algorithmManager;
	private static CnSEventListener algorithmEngine;
	private static CnSEventListener cyActivator;
	private static CnSEventListener viewManager;
	private static CnSEventListener networkManager;
	private static CnSEventListener partitionManager;
	private static CnSEventListener styleManager;
	private static CnSEventListener partitionPanel;
	private static CnSEventListener controlPanel;
	private static CnSEventListener nodeAnnotationManager;
	private static CnSSynchronizeClusterSelection synchronizeClusterSelection;
	private static CnSEventManager instance;

	private CnSEventManager() {
		super();
	}
	
	public static CnSEventManager getCnsEventManager(CnSEventListener _plugin, 
			CnSEventListener _analysisManager, CnSEventListener _clustnseeMenuManager,
			CnSEventListener _dataPanel, CnSEventListener _resultsPanel, CnSEventListener _algorithmManager, 
			CnSEventListener _algorithmEngine, CnSEventListener _viewManager, CnSNetworkManager _networkManager, 
			CnSPartitionManager _partitionManager, CnSStyleManager _styleManager, CnSPartitionPanel _partitionPanel, 
			CnSEventListener _nodeAnnotationManager, CnSSynchronizeClusterSelection _synchronizeClusterSelection, CyActivator ca) {
		if (instance == null) {
			instance = new CnSEventManager();
			plugin = _plugin;
			analysisManager = _analysisManager;
			clustnseeMenuManager = _clustnseeMenuManager;
			dataPanel = _dataPanel;
			resultsPanel = _resultsPanel;
			algorithmManager = _algorithmManager;
			algorithmEngine = _algorithmEngine;
			viewManager = _viewManager;
			networkManager = _networkManager;
			partitionManager = _partitionManager;
			styleManager = _styleManager;
			partitionPanel = _partitionPanel;
			nodeAnnotationManager = _nodeAnnotationManager;
			synchronizeClusterSelection = _synchronizeClusterSelection;
			cyActivator = ca;
		}
		return instance;
	}
	public static void addControlPanel(CnSEventListener _controlPanel) {
		controlPanel = _controlPanel;
	}
	public static Object handleMessage(CnSEvent event, boolean log) {
	//public static CnSEventResult<? extends Object> handleMessage(CnSEvent event) {
	    int target = event.getTarget();
	    //CnSEventResult<? extends Object> ret = null;
	    Object ret = null;
	    
	    switch (target) {
	      	case CLUSTNSEE_PLUGIN:
	      		if (plugin != null) ret = plugin.cnsEventOccured(event, log);
	      		break;
	      		
	      	case ANALYSIS_MANAGER:
	      		if (analysisManager != null) ret = analysisManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case CLUSTNSEE_MENU_MANAGER:
	      		if (clustnseeMenuManager != null) ret = clustnseeMenuManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case INFO_PANEL:
	      		if (dataPanel != null) ret = dataPanel.cnsEventOccured(event, log);
	      		break;
	      		
	      	case ALGORITHM_MANAGER:
	      		if (algorithmManager != null) ret = algorithmManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case ALGORITHM_ENGINE:
	      		if (algorithmEngine != null) ret = algorithmEngine.cnsEventOccured(event, log);
	      		break;
	      		
	      	case RESULTS_PANEL:
	      		if (resultsPanel != null) ret = resultsPanel.cnsEventOccured(event, log);
	      		break;
	      		
	      	case VIEW_MANAGER:
	    	  	if (viewManager != null) ret = viewManager.cnsEventOccured(event, log);
	    	  	break;
	    	  	
	      	case NETWORK_MANAGER:
	      		if (networkManager != null) ret = networkManager.cnsEventOccured(event, log);
	    	  	break;
	    	  
	      	case PARTITION_MANAGER:
	      		if (partitionManager != null) ret = partitionManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case STYLE_MANAGER:
	      		if (styleManager != null) ret = styleManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case PARTITION_PANEL:
	      		if (partitionPanel != null) ret = partitionPanel.cnsEventOccured(event, log);
	      		break;
	      		
	      	case ANNOTATION_MANAGER:
	      		if (nodeAnnotationManager != null) ret = nodeAnnotationManager.cnsEventOccured(event, log);
	      		break;
	      		
	      	case CY_ACTIVATOR:
	      		if (cyActivator != null) ret = cyActivator.cnsEventOccured(event, log);
	      		break;
	    	
	      	case CONTROL_PANEL:
	      		if (controlPanel != null) ret = controlPanel.cnsEventOccured(event, log);
	      		break;
	      	  
	      	case SYNCHRONIZE_CLUSTER_SELECTION:
	      		if (synchronizeClusterSelection != null) ret = synchronizeClusterSelection.cnsEventOccured(event, log);
	      		break;
	    }
	    return ret;
	}
}
