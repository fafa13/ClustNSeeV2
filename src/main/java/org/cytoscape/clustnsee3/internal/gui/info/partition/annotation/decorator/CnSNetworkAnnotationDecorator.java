/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 24 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.decorator;

import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.model.CyNode;

/**
 * 
 */
public abstract class CnSNetworkAnnotationDecorator {
	public Object getMeanValue(HashMap<CyNode, ?> ht, Vector<CyNode> nodes) {
		return null;
	}
}
