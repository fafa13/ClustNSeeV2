/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 déc. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.nodeannotation.trie.CnSTrieNode;

public class CnSNodeAnnotationFile {
	private File file;
	private int fromLine, annotationsColumn, targetColumn;
	private char columnSeparator, annotationSeparator;
	private HashMap<String, Vector<CnSTrieNode>> rawAnnotations;
	private HashMap<CnSTrieNode, Vector<String>> rawTargets;
	
	public CnSNodeAnnotationFile(File file, int fromLine, int annotationsColumn, int targetColumn, char columnSeparator, char annotationSeparator) {
		super();
		this.file = file;
		this.fromLine = fromLine;
		this.annotationsColumn = annotationsColumn;
		this.targetColumn = targetColumn;
		this.columnSeparator = columnSeparator;
		this.annotationSeparator = annotationSeparator;
		rawAnnotations = new HashMap<String, Vector<CnSTrieNode>>();
		rawTargets = new HashMap<CnSTrieNode, Vector<String>>();
	}
	
	public File getFile() { 
		return file;
	}
	public int getFromLine() {
		return fromLine;
	}
	public Vector<CnSTrieNode> getAnnotations(String target) {
		return rawAnnotations.get(target);
	}
	public Vector<String> getTargets(CnSTrieNode annotation) {
		return rawTargets.get(annotation);
	}
	public Set<String> getAllTargets() {
		return rawAnnotations.keySet();
	}
	public Set<CnSTrieNode> getAllAnnotations() {
		return rawTargets.keySet();
	}
	public String toString() {
		return file.getAbsolutePath();
	}
	public void addElement(CnSTrieNode annotation, String target) {
		Vector<String> v = rawTargets.get(annotation);
		if (v == null) {
			v = new Vector<String>();
			v.addElement(target);
			rawTargets.put(annotation, v);
		}
		else if (!v.contains(target)) v.addElement(target);
		Vector<CnSTrieNode> v2 = rawAnnotations.get(target);
		if (v2 == null) {
			v2 = new Vector<CnSTrieNode>();
			v2.addElement(annotation);
			rawAnnotations.put(target, v2);
		}
		else if (!v2.contains(annotation)) v2.addElement(annotation);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public int getAnnotationsColumn() {
		return annotationsColumn;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public int getTargetColumn() {
		return targetColumn;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public char getColumnSeparator() {
		return columnSeparator;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public char getAnnotationSeparator() {
		return annotationSeparator;
	}
}
