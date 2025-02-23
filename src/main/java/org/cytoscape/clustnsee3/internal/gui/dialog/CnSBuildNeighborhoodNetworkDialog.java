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
import java.util.HashSet;
import java.util.List;
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
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

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
	private CySubNetwork neighborhoodNetwork = null;
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
			CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
			CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
			network = cam.getCurrentNetwork();
			if (network == null)
				JOptionPane.showMessageDialog(null, "You must select a network first !");
			else {
				if (!nodeNameTextField.getText().equals("")) {
					ev = new CnSEvent(CnSNetworkManager.GET_NODES_WITH_VALUE, CnSEventManager.NETWORK_MANAGER, this.getClass());
					ev.addParameter(CnSNetworkManager.NETWORK, network);
					ev.addParameter(CnSNetworkManager.COLNAME, "name");
					ev.addParameter(CnSNetworkManager.VALUE, nodeNameTextField.getText());
					Set<CyNode> n = (Set<CyNode>)CnSEventManager.handleMessage(ev, true).getValue();
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
				try {
					maxDistance = -1;
					maxDistance = Integer.parseInt(maxDistanceTextField.getText());
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "You must provide a valid maximal distance.");
				}
				try {
					maxNodeNumber = -1;
					maxNodeNumber = Integer.parseInt(maxNodeNumberTextField.getText());
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "You must provide a valid maximal node number.");
				}
				
				if (maxDistance >= 0 && maxNodeNumber >= 0) {
					int dist = 0, nb_nodes = 0;
					Vector<CyEdge> edges_to_keep = new Vector<CyEdge>();
					Vector<CyNode> nodes_to_add = new Vector<CyNode>();
					List<CyNode> nodes;
				
					while (nb_nodes <= maxNodeNumber && dist < maxDistance) {
						nodes_to_add.clear();
						for (CyNode node : targetNodes) {
							nodes = network.getNeighborList(node, CyEdge.Type.ANY);
							for (CyNode currentNode : nodes)
								if (!nodes_to_add.contains(currentNode) && !targetNodes.contains(currentNode))
									nodes_to_add.addElement(currentNode);
						}
						nb_nodes = targetNodes.size() + nodes_to_add.size();
						dist++;
						if (nb_nodes <= maxNodeNumber) targetNodes.addAll(nodes_to_add);
					}
					
					for (CyEdge edge : network.getEdgeList())
						if (targetNodes.contains(edge.getSource()) && targetNodes.contains(edge.getTarget()))
							edges_to_keep.addElement(edge);
					
					// get useful cytoscape managers
					CnSEvent ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev, true).getValue();
					CyRootNetwork crn = crnm.getRootNetwork(network);
					ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CyActivator.GET_LAYOUT_ALGORITHM_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyLayoutAlgorithmManager clam = (CyLayoutAlgorithmManager)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CyActivator.GET_CY_EVENT_HELPER, CnSEventManager.CY_ACTIVATOR, this.getClass());
					CyEventHelper eh = (CyEventHelper)CnSEventManager.handleMessage(ev, true).getValue();
				
					// Create a new network
					neighborhoodNetwork = crn.addSubNetwork();
        	
					// Set the network name
					neighborhoodNetwork.getRow(neighborhoodNetwork).set(CyNetwork.NAME, networkNameTextField.getText());
        	
					// Add the network to Cytoscape
					networkManager.addNetwork(neighborhoodNetwork);
				
					// create a new view for my network
					CyNetworkView partCyView = cnvf.createNetworkView(neighborhoodNetwork);
					networkViewManager.addNetworkView(partCyView);
				
					for (CyNode node : targetNodes) neighborhoodNetwork.addNode(node);
					for (CyEdge edge : edges_to_keep) neighborhoodNetwork.addEdge(edge);
					partCyView.updateView();
				
					// apply circular layout
					CyLayoutAlgorithm cla = clam.getDefaultLayout();
					TaskIterator tit = cla.createTaskIterator(partCyView, cla.getDefaultLayoutContext(), new HashSet<View<CyNode>>(partCyView.getNodeViews()), "");
					tm.execute(tit);
				
					// select initial nodes
					for (Vector<CnSNodeToName> v : data) {
						CyNode node = v.firstElement().getNode();
						neighborhoodNetwork.getRow(node).set("selected", true);
					}
					eh.flushPayloadEvents();
					dispose();
				}
			}
		}
	}
}
