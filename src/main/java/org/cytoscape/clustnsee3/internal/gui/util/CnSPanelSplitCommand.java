/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 3 déc. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util;

import java.awt.Component;

import javax.swing.JSplitPane;

/**
 * 
 */
public class CnSPanelSplitCommand extends CnSPanel {
	private static final long serialVersionUID = -6722311530797841381L;
	protected CnSPanel commandPanel;
	protected Component mainPanel;
	protected JSplitPane splitPane;
	
	public CnSPanelSplitCommand() {
		super();
		super.initGraphics();
	}
	
	public void initGraphics(CnSPanel commandPanel, Component mainPanel) {
		//System.err.println("CnSPanelSplitCommand.initGraphics(" + commandPanel + " , " + mainPanel);
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0D);
		splitPane.setAutoscrolls(true);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		addComponent(splitPane, 0, 0, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, 5, 5, 0, 0, 0, 0);	
		this.commandPanel = commandPanel;
		this.mainPanel = mainPanel;
		splitPane.setRightComponent(commandPanel);
		splitPane.setLeftComponent(mainPanel);
	}
	
	public void setResizeWeight(double d) {
		splitPane.setResizeWeight(d);
	}
}
