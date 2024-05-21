/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 8 févr. 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util.cnstable;

import javax.swing.table.AbstractTableModel;

/**
 * 
 */
public abstract class CnSTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7416055527407340808L;

	private boolean[] taggedRows;
	
	public CnSTableModel() {
		super();
		taggedRows = null;
	}
	public void setTaggedRows(boolean[] tr) {
		taggedRows = tr;
	}
	public boolean isTagged(int row) {
		if (taggedRows == null) return false;
		return taggedRows[row];
	}
	public void clearTaggedRows() {
		taggedRows = null;
	}
}
