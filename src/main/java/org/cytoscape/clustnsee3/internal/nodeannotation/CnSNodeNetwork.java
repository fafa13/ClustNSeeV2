/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 déc. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.util.Vector;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class CnSNodeNetwork {
	private CyNetwork network;
	private CyNode node;
	private Vector<CnSNodeAnnotationFile> annotationFiles;
	
	public CnSNodeNetwork(CyNetwork nw, CyNode no/*, CnSNodeAnnotationFile af*/) {
		network = nw;
		node = no;
		annotationFiles = new Vector<CnSNodeAnnotationFile>();
		//annotationFile = af;
	}
	public CyNetwork getNetwork() {
		return network;
	}
	public CyNode getNode() {
		return node;
	}
	
	public void addAnnotationFile(CnSNodeAnnotationFile af) {
		annotationFiles.addElement(af);
	}
	
	public Vector<CnSNodeAnnotationFile> getAnnotationFiles() {
		return annotationFiles;
	}
	
	public boolean equals(Object o) {
		CnSNodeNetwork cnn = (CnSNodeNetwork)o;
		return cnn.getNetwork().equals(network) & cnn.getNode().equals(node)/* & cnn.getAnnotationFile().equals(annotationFile)*/;
	}
}
