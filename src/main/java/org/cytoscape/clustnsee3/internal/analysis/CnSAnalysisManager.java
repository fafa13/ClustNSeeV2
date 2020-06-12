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

package org.cytoscape.clustnsee3.internal.analysis;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;

/**
 * 
 */
public class CnSAnalysisManager implements CnSEventListener {
	/**
	 * @param
	 * @return
	 */
	
	private static CnSAnalysisManager instance;
	
	private CnSAnalysisManager() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public static CnSAnalysisManager getInstance() {
		if (instance == null)
			instance = new CnSAnalysisManager();
		return instance;
	}

}


