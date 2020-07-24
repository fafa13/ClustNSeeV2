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

package org.cytoscape.clustnsee3.internal.algorithm;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAlgorithmEngine implements CnSEventListener {
	public static final int START = 1;
	public static final int SET_SCOPE = 2;
	public static final int GET_SCOPE = 3;
	public static final int IS_CANCELLED = 4;
	public static final int SET_CANCELLED = 5;
	
	public static final int ALGORITHM = 1000;
	public static final int PARAMETERS = 1001;
	public static final int SCOPE = 1002;
	public static final int CANCELLED = 1003;
	
	private static CnSAlgorithmEngine instance;
	private String scope;
	private boolean cancelled;
	
	private CnSAlgorithmEngine() {
		super();
		scope = "Network";
		cancelled = false;
	}
	
	public static CnSAlgorithmEngine getInstance() {
		if (instance == null)
			instance = new CnSAlgorithmEngine();
		return instance;
	}
	
	public CnSAlgorithmResult start(CnSAlgorithm algo) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
		CyNetwork network = cam.getCurrentNetwork();
		if (network != null) return algo.execute(network);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		switch(event.getAction()) {
			case START :
				ret = start((CnSAlgorithm)event.getParameter(ALGORITHM));
				break;
			case SET_SCOPE :
				scope = (String)event.getParameter(SCOPE);
				break;
			case GET_SCOPE :
				ret = scope;
				break;
			case IS_CANCELLED :
				ret = Boolean.valueOf(cancelled);
				break;
			case SET_CANCELLED :
				cancelled = (Boolean)event.getParameter(CANCELLED);
				break;
		}
		return ret;
	}
}