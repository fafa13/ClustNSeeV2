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

package org.cytoscape.clustnsee3.internal.gui.menu.contextual.action;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;

/**
 * 
 */
public class CnSMenuManager implements CnSEventListener {
	private static CnSMenuManager instance;
	
	private CnSMenuManager() {
		super();
	}
	
	public String getActionName(int k) {
		switch(k) {
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			default : return "UNDEFINED_PARAMETER";
		}
	}

	public static CnSMenuManager getInstance() {
		if (instance == null)
			instance = new CnSMenuManager();
		return instance;
	}
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		if (log) CnSLogger.getInstance().LogCnSEvent(event, this);
		
		return new CnSEventResult<Object>(null);
	}
}
