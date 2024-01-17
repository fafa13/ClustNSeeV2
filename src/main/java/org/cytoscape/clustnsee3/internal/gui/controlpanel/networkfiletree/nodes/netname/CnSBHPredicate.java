/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 19 juil. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.util.function.Predicate;

import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;

/**
 * 
 */
public class CnSBHPredicate implements Predicate<CnSAnnotationClusterPValue> {
	private double threshold;
	
	public CnSBHPredicate(double threshold) {
		super();
		this.threshold = threshold;
	}
	
	/* (non-Javadoc)
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(CnSAnnotationClusterPValue pv) {
		return (pv.getBHValue() >= threshold);
	}
}
