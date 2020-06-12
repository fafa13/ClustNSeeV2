/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 8 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.algorithm;

import java.util.HashMap;

/**
 * 
 */
public class CnSAlgorithmResult {
	private int[][] classes;
	private int[] card;
	private int nb_class;
	private HashMap<Integer, Long> algo_to_cyto;
	
	public CnSAlgorithmResult(int[][] classes, int[] card, int nb_class, HashMap<Integer, Long> algo_to_cyto) {
		super();
		this.classes = classes;
		this.card = card;
		this.nb_class = nb_class;
		this.algo_to_cyto = algo_to_cyto;
	}
	public int[][] getClasses() {
		return classes;
	}
	public int[] getCard() {
		return card;
	}
	public int getNbClass() {
		return nb_class;
	}
	public HashMap<Integer, Long> getAlgoToCyto() {
		return algo_to_cyto;
	}
}
