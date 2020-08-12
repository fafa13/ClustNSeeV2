/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import javax.swing.JDialog;

import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSComparePartitionsResultDialog extends JDialog {
	private static final long serialVersionUID = 663921405674284603L;
	private double jaccard = 0.0;
	
	public CnSComparePartitionsResultDialog(CnSPartition part1, CnSPartition part2) {
		super();
		setModal(true);
		initGraphics(part1, part2);
		initListeners();
	}
	
	private void initGraphics(CnSPartition part1, CnSPartition part2) {
		setTitle("Compare partitions " + part1 + " and " + part2);
		
	}
	
	private void initListeners() {
		
	}
}
