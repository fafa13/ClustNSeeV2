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

import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public abstract class CnSPartitionAnnotation {
	protected String name;
	protected CnSPartition partition;
	
	public CnSPartitionAnnotation(CnSPartition partition, String name) {
		super();
		this.name = name;
		this.partition = partition;
	}
	
	public String getName() {
		return name;
	}
	
	public CyNetwork getNetwork() {
		return partition.getInputNetwork();
	}
	
	public abstract Object getValueAt(int index);
}

