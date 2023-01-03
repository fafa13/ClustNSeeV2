/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util.paneltree;

import java.awt.Font;

import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;

public class CnSPanelTreePanel extends CnSPanel {
	private static final long serialVersionUID = -3014417831630947233L;
	protected Font font = new Font("serif", Font.PLAIN, 12);
	
	public void initGraphics() {
		super.initGraphics();
	}
	public void initListeners(CnSPanelTreeNode node) { };
	public void setFont(Font f) {
		font = f;
	}
	public void deriveFont(int style, float size) {
		font = font.deriveFont(style, size);
	}
	public void deriveFont(float size) {
		font = font.deriveFont(size);
	}
	public void deriveFont(int style) {
		font = font.deriveFont(style);
	}
}
