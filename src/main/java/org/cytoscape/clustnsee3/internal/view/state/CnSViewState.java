/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 15 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.view.state;

/**
 * 
 */
public abstract class CnSViewState {
	private Object reference;
	
	public CnSViewState(Object reference) {
		super();
		this.reference = reference;
	}
	
	public Object getReference() {
		return reference;
	}
	
	public void clearReference() {
		reference = null;
	}
	public boolean equals(Object o) {
		if (reference == null || o == null) return false;
		CnSViewState s = (CnSViewState)o;
		if (s.getReference() == null) return false;
		return s.getReference().equals(reference);
	}
	public boolean isUserView() {
		return false;
	}
	
	public void updateNodeContextMenu() {
		
	}
}
