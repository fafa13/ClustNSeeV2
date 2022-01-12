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

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public abstract class CnSTrieNode {
	protected CnSTrieNode parent;
	protected static StringBuilder sb;
	protected char value;
	
	public CnSTrieNode() {
		value = 0;
		parent = null;
	}
	
	public void setParent(CnSTrieNode parent) {
		this.parent = parent;
	}
	public CnSTrieNode getParent() {
		return parent;
	}
	public Vector<CnSTrieNode> getChildren() {
		return null;
	}
	public CnSTrieNode getChild(char c) {
		return null;
	}
	public char getValue() {
		return value;
	}
	public CnSTrieNode addChild(char c) {
		return null;
	}
	public boolean contains(char c) {
		return false;
	}
	public String getWord() {
		return null;
	}
	public CnSNodeAnnotation getAnnotation() {
		return null;
	}
	public void setAnnotation(CnSNodeAnnotation annotation) {
		
	}
	public void removeChild(CnSTrieNode node) {
		if (getChildren() != null) getChildren().removeElement(node);
	}
	public void removeAnnotation() {
		
	}
}
