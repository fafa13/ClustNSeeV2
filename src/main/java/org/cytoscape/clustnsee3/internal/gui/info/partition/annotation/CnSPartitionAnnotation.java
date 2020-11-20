/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 18 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition.annotation;

import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public abstract class CnSPartitionAnnotation<AnnotationType> {
	protected String name;
	protected CyNetwork network;
	
	public CnSPartitionAnnotation(CyNetwork network, String name) {
		super();
		this.name = name;
		this.network = network;
	}
	
	public String getName() {
		return name;
	}
	
	public CyNetwork getNetwork() {
		return network;
	}
	
	public abstract <AnnotationType> AnnotationType getValueAt(int index);
}
