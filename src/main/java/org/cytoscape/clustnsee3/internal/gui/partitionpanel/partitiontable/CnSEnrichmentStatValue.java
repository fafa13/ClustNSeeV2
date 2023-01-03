/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 25 juil. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 
 */
public class CnSEnrichmentStatValue implements Comparable<CnSEnrichmentStatValue> {
	private double bhValue;
	private double pc;
	private int nbNodes;
	
	public CnSEnrichmentStatValue(int nbNodes, double bhValue, double pc) {
		this.bhValue = bhValue;
		this.pc = pc;
		this.nbNodes = nbNodes;
	}

	public double getBhValue() {
		return bhValue;
	}
	
	public double getPc() {
		return pc;
	}
	
	public int getNbNodes() {
		return nbNodes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CnSEnrichmentStatValue o) {
		if (bhValue > o.getBhValue())
			return 1;
		else if (bhValue < o.getBhValue())
			return -1;
		return 0;
	}
	
	public String toString() {
		NumberFormat format = new DecimalFormat("##.00%");
		return getNbNodes() + " (" + format.format(getPc()) + ") ; " + String.format("%G", getBhValue());
		//return "*-*- " + pc + " -> " + bhValue;
	}
}
