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

/**
 * 
 */
public class CnSNodeAnnotationFile {
	private File file;
	
	public CnSNodeAnnotationFile(File file) {
		super();
		this.file = file;
	}
	
	public File getFile() { 
		return file;
	}
	public String toString() {
		return file.getAbsolutePath();
	}
}
