/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 janv. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation.trie;

import java.util.Vector;

/**
 * 
 */
public class CnSCharTrieNode extends CnSTrieNode {
	private Vector<CnSTrieNode> children;
	private StringBuilder sb;
	
	public CnSCharTrieNode(char c) {
		super();
		value = c;
		children = new Vector<CnSTrieNode>();
		parent = null;
	}
	
	public char getValue() {
		return value;
	}
	
	public String getWord() {
		sb = new StringBuilder();
		retrieveWord(this);
		return sb.reverse().toString();
	}
	
	private void retrieveWord(CnSTrieNode node) {
		if (node.parent != null) {
			sb.append(node.value);
			retrieveWord(node.parent);
		}
	}
	
	public CnSTrieNode getChild(char c) {
		CnSTrieNode ret = null;
		if (getChildren() != null)
			for (CnSTrieNode n : getChildren())
				if (n.getValue() == c) {
					ret = n;
					break;
				}
		return ret;
	}
	
	public CnSTrieNode addChild(char c) {
		CnSTrieNode n;
		if (c != 0)
			n = new CnSCharTrieNode(c);
		else
			n = new CnSAnnotationTrieNode();
		children.addElement(n);
		n.setParent(this);
		return n;
	}
	
	public int getNbChildren() {
		return children.size();
	}
	
	public boolean contains(char c) {
		boolean ret = false;
		if (getChildren() != null)
			for (CnSTrieNode n : getChildren())
				if (n.getValue() == c) {
					ret = true;
					break;
				}
		return ret;
	}
	public Vector<CnSTrieNode> getChildren() {
		return children;
	}

}
