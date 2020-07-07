/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 15 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.results;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.FTTaskObserver;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSClusterViewState;
import org.cytoscape.clustnsee3.internal.view.state.CnSPartitionViewState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * 
 */
public class CnSResultsCommandPanel extends CnSPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4352179671876148293L;
	private CnSButton partitionViewButton, exportPartitionButton, discardPartitionButton;
	private CnSButton newClusterViewButton, newClusterNetworkViewButton, addClusterToViewButton, addClusterNetworkToViewButton;
	private CnSPanel clusterPanel, partitionPanel;
	
	public CnSResultsCommandPanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	protected void initGraphics() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		clusterPanel = new CnSPanel();
		clusterPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		newClusterViewButton = new CnSButton("New cluster view");
		clusterPanel.addComponent(newClusterViewButton, 0, 0, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 0, 0, 0, 0);
		newClusterNetworkViewButton = new CnSButton("New network cluster view");
		clusterPanel.addComponent(newClusterNetworkViewButton, 1, 0, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 0, 5, 0, 0);
		addClusterToViewButton = new CnSButton("Add cluster to view");
		clusterPanel.addComponent(addClusterToViewButton, 0, 1, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
		addClusterNetworkToViewButton = new CnSButton("Add cluster network to view");
		clusterPanel.addComponent(addClusterNetworkToViewButton, 1, 1, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		addComponent(clusterPanel, 0, 0, 1, 1, 1.0, 1.0, NORTH, HORIZONTAL, 5, 5, 0, 5, 0, 0);

		partitionPanel = new CnSPanel();
		partitionViewButton = new CnSButton("Partition view");
		partitionPanel.addComponent(partitionViewButton, 0, 0, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
		exportPartitionButton = new CnSButton("Export partition");
		partitionPanel.addComponent(exportPartitionButton, 1, 0, 1, 1, 1.0, 1.0, CENTER, NONE, 5, 5, 5, 0, 0, 0);
		discardPartitionButton = new CnSButton("Discard partition");
		partitionPanel.addComponent(discardPartitionButton, 2, 0, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		addComponent(partitionPanel, 0, 1, 1, 1, 1.0, 1.0, NORTH, HORIZONTAL, 5, 5, 5, 5, 0, 0);
	}
	
	private void initListeners() {
		newClusterNetworkViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL);
		        CnSCluster newCluster = (CnSCluster)CnSEventManager.handleMessage(ev);
	            
				ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.REFERENCE, newCluster);
				CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
				
				if (view == null) {
					ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
					CyNetwork inputNetwork = cam.getCurrentNetwork();
					ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev);
					CyRootNetwork crn = crnm.getRootNetwork(inputNetwork);
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR);
					CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev);
		        
					// Create a new network
					CySubNetwork myNet = crn.addSubNetwork(); //networkFactory.createNetwork();
	         
					// Add the network to Cytoscape
					networkManager.addNetwork(myNet);
	            
					// Fill network with cluster nodes and relevant edges 
					for (CnSNode node : newCluster.getNodes()) myNet.addNode(node.getCyNode());
					for (CnSEdge edge : newCluster.getEdges()) myNet.addEdge(edge.getCyEdge());
	            
					// create a new view for my network
					CyNetworkView myView = cnvf.createNetworkView(myNet);
	            
					networkViewManager.addNetworkView(myView);
					ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
	            	TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev);
	            	ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR);
	            	ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev);
	            	Vector<CyNetworkView> v = new Vector<CyNetworkView>();
	            	v.addElement(myView);
	            	TaskIterator tit = apltf.createTaskIterator(v);
	            	FTTaskObserver to = new FTTaskObserver(myView, newCluster);
	            	tm.execute(tit, to);
	            
	            	CnSNetwork network = new CnSNetwork(myNet);
	                ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
	                ev.addParameter(CnSNetworkManager.NETWORK, network);
	                CnSEventManager.handleMessage(ev);
	                
	                ev = new CnSEvent(CnSNetworkManager.SET_NETWORK_CLUSTER, CnSEventManager.NETWORK_MANAGER);
	                ev.addParameter(CnSNetworkManager.NETWORK, network);
	                ev.addParameter(CnSNetworkManager.CLUSTER, newCluster);
	                CnSEventManager.handleMessage(ev);
	                
	                ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
	                ev.addParameter(CnSNetworkManager.NETWORK, network);
	                ev.addParameter(CnSNetworkManager.NETWORK_NAME, newCluster.getName());
	                CnSEventManager.handleMessage(ev);
	                    
	            	view = new CnSView(myView, new CnSClusterViewState(newCluster));
	            	ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
	            	ev.addParameter(CnSViewManager.VIEW, view);
	            	ev.addParameter(CnSViewManager.NETWORK, network);
	            	ev.addParameter(CnSViewManager.CLUSTER, newCluster);
	            	CnSEventManager.handleMessage(ev);
				}
				else {
					ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
	            	ev.addParameter(CnSViewManager.VIEW, view);
	            	CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
	            	
					ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
			        CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev);
			        applicationManager.setCurrentNetwork(network.getNetwork());
				}
	        }
		});
		partitionViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition newPartition = (CnSPartition)CnSEventManager.handleMessage(ev);
		        
				ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.REFERENCE, newPartition);
				CnSView myView = (CnSView)CnSEventManager.handleMessage(ev);

				if (myView == null) { // partition network is not yet existing
					ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
					CyNetwork inputNetwork = cam.getCurrentNetwork();
					ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev);
					CyRootNetwork crn = crnm.getRootNetwork(inputNetwork);
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR);
					CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
					TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR);
					ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
					CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CyActivator.GET_LAYOUT_ALGORITHM_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyLayoutAlgorithmManager clam = (CyLayoutAlgorithmManager)CnSEventManager.handleMessage(ev);
		        
					// Create a new network
					CySubNetwork partNet = crn.addSubNetwork();
            	
					// Set the network name
					partNet.getRow(partNet).set(CyNetwork.NAME, partition.getName() + ":" + partition.getAlgorithmName());
            	
					// Add the network to Cytoscape
					networkManager.addNetwork(partNet);
            	
					// Fill partition network with clusters
					for (CnSCluster cluster : partition.getClusters()) {
						ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
						ev.addParameter(CnSViewManager.REFERENCE, cluster);
						myView = (CnSView)CnSEventManager.handleMessage(ev);
						
						CySubNetwork clNet = null;
						if (myView == null) {
						
							// Create a new network
							clNet = crn.addSubNetwork();
	            
							// Set the network name
							clNet.getRow(clNet).set(CyNetwork.NAME, cluster.getName());
	            
							// Add the network to Cytoscape
							networkManager.addNetwork(clNet);
	            	
							// Fill network with cluster nodes and relevant edges 
							for (CnSNode node : cluster.getNodes()) clNet.addNode(node.getCyNode());
							for (CnSEdge edge : cluster.getEdges()) clNet.addEdge(edge.getCyEdge());
	            	
							// create a new view for my network
							CyNetworkView clView = cnvf.createNetworkView(clNet);
							networkViewManager.addNetworkView(clView);
							
							CnSNetwork network = new CnSNetwork(clNet);
			                ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
			                ev.addParameter(CnSNetworkManager.NETWORK, network);
			                CnSEventManager.handleMessage(ev);
			                
			                ev = new CnSEvent(CnSNetworkManager.SET_NETWORK_CLUSTER, CnSEventManager.NETWORK_MANAGER);
			                ev.addParameter(CnSNetworkManager.NETWORK, network);
			                ev.addParameter(CnSNetworkManager.CLUSTER, cluster);
			                CnSEventManager.handleMessage(ev);
							
							// register the view
							CnSView view = new CnSView(clView, new CnSClusterViewState(cluster));
							ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
							ev.addParameter(CnSViewManager.VIEW, view);
							ev.addParameter(CnSViewManager.NETWORK, network);
							ev.addParameter(CnSViewManager.CLUSTER, cluster);
							CnSEventManager.handleMessage(ev);
							
							// apply cluster view style
							for (CnSNode node : cluster.getNodes())
								clView.getNodeView(node.getCyNode()).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.BLUE);
							Vector<CyNetworkView> v = new Vector<CyNetworkView>();
							v.addElement(clView);
							TaskIterator tit = apltf.createTaskIterator(v);
							tm.execute(tit);
						}
						else {
							clNet = cluster.getNetwork();
						}
						
						// Add a node in partition network
						CyNode clNode = cluster.getCyNode();
						if (clNode == null) {
							clNode = partNet.addNode();
							cluster.setCyNode(clNode);
							newPartition.addClusterNode(clNode);
						}
						else {
							partNet.addNode(clNode);
						}
						
						// Set name for new node
						partNet.getRow(clNode).set(CyNetwork.NAME, cluster.getName());
						
						// Set nested network
						clNode.setNetworkPointer(clNet);
					}
					// Add links between cluster nodes
					for (CnSClusterLink clusterLink : partition.getClusterLinks()) {
						if ((clusterLink.getInteractionEdge() == null) && (clusterLink.getEdges().size() > 0)) {
							CyEdge ce = partNet.addEdge(clusterLink.getSource().getCyNode(), clusterLink.getTarget().getCyNode(), false);
							clusterLink.setInteractionEdge(ce);
							newPartition.addClusterEdge(ce);
							partNet.addEdge(clusterLink.getInteractionEdge());
						}
						else if (clusterLink.getEdges().size() > 0) {
							partNet.addEdge(clusterLink.getInteractionEdge());
						}
						if ((clusterLink.getMulticlassEdge() == null) && (clusterLink.getNodes().size() > 0)) {
							CyEdge ce = partNet.addEdge(clusterLink.getSource().getCyNode(), clusterLink.getTarget().getCyNode(), false);
							clusterLink.setMulticlassEdge(ce);
							newPartition.addClusterEdge(ce);
							partNet.addEdge(clusterLink.getMulticlassEdge());
						}
						else if (clusterLink.getNodes().size() > 0) {
							partNet.addEdge(clusterLink.getMulticlassEdge());
						}
					}
					
					// create a new view for my network
					CyNetworkView cyView = cnvf.createNetworkView(partNet);
					networkViewManager.addNetworkView(cyView);
	            
					for (CnSClusterLink clusterLink : partition.getClusterLinks()) {
						CyEdge ce = clusterLink.getInteractionEdge();
						if (ce != null) {
							cyView.getEdgeView(ce).setVisualProperty(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(Math.min(clusterLink.getEdges().size(), 16)));
							cyView.getEdgeView(ce).setVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.blue);
						}
						ce = clusterLink.getMulticlassEdge();
						if (ce != null) {
							cyView.getEdgeView(ce).setVisualProperty(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(Math.min(clusterLink.getNodes().size(), 16)));
							cyView.getEdgeView(ce).setVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.green);
						}
					}
					for (CyNode no : partNet.getNodeList()) {
						cyView.getNodeView(no).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ROUND_RECTANGLE);
						cyView.getNodeView(no).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.PINK);
					}
					
					CyLayoutAlgorithm cla = clam.getLayout("circular");
					TaskIterator tit = cla.createTaskIterator(cyView, cla.getDefaultLayoutContext(), new HashSet<View<CyNode>>(cyView.getNodeViews()), "");
					tm.execute(tit);
					
					CnSNetwork network = new CnSNetwork(partNet);
	                ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
	                ev.addParameter(CnSNetworkManager.NETWORK, network);
	                CnSEventManager.handleMessage(ev);
					
	                ev = new CnSEvent(CnSPartitionManager.SET_PARTITION_NETWORK, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.NETWORK, network);
					ev.addParameter(CnSPartitionManager.PARTITION, newPartition);
					CnSEventManager.handleMessage(ev);
					
					myView = new CnSView(cyView, new CnSPartitionViewState(partition));
					ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, myView);
					ev.addParameter(CnSViewManager.NETWORK, network);
					CnSEventManager.handleMessage(ev);
					
					ev = new CnSEvent(CnSPartitionManager.SET_PARTITION_VIEW, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.VIEW, myView);
					ev.addParameter(CnSPartitionManager.PARTITION, newPartition);
					CnSEventManager.handleMessage(ev);
					
					ev = new CnSEvent(CnSViewManager.RECORD_CLUSTERS_LOCATION, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, myView);
					CnSEventManager.handleMessage(ev);
					
					ev = new CnSEvent(CnSViewManager.SET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, myView);
					CnSEventManager.handleMessage(ev);
				}
				
				myView.getView().updateView();
				
				ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.VIEW, myView);
				CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				applicationManager.setCurrentNetwork(network.getNetwork());
			}
		});
		discardPartitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		        
				ev = new CnSEvent(CnSPartitionManager.REMOVE_PARTITION, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.PARTITION, partition);
				CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CnSResultsPanel.DISCARD_PARTITION, CnSEventManager.RESULTS_PANEL);
				ev.addParameter(CnSResultsPanel.PARTITION, partition);
				CnSEventManager.handleMessage(ev);
			}
		});
	}
}
