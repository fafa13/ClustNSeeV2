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

/**
 * This class is an implementation of the Mediator pattern. It's goal is to organize the 
 * message flow between the main classes of the application.
 */
public class CnSEventManager {
	public static final int CLUSTNSEE_PLUGIN = 1;
	public static final int ANALYSIS_MANAGER = 2;
	public static final int CLUSTNSEE_MENU_MANAGER = 3;
	public static final int DATA_PANEL = 4;
	public static final int ALGORITHM_MANAGER = 5;
	public static final int ALGORITHM_ENGINE = 6;
	public static final int RESULTS_PANEL = 7;
	public static final int CY_ACTIVATOR = 8;
	public static final int VIEW_MANAGER = 9;
	
	private static CnSEventListener plugin;
	private static CnSEventListener analysisManager;
	private static CnSEventListener clustnseeMenuManager;
	private static CnSEventListener dataPanel;
	private static CnSEventListener resultsPanel;
	private static CnSEventListener algorithmManager;
	private static CnSEventListener algorithmEngine;
	private static CnSEventListener cyActivator;
	private static CnSEventListener viewManager;
	private static CnSEventManager instance;

	private CnSEventManager() {
		super();
	}
	
	public static CnSEventManager getCnsEventManager(CnSEventListener _plugin, 
			CnSEventListener _analysisManager, CnSEventListener _clustnseeMenuManager,
			CnSEventListener _dataPanel, CnSEventListener _resultsPanel, CnSEventListener _algorithmManager, 
			CnSEventListener _algorithmEngine, CnSEventListener _viewManager, CyActivator ca) {
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
			cyActivator = ca;
		}
		return instance;
	}
	
	public static Object handleMessage(CnSEvent event) {
	    int target = event.getTarget();
	    Object ret = null;
	    switch (target) {
	      	case CLUSTNSEE_PLUGIN:
	      		if (plugin != null) ret = plugin.cnsEventOccured(event);
	      		break;
	      		
	      	case ANALYSIS_MANAGER:
	      		if (analysisManager != null) ret = analysisManager.cnsEventOccured(event);
	      		break;
	      		
	      	case CLUSTNSEE_MENU_MANAGER:
	      		if (clustnseeMenuManager != null) ret = clustnseeMenuManager.cnsEventOccured(event);
	      		break;
	      		
	      	case DATA_PANEL:
	      		if (dataPanel != null) ret = dataPanel.cnsEventOccured(event);
	      		break;
	      		
	      	case ALGORITHM_MANAGER:
	      		if (algorithmManager != null) ret = algorithmManager.cnsEventOccured(event);
	      		break;
	      		
	      	case ALGORITHM_ENGINE:
	      		if (algorithmEngine != null) ret = algorithmEngine.cnsEventOccured(event);
	      		break;
	      		
	      	case RESULTS_PANEL:
	      		if (resultsPanel != null) ret = resultsPanel.cnsEventOccured(event);
	      		break;
	      		
	      case VIEW_MANAGER:
	    	  	if (viewManager != null) ret = viewManager.cnsEventOccured(event);
	    	  	break;
	    	  	
	      case CY_ACTIVATOR:
	    	  if (cyActivator != null) ret = cyActivator.cnsEventOccured(event);
	    	  break;
	    }
	    return ret;
	}
}
