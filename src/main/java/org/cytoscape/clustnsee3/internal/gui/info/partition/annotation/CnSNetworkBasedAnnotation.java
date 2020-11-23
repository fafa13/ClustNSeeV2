/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition.annotation;

import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSNetworkBasedAnnotation extends CnSPartitionAnnotation {
	private CnSNetworkAnnotation<?> networkAnnotation;
	
	/**
	 * @param
	 * @return
	 */
	public CnSNetworkBasedAnnotation(CnSPartition partition, CnSNetworkAnnotation<?> networkAnnotation) {
		super(partition, networkAnnotation.getName());
		this.networkAnnotation = networkAnnotation;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionAnnotation#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int index) {
		return "NA";
	}

}
