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
import java.util.Vector;

/**
 * 
 */
public class CnSNodeAnnotationFile {
	private File file;
	private Vector<CnSNodeAnnotation> annotations;
	
	public CnSNodeAnnotationFile(File file) {
		super();
		this.file = file;
		annotations = new Vector<CnSNodeAnnotation>();
	}
	
	public File getFile() { 
		return file;
	}
	public Vector<CnSNodeAnnotation> getAnnotations() {
		return annotations;
	}
	public String toString() {
		return file.getAbsolutePath();
	}
	public void addAnnotation(CnSNodeAnnotation annotation) {
		if (!annotations.contains(annotation)) annotations.addElement(annotation);
	}
}
