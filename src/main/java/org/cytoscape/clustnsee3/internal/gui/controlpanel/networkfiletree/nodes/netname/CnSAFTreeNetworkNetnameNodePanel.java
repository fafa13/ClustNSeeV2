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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreePanel;

import java.awt.Font;

public class CnSAFTreeNetworkNetnameNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 8723572659604754855L;
	private CnSButton closeButton;
	private CnSButton bhButton;
	private String networkName;
	
	public CnSAFTreeNetworkNetnameNodePanel(String networkName, CnSAFTreeNetworkNetnameNode cnSAFTreeNetworkNetnameNode) {
		super();
		this.networkName = networkName;
		initGraphics();
		initListeners(cnSAFTreeNetworkNetnameNode);
	}
	public CnSButton getDeleteButton() {
		return closeButton;
	}
	public CnSButton getBHButton() {
		return bhButton;
	}
	public void initListeners(CnSAFTreeNetworkNetnameNode netnameNode) {
		closeButton.addActionListener(netnameNode);
		bhButton.addActionListener(netnameNode);
	}
	
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(networkName);
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		
		addComponent(label, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 5, 5, 5, 0, 0, 0);
		
		ImageIcon icon_delete = new ImageIcon(getClass().getResource("/org/cytoscape/clustnsee3/internal/resources/delete_annotation.gif"));
		closeButton = new CnSButton(icon_delete);
		closeButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 4, icon_delete.getIconHeight() + 4));
		closeButton.setFocusable(false);
		closeButton.setActionCommand("remove_network");
		addComponent(closeButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		
		bhButton = new CnSButton("BH");
		//bhButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 4, icon_delete.getIconHeight() + 4));
		//bhButton.setFocusable(false);
		//bhButton.setActionCommand("bh");
		//addComponent(bhButton, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
