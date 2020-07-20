/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 20 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info;

import java.awt.Font;

import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdgeDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -2907729617937489552L;
	private CnSClusterDetailsPanel sourcePanel, targetPanel;
	private CnSInteractionsPanel interactionsPanel;
	private CnSMulticlassPanel multiclassPanel;
	private JLabel linkNameLabel;
	private CnSPanel mainPanel;
	
	public CnSEdgeDetailsPanel() {
		super();
		initGraphics();
	}
	
	protected void initGraphics() {
		sourcePanel = new CnSClusterDetailsPanel();
		targetPanel = new CnSClusterDetailsPanel();
		interactionsPanel = new CnSInteractionsPanel();
		multiclassPanel = new CnSMulticlassPanel();
		
		linkNameLabel = new JLabel();
		linkNameLabel.setFont(linkNameLabel.getFont().deriveFont(Font.BOLD));
		
		addComponent(linkNameLabel, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		mainPanel = new CnSPanel();
		
		mainPanel.addComponent(sourcePanel, 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(targetPanel, 2, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		
		addComponent(mainPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.BOTH, 5, 5, 5, 5, 0, 0);
		
	}
	public void init(CnSClusterLink clusterLink, CyEdge edge) {
		linkNameLabel.setText(clusterLink.getSource().getName() + " -- " + clusterLink.getTarget().getName());
		sourcePanel.init(clusterLink.getSource());
		targetPanel.init(clusterLink.getTarget());
		if (clusterLink.getInteractionEdge() == edge) {
			interactionsPanel.init(clusterLink);
			mainPanel.remove(interactionsPanel);
			mainPanel.remove(multiclassPanel);
			mainPanel.addComponent(interactionsPanel, 1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		}
		else {
			//multiclassPanel.init(clusterLink);
			mainPanel.remove(interactionsPanel);
			mainPanel.remove(multiclassPanel);
			mainPanel.addComponent(multiclassPanel, 1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		}
	}
}
