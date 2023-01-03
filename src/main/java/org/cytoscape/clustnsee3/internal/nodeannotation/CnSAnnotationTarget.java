/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 30 nov. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.util.Vector;

/**
 * 
 */
public class CnSAnnotationTarget {
	private Vector<CnSNodeAnnotationFile> files;
	private String target;
	
	public CnSAnnotationTarget(String node, CnSNodeAnnotationFile file) {
		target = node;
		files = new Vector<CnSNodeAnnotationFile>();
		files.addElement(file);
	}
	public void addFile(CnSNodeAnnotationFile file) {
		files.addElement(file);
	}
	public void removeFile(CnSNodeAnnotationFile file) {
		files.removeElement(file);
	}
	public String getTarget() {
		return target;
	}
	public Vector<CnSNodeAnnotationFile> getFiles() {
		return files;
	}
	public boolean equals(Object o) {
		CnSAnnotationTarget t = (CnSAnnotationTarget)o;
		return /*t.getFile().toString().equals(file.toString()) &&*/ target.equals(t.getTarget());
	}
}
