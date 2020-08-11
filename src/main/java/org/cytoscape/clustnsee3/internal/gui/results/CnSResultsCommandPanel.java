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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
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
import org.cytoscape.clustnsee3.internal.view.state.CnSUserViewState;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
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
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		clusterPanel = new CnSPanel();
		clusterPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		newClusterViewButton = new CnSButton("New cluster view");
		clusterPanel.addComponent(newClusterViewButton, 0, 0, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 0, 0, 0, 0);
		newClusterNetworkViewButton = new CnSButton("New network cluster view");
		clusterPanel.addComponent(newClusterNetworkViewButton, 1, 0, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 0, 5, 0, 0);
		addClusterToViewButton = new CnSButton("Add cluster to view");
		clusterPanel.addComponent(addClusterToViewButton, 0, 1, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
		addClusterNetworkToViewButton = new CnSButton("Add cluster network to view");
		clusterPanel.addComponent(addClusterNetworkToViewButton, 1, 1, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		addComponent(clusterPanel, 0, 0, 1, 1, 0.0, 1.0, NORTH, NONE, 5, 5, 0, 5, 0, 0);

		partitionPanel = new CnSPanel();
		partitionViewButton = new CnSButton("Partition view");
		partitionPanel.addComponent(partitionViewButton, 0, 0, 1, 1, 1.0, 1.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
		exportPartitionButton = new CnSButton("Export partition");
		partitionPanel.addComponent(exportPartitionButton, 1, 0, 1, 1, 1.0, 1.0, CENTER, NONE, 5, 5, 5, 0, 0, 0);
		discardPartitionButton = new CnSButton("Discard partition");
		partitionPanel.addComponent(discardPartitionButton, 2, 0, 1, 1, 1.0, 1.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		addComponent(partitionPanel, 0, 1, 1, 1, 1.0, 1.0, NORTH, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		setEnabled(false);
	}
	
	private void initListeners() {
		newClusterNetworkViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL);
		        CnSCluster cluster = (CnSCluster)CnSEventManager.handleMessage(ev);
		        if (cluster != null) {
		        	Long suid = makeClusterView(cluster);
		        	
		        	ev = new CnSEvent(CnSViewManager.EXPAND_CLUSTER, CnSEventManager.VIEW_MANAGER);
		        	ev.addParameter(CnSViewManager.SUID, suid);
		        	CnSEventManager.handleMessage(ev);
		        	
		        	// Apply the CnS visual style to the view
		        	ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
					CnSEventManager.handleMessage(ev);
		        }
	        }
		});
		newClusterViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL);
		        CnSCluster cluster = (CnSCluster)CnSEventManager.handleMessage(ev);
				if (cluster != null) {
					makeClusterView(cluster);
					
					// create CnS attributes in the partition network table
					ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
					CnSView sView = (CnSView)CnSEventManager.handleMessage(ev);
					ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, sView);
					CnSNetwork sNetwork = (CnSNetwork)CnSEventManager.handleMessage(ev);
			        CyTable networkTable = sNetwork.getNetwork().getDefaultNetworkTable();
	                if (networkTable.getColumn("CnS:viewState") == null) networkTable.createColumn("CnS:viewState", String.class, true);
	                sNetwork.getNetwork().getRow(sNetwork.getNetwork()).set("CnS:viewState", "user");
	                
	                // Apply the CnS visual style to the view
					ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
					CnSEventManager.handleMessage(ev);
	            }
			}
		});
		partitionViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		        
				ev = new CnSEvent(CnSViewManager.GET_PARTITION_VIEW, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.REFERENCE, partition);
				CnSView partitionView = (CnSView)CnSEventManager.handleMessage(ev);
				
				if (partitionView == null) { // partition network is not yet existing
					CyNetwork inputNetwork = partition.getInputNetwork();
					
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
					ev = new CnSEvent(CyActivator.GET_LAYOUT_ALGORITHM_MANAGER, CnSEventManager.CY_ACTIVATOR);
					CyLayoutAlgorithmManager clam = (CyLayoutAlgorithmManager)CnSEventManager.handleMessage(ev);
		        
					// Create a new network
					CySubNetwork partNet = crn.addSubNetwork();
            	
					// Set the network name
					partNet.getRow(partNet).set(CyNetwork.NAME, partition.getName() + ":" + partition.getAlgorithmName());
            	
					// Add the network to Cytoscape
					networkManager.addNetwork(partNet);
					
					// create a new view for my network
					CyNetworkView partCyView = cnvf.createNetworkView(partNet);
					networkViewManager.addNetworkView(partCyView);

					// register the partition network
					partitionView = new CnSView(partCyView, new CnSPartitionViewState(partition));
					CnSNetwork partNetwork = new CnSNetwork(partNet);
					
	                ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
	                ev.addParameter(CnSNetworkManager.NETWORK, partNetwork);
	                CnSEventManager.handleMessage(ev);
	                
	                // create CnS attributes in the partition network table
	                CyTable networkTable = partNet.getDefaultNetworkTable();
	                if (networkTable.getColumn("CnS:viewState") == null) networkTable.createColumn("CnS:viewState", String.class, true);
	                partNet.getRow(partNet).set("CnS:viewState", partitionView.getStateValue());
						
					// Fill partition network with cluster nodes
					for (CnSCluster cluster : partition.getClusters()) {
						ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
						ev.addParameter(CnSViewManager.REFERENCE, cluster);
						CnSView clusterView = (CnSView)CnSEventManager.handleMessage(ev);
						
						CySubNetwork clNet = null;
						if (clusterView == null) {
							CnSNetwork cnsn = makeClusterNetworkAndView(cluster, partition);
							clNet = cnsn.getNetwork();
						}
						else {
							clNet = cluster.getNetwork();
						}
						
						// Add a node in partition network
						CyNode clNode = cluster.getCyNode();
						if (clNode == null) {
							clNode = partNet.addNode();
							cluster.setCyNode(clNode);
							partition.addClusterNode(clNode);
						}
						else {
							partNet.addNode(clNode);
						}
						
						// add the cluster in the partition view
						partitionView.addCluster(cluster);
						
						// fill CnS attributes
						for (String key : partNetwork.getNodeColumns().keySet())
							partNet.getRow(clNode).set(key, cluster.getAttributes().get(key));
						
						// fill CnS attributes
				        for (String key : partNetwork.getEdgeColumns().keySet())
				        	for (CnSEdge edge : cluster.getEdges())
				        		clNet.getRow(edge.getCyEdge()).set(key, edge.getAttributes().get(key));

						// make the view up to date
						partitionView.getView().updateView();
						
						// Set nested network
						clNode.setNetworkPointer(clNet);
					}
					
					
					// Add links between cluster nodes
					for (CnSClusterLink clusterLink : partition.getClusterLinks()) {
						CyEdge ce = null;
						if (clusterLink.getEdges().size() > 0) {
							if (clusterLink.getInteractionEdge().getCyEdge() == null) {
								ce = partNet.addEdge(clusterLink.getSource().getCyNode(), clusterLink.getTarget().getCyNode(), false);
								clusterLink.getInteractionEdge().setCyEdge(ce);;
								partition.addClusterEdge(ce);
							
								for (String key : partNetwork.getEdgeColumns().keySet())
									partNet.getRow(ce).set(key, clusterLink.getInteractionEdge().getAttributes().get(key));
							}
							else {
								ce = clusterLink.getInteractionEdge().getCyEdge();
								partNet.addEdge(ce);
								for (String key : partNetwork.getEdgeColumns().keySet())
									partNet.getRow(ce).set(key, clusterLink.getInteractionEdge().getAttributes().get(key));
							}
						}
						if (clusterLink.getNodes().size() > 0) {
							if (clusterLink.getMulticlassEdge().getCyEdge() == null) {
								ce = partNet.addEdge(clusterLink.getSource().getCyNode(), clusterLink.getTarget().getCyNode(), false);
								clusterLink.getMulticlassEdge().setCyEdge(ce);
								partition.addClusterEdge(ce);
								for (String key : partNetwork.getEdgeColumns().keySet())
									partNet.getRow(ce).set(key, clusterLink.getMulticlassEdge().getAttributes().get(key));
							}
							else {
								ce = clusterLink.getMulticlassEdge().getCyEdge();
								partNet.addEdge(ce);
								for (String key : partNetwork.getEdgeColumns().keySet())
									partNet.getRow(ce).set(key, clusterLink.getMulticlassEdge().getAttributes().get(key));
							}
						}
					}
					
					// make the view up to date
					partitionView.getView().updateView();
					
					// set visual properties of nodes and edges
					for (CyNode no : partNet.getNodeList())
						partitionView.getView().getNodeView(no).setVisualProperty(BasicVisualLexicon.NODE_TOOLTIP, partNet.getRow(no).get(CyNetwork.NAME, String.class));

					// make the view up to date
					partitionView.getView().updateView();
					
					// apply circular layout
					CyLayoutAlgorithm cla = clam.getLayout("circular");
					TaskIterator tit = cla.createTaskIterator(partitionView.getView(), cla.getDefaultLayoutContext(), new HashSet<View<CyNode>>(partitionView.getView().getNodeViews()), "");
					tm.execute(tit);
					
					// make the view up to date
					partitionView.getView().updateView();
					
					// associate the partition with her network
	                ev = new CnSEvent(CnSPartitionManager.SET_PARTITION_NETWORK, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.NETWORK, partNetwork);
					ev.addParameter(CnSPartitionManager.PARTITION, partition);
					CnSEventManager.handleMessage(ev);
					
					// register the partition view
					ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, partitionView);
					ev.addParameter(CnSViewManager.NETWORK, partNetwork);
					CnSEventManager.handleMessage(ev);
					
					// associate the partition with her view
					ev = new CnSEvent(CnSPartitionManager.SET_PARTITION_VIEW, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.VIEW, partitionView);
					ev.addParameter(CnSPartitionManager.PARTITION, partition);
					CnSEventManager.handleMessage(ev);
					
					// record cluster location in the view
					ev = new CnSEvent(CnSViewManager.RECORD_CLUSTERS_LOCATION, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, partitionView);
					CnSEventManager.handleMessage(ev);
					
					// set the current selected view
					ev = new CnSEvent(CnSViewManager.SET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, partitionView);
					CnSEventManager.handleMessage(ev);
				}
				
				// make the view up to date
				partitionView.getView().updateView();
				
				// set the current network in cytoscape to be the partition network
				ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.VIEW, partitionView);
				CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
				ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
				CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev);
				applicationManager.setCurrentNetwork(network.getNetwork());
				
				// Apply the CnS visual style to the view
				ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
				CnSEventManager.handleMessage(ev);
			}
		});
		discardPartitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// get the selected partition
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		        
				// remove it from the results panel
				ev = new CnSEvent(CnSResultsPanel.DISCARD_PARTITION, CnSEventManager.RESULTS_PANEL);
				ev.addParameter(CnSResultsPanel.PARTITION, partition);
				CnSEventManager.handleMessage(ev);
				
				// remove it from the partition controller
				ev = new CnSEvent(CnSPartitionManager.REMOVE_PARTITION, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.PARTITION, partition);
				CnSEventManager.handleMessage(ev);
				
			}
		});
		addClusterToViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL);
		        CnSCluster cluster = (CnSCluster)CnSEventManager.handleMessage(ev);
				if (cluster != null) {
					addClusterToView(cluster);
					
					// Apply the CnS visual style to the view
					ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
					CnSEventManager.handleMessage(ev);
				}
			}
		});
		addClusterNetworkToViewButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_CLUSTER, CnSEventManager.RESULTS_PANEL);
		        CnSCluster cluster = (CnSCluster)CnSEventManager.handleMessage(ev);
		        if (cluster != null) {
		        	Long suid = addClusterToView(cluster);
		        	if (suid != null) {
		        		ev = new CnSEvent(CnSViewManager.EXPAND_CLUSTER, CnSEventManager.VIEW_MANAGER);
		        		ev.addParameter(CnSViewManager.SUID, suid);
		        		CnSEventManager.handleMessage(ev);
		        	
		        		// Apply the CnS visual style to the view
		        		ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
		        		CnSEventManager.handleMessage(ev);
		        	}
		        }
			}
		});
		
		exportPartitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("Clust&See file", "cns"));
				int ret = jfc.showSaveDialog(null);
				boolean tosave =false;
				File file = null;
				if (ret == JFileChooser.APPROVE_OPTION) {
					tosave =true;
					file = jfc.getSelectedFile();
					if (file.exists()) {
						ret = JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists. Are you sure you want to owerwrite it ?");
						tosave =  (ret == JOptionPane.YES_OPTION);
					}	
				}
				if (tosave) {
					try {
						CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
						CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				        
						BufferedWriter br= new BufferedWriter(new FileWriter(file));
						br.write("#ClustnSee analysis export");
						br.newLine();
						br.write("#Algorithm:" + partition.getAlgorithmName());
						br.newLine();
						br.write("#Network:" + partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
						br.newLine();
						br.write("#Scope:" + partition.getScope());
						br.newLine();
						br.write("#Cluster Name (nb nodes in cluster, nb multi-classed nodes in cluster):");
						br.newLine();
						br.write("#");
						for (CnSCluster cluster : partition.getClusters()) {
							ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER);
							ev.addParameter(CnSPartitionManager.PARTITION, partition);
							ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
							Integer nb = (Integer)CnSEventManager.handleMessage(ev);
							br.write(cluster.getName() + "(" + cluster.getNbNodes() + "," + nb.intValue() + "), ");
						}
						br.newLine();
						Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
						while (k.hasNext()) {
							int key = k.next();
							br.write("#Parameter:" + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
							br.newLine();
						}
						br.newLine();
						br.newLine();
						for (CnSCluster cluster : partition.getClusters()) {
							br.write(">" + cluster.getName() + "||");
							for (int i = 0; i < cluster.getAnnotations().size(); i++)
								br.write(cluster.getAnnotations().get(i).getAnnotation() + "||");
							br.newLine();
							for (CnSNode node : cluster.getNodes()) {
								br.write(partition.getInputNetwork().getRow(node.getCyNode()).get(CyNetwork.NAME, String.class));
								br.newLine();
							}
							br.newLine();
						}
						
						br.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}		

	/**
	 * 
	 * @param
	 * @return
	 */
	public void setEnabled(boolean b) {
		partitionViewButton.setEnabled(b);
		exportPartitionButton.setEnabled(b);
		discardPartitionButton.setEnabled(b);
		newClusterViewButton.setEnabled(b);
		newClusterNetworkViewButton.setEnabled(b);
		addClusterToViewButton.setEnabled(b);
		addClusterNetworkToViewButton.setEnabled(b);
	}
	
	/*
	 * Make a network and a view from a cluster
	 */
	private CnSNetwork makeClusterNetworkAndView(CnSCluster cluster, CnSPartition partition) {
		// get the root network of the cluster
		CnSEvent ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev);
		CyRootNetwork crn = crnm.getRootNetwork(partition.getInputNetwork());
		
		// Create a new sub-network
		CySubNetwork clNet = crn.addSubNetwork();

		// Set the sub-network name
		clNet.getRow(clNet).set(CyNetwork.NAME, cluster.getName());

		// Add the sub-network to Cytoscape
		ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev);
		networkManager.addNetwork(clNet);

		// Fill sub-network with cluster nodes and relevant edges 
		for (CnSNode node : cluster.getNodes()) clNet.addNode(node.getCyNode());
		for (CnSEdge edge : cluster.getEdges()) clNet.addEdge(edge.getCyEdge());
		
		// create a new view for the sub-network
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR);
		CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev);
		CyNetworkView clView = cnvf.createNetworkView(clNet);
		
		// add the sub-network view in cytoscape
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
		networkViewManager.addNetworkView(clView);
		
		// register the sub-network
		CnSNetwork network = new CnSNetwork(clNet);
        ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
        ev.addParameter(CnSNetworkManager.NETWORK, network);
        CnSEventManager.handleMessage(ev);
        
        // fill CnS attributes
        for (String key : network.getEdgeColumns().keySet())
        	for (CnSEdge edge : cluster.getEdges())
        		clNet.getRow(edge.getCyEdge()).set(key, edge.getAttributes().get(key));

		// associate the sub-network with the cluster
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
		
		// apply the preferred layout
		Vector<CyNetworkView> v = new Vector<CyNetworkView>();
		v.addElement(clView);
		ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR);
		ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev);
		TaskIterator tit = apltf.createTaskIterator(v);
		ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
		TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev);
		tm.execute(tit);
		
		return network;
	}
	
	private Long makeClusterView(CnSCluster cluster) {
		// get the selected cluster, his partition and his associated view if exists
		CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);				
        ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.REFERENCE, cluster);
		CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
		
		// get the cluster network; create it if it doesn't exist
		CnSNetwork network;
		if (view == null) {
			network = makeClusterNetworkAndView(cluster, partition);
		}
		else {
			ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
        	ev.addParameter(CnSViewManager.VIEW, view);
        	network = (CnSNetwork)CnSEventManager.handleMessage(ev);
		}
		
		// Create a new network
		ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev);
		CyRootNetwork crn = crnm.getRootNetwork(network.getNetwork());
		CySubNetwork clNet = crn.addSubNetwork();
	
		// Set the network name
		clNet.getRow(clNet).set(CyNetwork.NAME, "User:" + cluster.getName());
	
		// Add the network to Cytoscape
		ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev);
		networkManager.addNetwork(clNet);

		// Add a node in partition network
		CyNode clNode = cluster.getCyNode();
		if (clNode == null) {
			clNode = clNet.addNode();
			cluster.setCyNode(clNode);
			partition.addClusterNode(clNode);
		}
		else {
			clNet.addNode(clNode);
		}
		
		// Set nested network
		clNode.setNetworkPointer(network.getNetwork());
		
		// create a new view for my network
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR);
		CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev);
		CyNetworkView cyView = cnvf.createNetworkView(clNet);
		
		CnSUserViewState viewState = new CnSUserViewState(partition);
		viewState.addCluster(cluster);
		CnSView myView = new CnSView(cyView, viewState);
		
		// add the view in cytoscape
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
		networkViewManager.addNetworkView(cyView);
		
		// register network
		CnSNetwork partNetwork = new CnSNetwork(clNet);
		ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
        ev.addParameter(CnSNetworkManager.NETWORK, partNetwork);
        CnSEventManager.handleMessage(ev);
        
        // register view
        ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, myView);
		ev.addParameter(CnSViewManager.NETWORK, partNetwork);
		CnSEventManager.handleMessage(ev);
		
		// assign a partition to the view
		ev = new CnSEvent(CnSViewManager.SET_VIEW_PARTITION, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, myView);
		ev.addParameter(CnSViewManager.PARTITION, partition);
		CnSEventManager.handleMessage(ev);
		
		// record cluster location
		ev = new CnSEvent(CnSViewManager.RECORD_CLUSTERS_LOCATION, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, myView);
		CnSEventManager.handleMessage(ev);
		
		// set the current selected view
		ev = new CnSEvent(CnSViewManager.SET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, myView);
		CnSEventManager.handleMessage(ev);
		
		// fill CnS attributes
		for (String key : network.getNodeColumns().keySet())
			clNet.getRow(cluster.getCyNode()).set(key, cluster.getAttributes().get(key));
		
		myView.getView().updateView();
		return clNode.getSUID();
	}
	
	private Long addClusterToView(CnSCluster cluster) {
		Long ret = null;
		
		// get the selected view, in which the new cluster must be added
		CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		CnSView currentView = (CnSView)CnSEventManager.handleMessage(ev);
		
		if (currentView != null)
			if (!currentView.getClusters().contains(cluster)) {
				// get the selected partition
				ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				
				// get the cluster view
				ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.REFERENCE, cluster);
				CnSView clView = (CnSView)CnSEventManager.handleMessage(ev);
		
				// get the cluster network; create it if it doesn't exist
				CnSNetwork network;
				if (clView == null) {
					network = makeClusterNetworkAndView(cluster, partition);
					ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.REFERENCE, cluster);
					clView = (CnSView)CnSEventManager.handleMessage(ev);
				}
				else {
					ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, clView);
					network = (CnSNetwork)CnSEventManager.handleMessage(ev);
				}
		
				// get the selected network
				ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.VIEW, currentView);
				CnSNetwork currentNet = (CnSNetwork)CnSEventManager.handleMessage(ev);
		
				// stop listening to network changes
				currentView.setModifCluster(true);
		
				CyNode clNode = null;
				if (currentNet != null) {
					// Add a node in the current network
					clNode = cluster.getCyNode();
					if (clNode == null) {
						clNode = currentNet.getNetwork().addNode();
						cluster.setCyNode(clNode);
						partition.addClusterNode(clNode);
					}
					else {
						currentNet.getNetwork().addNode(clNode);
					}
					currentView.getView().updateView();
				
					// make the needed links
					CnSCluster partner;
					for (CnSClusterLink cl : partition.getClusterLinks()) {
						if (cl.getSource() == cluster)
							partner = cl.getTarget();
						else if (cl.getTarget() == cluster)
							partner = cl.getSource();
						else
							partner = null;
						if (partner != null) {
							if (currentView.getClusters().contains(partner)) {
								Boolean b = false;
								ev = new CnSEvent(CnSViewManager.IS_EXPANDED, CnSEventManager.VIEW_MANAGER);
								ev.addParameter(CnSViewManager.CLUSTER, partner);
								ev.addParameter(CnSViewManager.VIEW, currentView);
								b = (Boolean)CnSEventManager.handleMessage(ev);
							
								if (!b.booleanValue()) {
									if (cl.getEdges().size() > 0) {
										if (cl.getInteractionEdge().getCyEdge() == null) {
											CyEdge ce = currentNet.getNetwork().addEdge(cluster.getCyNode(), partner.getCyNode(), false);
											cl.getInteractionEdge().setCyEdge(ce);;
											partition.addClusterEdge(ce);
											currentView.getView().updateView();
											for (String key : currentNet.getEdgeColumns().keySet())
												currentNet.getNetwork().getRow(ce).set(key, cl.getInteractionEdge().getAttributes().get(key));
										}
										else {
											currentNet.getNetwork().addEdge(cl.getInteractionEdge().getCyEdge());
											currentView.getView().updateView();
											for (String key : currentNet.getEdgeColumns().keySet())
												currentNet.getNetwork().getRow(cl.getInteractionEdge().getCyEdge()).set(key, cl.getInteractionEdge().getAttributes().get(key));
										}
									}
								
									currentView.getView().updateView();
									if (cl.getNodes().size() > 0) {
										if (cl.getMulticlassEdge().getCyEdge() == null) {
											CyEdge ce = currentNet.getNetwork().addEdge(cluster.getCyNode(), partner.getCyNode(), false);
											cl.getMulticlassEdge().setCyEdge(ce);;
											currentView.getView().updateView();
											for (String key : currentNet.getEdgeColumns().keySet())
												currentNet.getNetwork().getRow(ce).set(key, cl.getMulticlassEdge().getAttributes().get(key));
										}
										else {
											currentNet.getNetwork().addEdge(cl.getMulticlassEdge().getCyEdge());
											currentView.getView().updateView();
											for (String key : currentNet.getEdgeColumns().keySet())
												currentNet.getNetwork().getRow(cl.getMulticlassEdge().getCyEdge()).set(key, cl.getMulticlassEdge().getAttributes().get(key));
										}
									}
									currentView.getView().updateView();
								}
								else {
									for (CnSEdge cnse : cl.getEdges()) {
										if (partner.contains(cnse.getCyEdge().getSource()))
											currentNet.getNetwork().addEdge(cluster.getCyNode(), cnse.getCyEdge().getSource(), false);
										else
											currentNet.getNetwork().addEdge(cluster.getCyNode(), cnse.getCyEdge().getTarget(), false);
									}
									currentView.getView().updateView();
									for (CnSNode cnsn : cl.getNodes()) {
										if (!currentNet.getNetwork().containsEdge(cluster.getCyNode(), cnsn.getCyNode()) && 
												!currentNet.getNetwork().containsEdge(cnsn.getCyNode(), cluster.getCyNode())) {
											currentNet.getNetwork().addEdge(cluster.getCyNode(), cnsn.getCyNode(), false);
											currentView.getView().updateView();
										}
									}
									currentView.getView().updateView();
								}
							}
						}				
					}
				}
			
				currentView.addCluster(cluster);
		
				// Set nested network
				clNode.setNetworkPointer(network.getNetwork());
				
				// fill CnS attributes
				for (String key : currentNet.getNodeColumns().keySet())
					currentNet.getNetwork().getRow(clNode).set(key, cluster.getAttributes().get(key));
				
				// make the view up to date
				currentView.getView().updateView();
			
				// record cluster location
				ev = new CnSEvent(CnSViewManager.RECORD_CLUSTERS_LOCATION, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.VIEW, currentView);
				CnSEventManager.handleMessage(ev);
			
				// start listening to network changes
				currentView.setModifCluster(false);
			
				ret = clNode.getSUID();
			}
		else
			ret = cluster.getCyNode().getSUID();
		return ret;
	}
}
