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

import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
//import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAnnotationTrie {
	private CnSTrieNode root;
	private Vector<CnSNodeAnnotation> ret_annot;
	private Vector<String> ret_string;
	
	public CnSAnnotationTrie() {
		super();
		root = new CnSCharTrieNode((char)-1);
	}
	
	public CnSTrieNode addWord(String word) {
		CnSTrieNode node = root, n;
		for (char c : word.toCharArray()) {
			n = node.getChild(c);
			if (n != null) 
				node = n;
			else 
				node = node.addChild(c);
		}
		if (!node.contains((char)0)) 
			return node.addChild((char)0);
		else
			return node.getChild((char)0);
	}
	
	public Vector<String> getWords(String prefix) {
		CnSTrieNode node = root, n = null;
		for (char c : prefix.toCharArray()) {
			n = node.getChild(c);
			if (n != null) 
				node = n;
			else
				break;
		}
		ret_string = new Vector<String>();
		if (n != null) getAllWords(prefix, n);
		return ret_string;
	}
	public Vector<CnSNodeAnnotation> getAnnotations(String prefix) {
		CnSTrieNode node = root, n = null;
		for (char c : prefix.toCharArray()) {
			n = node.getChild(c);
			if (n != null) 
				node = n;
			else
				break;
		}
		ret_annot = new Vector<CnSNodeAnnotation>();
		if (n != null) getAllAnnotations(prefix, n);
		return ret_annot;
	}
	private void getAllAnnotations(String prefix, CnSTrieNode n) {
		for (CnSTrieNode node : n.getChildren())
			if (node.getValue() != 0)
				getAllAnnotations(prefix + node.getValue(), node);
			else
				ret_annot.addElement(node.getAnnotation());
	}
	private void getAllWords(String prefix, CnSTrieNode n) {
		for (CnSTrieNode node : n.getChildren())
			if (node.getValue() != 0)
				getAllWords(prefix + node.getValue(), node);
			else
				ret_string.addElement(prefix);
	}
	public CnSTrieNode get(String value) {
		CnSTrieNode node = root, n = null;
		for (char c : value.toCharArray()) {
			n = node.getChild(c);
			if (n != null) 
				node = n;
			else
				break;
		}
		if (n != null) {
			for (CnSTrieNode n2 : node.getChildren()) 
				if (n2.getValue() == 0) {
					n = n2;
					break;
				}
		}
		return n;
	}
/*	public void removeAnnotations(CnSNodeAnnotationFile annotationFile) {
		removeAllAnnotations(annotationFile, root);
	}
	
	private boolean removeAllAnnotations(CnSNodeAnnotationFile annotationFile, CnSTrieNode node) {
		boolean b = false;
		Vector<CnSTrieNode> toRemove = new Vector<CnSTrieNode>();
		if (node.getValue() == 0) {
			if (node.getAnnotation().getAnnotationFiles().contains(annotationFile)) {
				node.getAnnotation().getAnnotationFiles().removeElement(annotationFile);
				if (node.getAnnotation().getAnnotationFiles().size() == 0) {
					b = true;
				}
			}
		}
		else {
			for (CnSTrieNode n : node.getChildren()) {
				b = removeAllAnnotations(annotationFile, n);
				if (b) {
					toRemove.addElement(n);
					if (node.getChildren().size() > 1) b = false;
				}
			}
			for (CnSTrieNode n : toRemove) {
				node.getChildren().removeElement(n);
			}
		}
		return b;
	}*/
}
