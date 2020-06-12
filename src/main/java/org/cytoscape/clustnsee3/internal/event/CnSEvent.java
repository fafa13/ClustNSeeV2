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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 
 */
public class CnSEvent {	
	/*
	 * 
	 */
	private int target;
	
	/*
	 * 
	 */
	private int action;
	
	/*
	 * 
	 */
	private Hashtable<Integer, Object> eventParameters;
	
	/*
	 * 
	 */
	public CnSEvent() {
		super();
		target = action = 0;
		eventParameters = new Hashtable<Integer, Object>();
	}
	
	public CnSEvent(int action, int target) {
		this();
		this.target = target;
		this.action = action;
	}
	/*
	 * 
	 */
	public Object addParameter(int k, Object value) {
		return eventParameters.put(k, value);
	}
	
	public int getTarget() {
		return target;
	}
	public int getAction() {
		return action;
	}
	public Object getParameter(int k) {
		return eventParameters.get(k);
	}
	public Enumeration<Integer> getParameters() {
		return eventParameters.keys();
	}
}