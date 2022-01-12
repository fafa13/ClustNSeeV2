/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 28 janv. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation.trie;

import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSAnnotationTrieNode extends CnSTrieNode {
	private CnSNodeAnnotation annotation;
	
	public CnSAnnotationTrieNode() {
		super();
	}
	
	public void setAnnotation(CnSNodeAnnotation annotation) {
		this.annotation = annotation;
	}
	public CnSNodeAnnotation getAnnotation() {
		return annotation;
	}
	public String getWord() {
		if (parent != null)
			return parent.getWord();
		return  null;
	}
	public void removeAnnotation() {
		this.annotation = null;
	}
}
