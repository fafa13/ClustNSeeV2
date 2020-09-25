/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 24 sept. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSBuildNeighborhoodNetworkDialog extends JDialog {
	private static final long serialVersionUID = -8801638615169831545L;
	private JTextField nodeNameTextField, networkNameTextField, maxDistanceTextField, maxNodeNumberTextField;
	private CnSButton addNodeButton, removeNodeButton, buildNetworkButton, closeButton;
	private JTable nodesTable;
	private Vector<Vector<CnSNode>> data;
	
	public CnSBuildNeighborhoodNetworkDialog() {
		super();
		setModal(true);
		data = new Vector<Vector<CnSNode>>();
		initGraphics();
		initListeners();
	}
	
	private void initGraphics() {
		setTitle("Build neighborhood network");
		CnSPanel mainPanel = new CnSPanel();
		CnSPanel neiborhoodNetworkParametersPanel = new CnSPanel();
		CnSPanel nodeNamePanel = new CnSPanel();
		CnSPanel nodeListPanel = new CnSPanel();
		CnSPanel parametersPanel = new CnSPanel();
		CnSPanel buttonsPanel = new CnSPanel();
		
		neiborhoodNetworkParametersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Neighborhood network parameters"));
		nodeNamePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Node name"));
		nodeListPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Node list"));
		parametersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Neighborhood parameters"));
		
		nodeNameTextField = new JTextField(20);
		nodeNamePanel.addComponent(nodeNameTextField, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 25, 10, 10, 0, 0, 0);
		addNodeButton = new CnSButton("Add");
		nodeNamePanel.addComponent(addNodeButton, 1, 0, 1, 1, 0.0, 1.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 25, 10, 10, 10, 0, 0);
		
		neiborhoodNetworkParametersPanel.addComponent(nodeNamePanel, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Node name");
		nodesTable = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(nodesTable);
		scrollPane.setPreferredSize(new Dimension(200, 150));
		nodeListPanel.addComponent(scrollPane, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 30, 10, 0, 10, 0, 0);
		removeNodeButton = new CnSButton("Remove");
		nodeListPanel.addComponent(removeNodeButton, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		
		neiborhoodNetworkParametersPanel.addComponent(nodeListPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 10, 10, 0, 10, 0, 0);
		
		networkNameTextField = new JTextField(20);
		parametersPanel.addComponent(new JLabel("Network name :"), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 30, 10, 10, 10, 0, 0);
		parametersPanel.addComponent(networkNameTextField, 1, 0, 3, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 30, 0, 10, 10, 0, 0);
		parametersPanel.addComponent(new JLabel("Maximal distance :"), 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 10, 10, 10, 0, 0);
		maxDistanceTextField = new JTextField(10);
		parametersPanel.addComponent(maxDistanceTextField, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 0, 10, 30, 0, 0);
		parametersPanel.addComponent(new JLabel("Maximal node number :"), 2, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 10, 10, 10, 0, 0);
		maxNodeNumberTextField = new JTextField(10);
		parametersPanel.addComponent(maxNodeNumberTextField, 3, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 0, 10, 10, 0, 0);
		
		neiborhoodNetworkParametersPanel.addComponent(parametersPanel, 0, 2, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 10, 10, 10, 10, 0, 0);
		
		mainPanel.addComponent(neiborhoodNetworkParametersPanel, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 30, 10, 0, 10, 0, 0);
		
		buildNetworkButton = new CnSButton("Build network");
		buttonsPanel.addComponent(buildNetworkButton, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		closeButton = new CnSButton("Close");
		buttonsPanel.addComponent(closeButton, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		
		mainPanel.addComponent(buttonsPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		getContentPane().add(mainPanel);
	}
	
	/*public Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}*/
	
	private void initListeners() {
		
	}
}
