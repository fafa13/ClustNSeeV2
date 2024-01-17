/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 8 nov. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util;

public class CnSTwoIntegers implements Comparable<CnSTwoIntegers> {
	private int i1, i2;
	
	public CnSTwoIntegers(int i1, int i2) {
		super();
		this.i1 = i1;
		this.i2 = i2;
	}
	
	public String toString() {
		return i1 + " / " + i2;
	}
	
	public int getI1() {
		return i1;
	}
	
	public int getI2() {
		return i2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CnSTwoIntegers i) {
		return i1 - i.getI1();
	}
}
