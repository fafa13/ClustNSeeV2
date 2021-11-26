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
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNodeAnnotation {
	private Vector<CnSNodeAnnotationFile> annotationFiles;
	private CnSTrieNode trieNode;
	private Vector<CyNode> targetNodes;
	
	public CnSNodeAnnotation(CnSTrieNode trieNode, CnSNodeAnnotationFile file) {
		super();
		this.trieNode = trieNode;
		annotationFiles = new Vector<CnSNodeAnnotationFile>();
		annotationFiles.addElement(file);
		targetNodes = new Vector<CyNode>();
	}
	public void addTargetNode(CyNode node) {
		targetNodes.addElement(node);
	}
	public String getValue() {
		return trieNode.getWord();
	}
	public Vector<CyNode> getTargetNodes() {
		return targetNodes;
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
	public Vector<CnSNodeAnnotationFile> getAnnotationFiles() {
		return annotationFiles;
	}
	public void addAnnotationfile(CnSNodeAnnotationFile file) {
		annotationFiles.addElement(file);
	}
	public String toString() {
		return getValue();
	}
}
