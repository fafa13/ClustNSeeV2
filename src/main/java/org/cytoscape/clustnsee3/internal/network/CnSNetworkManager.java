/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.network;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSNetworkManager implements CnSEventListener {
	public static final int ADD_NETWORK = 1;
	
	public static final int NETWORK = 1000;
	
	private Vector<CnSNetwork> networks;
	
	private static CnSNetworkManager instance = null;
	
	/**
	 * @param
	 * @return
	 */
	private CnSNetworkManager() {
		super();
		networks = new Vector<CnSNetwork>();
	}
	
	public static CnSNetworkManager getInstance() {
		if (instance == null) {
			instance = new CnSNetworkManager();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		
		switch (event.getAction()) {
			case ADD_NETWORK :
				CnSNetwork network = (CnSNetwork)event.getParameter(NETWORK);
				if (!networks.contains(network)) networks.addElement(network);
				break;
		}
		return ret;
	}

}
