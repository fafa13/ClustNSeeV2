/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 18 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis.annotation;

/**
 * 
 */
public class CnSClusterAnnotation {
	private String annotation;
	
	public CnSClusterAnnotation(String annotation) {
		super();
		this.annotation = annotation;
	}
	
	public String getAnnotation() {
		return annotation;
	}
	
	public boolean equals(Object o) {
		CnSClusterAnnotation cca = (CnSClusterAnnotation)o;
		return cca.getAnnotation().equals(annotation);
	}
}
