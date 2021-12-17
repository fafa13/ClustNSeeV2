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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class CnSNodeNetwork {
	private CyNetwork network;
	private CyNode node;
	private CnSNodeAnnotationFile annotationFile;
	
	public CnSNodeNetwork(CyNetwork nw, CyNode no, CnSNodeAnnotationFile af) {
		network = nw;
		node = no;
		annotationFile = af;
	}
	public CyNetwork getNetwork() {
		return network;
	}
	public CyNode getNode() {
		return node;
	}
	public CnSNodeAnnotationFile getAnnotationFile() {
		return annotationFile;
	}
	public boolean equals(Object o) {
		CnSNodeNetwork cnn = (CnSNodeNetwork)o;
		return cnn.getNetwork().equals(network) & cnn.getNode().equals(node) & cnn.getAnnotationFile().equals(annotationFile);
	}
}
