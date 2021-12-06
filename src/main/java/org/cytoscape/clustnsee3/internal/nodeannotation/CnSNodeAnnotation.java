/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 déc. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.nodeannotation.trie.CnSTrieNode;

/**
 * 
 */
public class CnSNodeAnnotation implements Comparable {
	private CnSTrieNode trieNode;
	private Vector<CnSAnnotationTarget> targets;
	
	public CnSNodeAnnotation(CnSTrieNode trieNode) {
		super();
		this.trieNode = trieNode;
		targets = new Vector<CnSAnnotationTarget>();
	}
	public void addTarget(String node, CnSNodeAnnotationFile file) {
		targets.addElement(new CnSAnnotationTarget(node, file));
	}
	public String getValue() {
		return trieNode.getWord();
	}
	public Vector<CnSAnnotationTarget> getTargets() {
		return targets;
	}
	public boolean equals(Object toCompare) {
		String value = getValue();
		String toCompareValue = ((CnSNodeAnnotation)toCompare).getValue();
		if (value != null && toCompareValue != null)
			return value.equals(toCompareValue);
		return false;
	}
	
	public int hashCode() {
		return trieNode.hashCode();
	}
	public String toString() {
		return getValue();
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		CnSNodeAnnotation na = (CnSNodeAnnotation)o;
		return na.getValue().compareTo(this.getValue());
	}
}
