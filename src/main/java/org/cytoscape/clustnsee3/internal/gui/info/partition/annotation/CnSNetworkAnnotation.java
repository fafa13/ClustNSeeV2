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

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSNetworkAnnotation<AnnotationType> extends CnSPartitionAnnotation<AnnotationType> {
	private HashMap<String, AnnotationType> data; 
	
	public CnSNetworkAnnotation(CyNetwork network, String name) {
		super(network, name);
	}
	
	public void setData(HashMap<String, AnnotationType> data) {
		this.data = data;
	}
	
	public HashMap<String, AnnotationType> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionAnnotation#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int index) {
		return "NA";
	}
}
