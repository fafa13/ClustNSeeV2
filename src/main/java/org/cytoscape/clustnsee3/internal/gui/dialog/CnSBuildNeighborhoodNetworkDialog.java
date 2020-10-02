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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSBuildNeighborhoodNetworkDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -8801638615169831545L;
	private JTextField nodeNameTextField, networkNameTextField, maxDistanceTextField, maxNodeNumberTextField;
	private CnSButton addNodeButton, removeNodeButton, buildNetworkButton, closeButton;
	private JTable nodesTable;
	private Vector<Vector<CnSNodeToName>> data;
	private CyNetwork network = null;
	private int maxDistance = 3, maxNodeNumber = 5000;
	
	public CnSBuildNeighborhoodNetworkDialog() {
		super();
		setModal(true);
		data = new Vector<Vector<CnSNodeToName>>();
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
		
		neiborhoodNetworkParametersPanel.addComponent(nodeNamePanel, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 30, 10, 0, 10, 0, 0);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Node name");
		nodesTable = new JTable(data, columnNames);
		nodesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(nodesTable);
		scrollPane.setPreferredSize(new Dimension(200, 150));
		nodeListPanel.addComponent(scrollPane, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 30, 10, 10, 10, 0, 0);
		removeNodeButton = new CnSButton("Remove");
		nodeListPanel.addComponent(removeNodeButton, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 60, 10, 10, 10, 0, 0);
		
		neiborhoodNetworkParametersPanel.addComponent(nodeListPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 10, 10, 0, 10, 0, 0);
		
		networkNameTextField = new JTextField(20);
		parametersPanel.addComponent(new JLabel("Network name :"), 0, 0, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 30, 10, 10, 10, 0, 0);
		parametersPanel.addComponent(networkNameTextField, 1, 0, 3, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 30, 0, 10, 10, 0, 0);
		parametersPanel.addComponent(new JLabel("Maximal distance :"), 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 10, 10, 10, 0, 0);
		maxDistanceTextField = new JTextField(String.valueOf(maxDistance), 10);
		parametersPanel.addComponent(maxDistanceTextField, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 0, 10, 30, 0, 0);
		parametersPanel.addComponent(new JLabel("Maximal node number :"), 2, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 10, 10, 10, 0, 0);
		maxNodeNumberTextField = new JTextField(String.valueOf(maxNodeNumber), 20);
		parametersPanel.addComponent(maxNodeNumberTextField, 3, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 0, 0, 10, 10, 0, 0);
		
		neiborhoodNetworkParametersPanel.addComponent(parametersPanel, 0, 2, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 10, 10, 10, 10, 0, 0);
		
		mainPanel.addComponent(neiborhoodNetworkParametersPanel, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 10, 10, 0, 10, 0, 0);
		
		buildNetworkButton = new CnSButton("Build network");
		buttonsPanel.addComponent(buildNetworkButton, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.NONE, 0, 10, 0, 10, 0, 0);
		closeButton = new CnSButton("Close");
		buttonsPanel.addComponent(closeButton, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.NONE, 0, 10, 0, 10, 0, 0);
		
		mainPanel.addComponent(buttonsPanel, 0, 1, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 10, 10, 10, 10, 0, 0);
		getContentPane().add(mainPanel);
	}
	
	private void initListeners() {
		closeButton.addActionListener(this);
		addNodeButton.addActionListener(this);
		nodeNameTextField.addActionListener(this);
		removeNodeButton.addActionListener(this);
		buildNetworkButton.addActionListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == closeButton)
			dispose();
		else if (event.getSource() == addNodeButton || event.getSource() == nodeNameTextField) {
			CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
			network = cam.getCurrentNetwork();
			if (network == null)
				JOptionPane.showMessageDialog(null, "You must select a network first !");
			else {
				if (!nodeNameTextField.getText().equals("")) {
					ev = new CnSEvent(CnSNetworkManager.GET_NODES_WITH_VALUE, CnSEventManager.NETWORK_MANAGER);
					ev.addParameter(CnSNetworkManager.NETWORK, network);
					ev.addParameter(CnSNetworkManager.COLNAME, "name");
					ev.addParameter(CnSNetworkManager.VALUE, nodeNameTextField.getText());
					Set<CyNode> n = (Set<CyNode>)CnSEventManager.handleMessage(ev);
					if (!n.isEmpty()) {
						Vector<CnSNodeToName> v  = new Vector<CnSNodeToName>();
						v.addElement(new CnSNodeToName(n.iterator().next(), nodeNameTextField.getText()));
						data.addElement(v);
						nodeNameTextField.setText("");
						nodesTable.updateUI();
						nodesTable.repaint();
					}
					else
						JOptionPane.showMessageDialog(null, "No node exists with the given name.");
				}
			}
		}
		else if (event.getSource() == removeNodeButton) {
			int toRemove = nodesTable.getSelectedRow();
			if (toRemove >= 0) {
				data.removeElementAt(toRemove);
				nodesTable.clearSelection();
				nodesTable.updateUI();
				nodesTable.repaint();
			}
		}
		else if (event.getSource() == buildNetworkButton) {
			if (networkNameTextField.getText().equals(""))
				JOptionPane.showMessageDialog(null, "You must provide a name for the new network.");
			else if (data.size() == 0)
				JOptionPane.showMessageDialog(null, "You must provide some nodes from which to build the network.");
			else if (network == null)
				JOptionPane.showMessageDialog(null, "You must select a network first !");
			else {
				Vector<CyNode> targetNodes = new Vector<CyNode>();
				for (Vector<CnSNodeToName> v : data) {
					CnSNodeToName ntn = v.firstElement();
					targetNodes.addElement(ntn.getNode());
				}
				for (CyNode sourceNode : network.getNodeList()) {
					for (CyNode targetNode : targetNodes) {
						if (network.getConnectingEdgeList(sourceNode, targetNode, CyEdge.Type.ANY).size() <= maxDistance) {
							
						}
					}
				}
			}
		}
	}
}
