/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.widget.paneltree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class CnSPanelTreeUI extends BasicTreeUI {
	private TreePath path;
	public CnSPanelTreeUI() {
		super();
	}
	protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
		g.setColor(Color.BLACK);
		Rectangle lastChildBounds = getPathBounds(tree, getLastChildPath(path));
		g.drawLine(x - 15, top, x - 15,  bottom + lastChildBounds.height / 2);
	}
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
		this.path = path;
		super.paintVerticalPartOfLeg(g, clipBounds, insets, path);
	}
}
