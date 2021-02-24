/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 24 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.contextual.action;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.utils.DelayedVizProp;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class CnSExpandClusterNodeAction {
	public static final String ACTION = "Expand cluster";
	/**
	 * Replace a cluster node by its network in a cytoscape view.
	 * 
	 * @param suid Cytoscape ID of the cluster node to be expanded
	 */
	public void doAction(Long suid) {
		// used to store the expanded state of a cluster
		boolean expanded = false;
		
		// get the current view (on which the expanded action occurred) 
		CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
		
		// get the current view's network
		ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, view);
		CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
		
		// get the current network related partition, if any
		ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.NETWORK, network);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		
		// if the current network is not a partition network, get the current view's related partition
		if (partition == null) {
			ev = new CnSEvent(CnSViewManager.GET_VIEW_PARTITION, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		}
		
		// get the cluster node to be expanded
		CnSNode node = partition.getClusterNode(suid);
		
		// get the corresponding cluster
		CnSCluster cluster = null, linkedCluster;
		for (CnSCluster c : partition.getClusters()) {
			if (c.getCyNode() == node.getCyNode()) {
				cluster = c;
				break;
			}
		}
		
		if (cluster != null) {
			// get the cluster view
			ev = new CnSEvent(CnSViewManager.GET_VIEW, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.REFERENCE, cluster);
			CnSView clusterView = (CnSView)CnSEventManager.handleMessage(ev);
			
			// some variables used to compute the position and the size of the cluster network in the view
			double x0, y0, x, y, x_min = 1000000, y_min = 1000000, x_max = 0, y_max = 0;
			
			// initial coordinates of the cluster node
			x0 = view.getView().getNodeView(cluster.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			y0 = view.getView().getNodeView(cluster.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			
			// compute the size of the cluster network
			for (CnSNode cnsnode : cluster.getNodes()) {
				x = clusterView.getView().getNodeView(cnsnode.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y = clusterView.getView().getNodeView(cnsnode.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				if (x_min > x) x_min = x;
				if (y_min > y) y_min = y;
				if (x_max < x) x_max = x;
				if (y_max < y) y_max = y;
			}
			double x_size = x_max - x_min + 1;
			double y_size = y_max - y_min + 1;
			
			// zoom factor to apply when adding the cluster network into the view. This factor depends on the cluster network size and also on the number of nodes in the cluster
			double ratio = (Math.min(cluster.getNbNodes(), 100) * (50 - 4750 / 99) + 4750 / 99) / Math.max(x_size, y_size);
			
			// flag is set to avoid the calling of listeners on view changes
			view.setModifCluster(true);
			
			// get the cytoscape event handler
			ev = new CnSEvent(CyActivator.GET_CY_EVENT_HELPER, CnSEventManager.CY_ACTIVATOR);
			CyEventHelper eh = (CyEventHelper)CnSEventManager.handleMessage(ev);
			
			// fire all cytoscape events
			eh.flushPayloadEvents();
			
			// add the cluster network in the current network, initialize the "CnS:size" column and compute, for each node, the appropriate location
			Vector<DelayedVizProp> dvp = new Vector<DelayedVizProp>();
			for (CnSNode cnsnode : cluster.getNodes()) {
				if (!view.getView().getModel().containsNode(cnsnode.getCyNode())) {
					network.getNetwork().addNode(cnsnode.getCyNode());
					network.getNetwork().getRow(cnsnode.getCyNode()).set("CnS:size", null);
					x = (x0 - ratio * (x_max + x_min) / 2) + ratio * clusterView.getView().getNodeView(cnsnode.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					y = (y0 - ratio * (y_max + y_min) / 2) + ratio * clusterView.getView().getNodeView(cnsnode.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					dvp.addElement(new DelayedVizProp(cnsnode.getCyNode(), BasicVisualLexicon.NODE_X_LOCATION, x, false));
					dvp.addElement(new DelayedVizProp(cnsnode.getCyNode(), BasicVisualLexicon.NODE_Y_LOCATION, y, false));
				}
			}
			
			// fire all cytoscape events
			eh.flushPayloadEvents();
			
			// set the nodes locations
			DelayedVizProp.applyAll(view.getView(), dvp);
			
			// add cluster internal edges
			for (CnSEdge cnsedge : cluster.getEdges()) {
				// add the edge in the current network
				network.getNetwork().addEdge(cnsedge.getCyEdge());
				// fill CnS and cytoscape attributes
				for (String key : network.getEdgeColumns().keySet())
		        	network.getNetwork().getRow(cnsedge.getCyEdge()).set(key, cnsedge.getAttributes().get(key));
			}
			
			// fire all cytoscape events
			eh.flushPayloadEvents();
			
			// unselect the cluster node
			network.getNetwork().getRow(cluster.getCyNode()).set("selected", false);
			
			// register the initial cluster location in the view manager in order to restore it later if the cluster is recompressed 
			ev = new CnSEvent(CnSViewManager.SET_CLUSTER_LOCATION, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			ev.addParameter(CnSViewManager.CLUSTER, cluster);
			ev.addParameter(CnSViewManager.CLUSTER_LOCATION, new Point2D.Double(x0, y0));
			CnSEventManager.handleMessage(ev);
			
			// remove the cluster node from the current network
			Vector<CyNode> toRemove = new Vector<CyNode>();
			toRemove.addElement(node.getCyNode());
			network.getNetwork().removeNodes(toRemove);
			
			// fire all cytoscape events
			eh.flushPayloadEvents();
			
			// now, we must set the edges between the expanded nodes and the current network
			
			// browse all the partition cluster links
			for (CnSClusterLink cl : partition.getClusterLinks()) {
				// if the observed cluster link (cl) contains the expanded cluster, get the cluster linked with it (linkedCluster)
				linkedCluster = null;
				if (cl.getSource() == cluster)
					linkedCluster = cl.getTarget();	
				else if (cl.getTarget() == cluster)
					linkedCluster = cl.getSource();
				
				if (linkedCluster != null && view.getClusters().contains(linkedCluster)) {
					// get the expanded state of the linked cluster 
					ev = new CnSEvent(CnSViewManager.IS_EXPANDED, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, view);
					ev.addParameter(CnSViewManager.CLUSTER, linkedCluster);
					expanded = (Boolean)CnSEventManager.handleMessage(ev);
					
					// a hashmap to store the widths of the edges between the expanded nodes and the current network
					HashMap<CyEdge, Double> edgeWidth = new HashMap<CyEdge, Double>();
					
					// used to store an expanded node involved in a link
					CnSNode clusterNode = null;
					
					// used to store an edge to be added
					CyEdge edge = null;
					
					// used to store the the edges connected to a node 
					List<CyEdge> lce;
					
					// browse the edges contained in the observed cluster link
					for (CnSEdge ce : cl.getEdges())
						// if the linked cluster is expanded, we just have to add the edges and to fill the CnS attributes 
						if (expanded) {
							network.getNetwork().addEdge(ce.getCyEdge());
							for (String key : network.getEdgeColumns().keySet())
					        	network.getNetwork().getRow(ce.getCyEdge()).set(key, ce.getAttributes().get(key));
						}
						// if the linked cluster is not expanded, we must create all the edges between the expanded nodes and the linked cluster 
						else {
							// first, we identify the node (clusterNode) involved in the observed edge (ce)
							clusterNode = null;
							for (CnSNode n : cluster.getNodes())
								if ((n.getCyNode() == ce.getCyEdge().getSource()) || (n.getCyNode() == ce.getCyEdge().getTarget())) {
									clusterNode = n;
									break;
								}
							
							// if the edge between clusterNode and the linked cluster is not existing yet, we add it in the current network and set all the CnS and cytoscape attributes
							if (!network.getNetwork().containsEdge(linkedCluster.getCyNode(), clusterNode.getCyNode()) &&
									!network.getNetwork().containsEdge(clusterNode.getCyNode(), linkedCluster.getCyNode())) {
								// adding node-cluster interaction link
								edge = network.getNetwork().addEdge(linkedCluster.getCyNode(), clusterNode.getCyNode(), false);
								
								// initialize the created edge width in the hashmap  
								edgeWidth.putIfAbsent(edge, 1.0);
								
								// fill the CnS and cytoscape attributes
								network.getNetwork().getRow(edge).set("CnS:isInteraction", true);
								network.getNetwork().getRow(edge).set("CnS:size", edgeWidth.get(edge).intValue());
								network.getNetwork().getRow(edge).set("interaction", "pp");
								network.getNetwork().getRow(edge).set("name", clusterNode.getAttributes().get("name") + " - " + linkedCluster.getName());
								network.getNetwork().getRow(edge).set("shared name", clusterNode.getAttributes().get("name") + " - " + linkedCluster.getName());
							}
							// if the edge between clusterNode and the linked cluster already exists, we just increase its with
							else {
								// get the existing edge between the clusterNode and the linked cluster
								lce = network.getNetwork().getConnectingEdgeList(linkedCluster.getCyNode(), clusterNode.getCyNode(), CyEdge.Type.ANY);
								lce.addAll(network.getNetwork().getConnectingEdgeList(clusterNode.getCyNode(), linkedCluster.getCyNode(), CyEdge.Type.ANY));
								for (CyEdge e : lce)
									if ((e.getSource() == linkedCluster.getCyNode() && e.getTarget() == clusterNode.getCyNode()) ||
											(e.getSource() == clusterNode.getCyNode() && e.getTarget() == linkedCluster.getCyNode())) {
										edge = e;
										break;
									}
								
								// increase the edge's width
								if (edgeWidth.get(edge) != null) {
								
									edgeWidth.put(edge, edgeWidth.get(edge) + 1);
								}
								else {
									edgeWidth.putIfAbsent(edge, view.getView().getEdgeView(edge).getVisualProperty(BasicVisualLexicon.EDGE_WIDTH) + 1.0);
								}
								
								// set the CnS size attribute of the edge
								network.getNetwork().getRow(edge).set("CnS:size", edgeWidth.get(edge).intValue());
							}
						}
					
					// browse the shared nodes between the expanded cluster and the linked cluster 
					for (CnSNode n : cl.getNodes()) {
						// a new multiclass edge must be added only if the linked cluster is not expanded
						if (!expanded) {
							// as for interaction edges, we add a new edge if it doesn't already exist; its width is obviously 1 
							if (!network.getNetwork().containsEdge(linkedCluster.getCyNode(), n.getCyNode()) && 
									!network.getNetwork().containsEdge(n.getCyNode(), linkedCluster.getCyNode())) {
								// adding node-cluster multiclass link
								edge = network.getNetwork().addEdge(linkedCluster.getCyNode(), n.getCyNode(), false);
								eh.flushPayloadEvents();
								
								// fill the CnS and cytoscape attributes
								network.getNetwork().getRow(edge).set("CnS:isInteraction", false);
								network.getNetwork().getRow(edge).set("CnS:size", 1);
								network.getNetwork().getRow(edge).set("interaction", "multiclass");
								network.getNetwork().getRow(edge).set("name", n.getAttributes().get("name") + " ~ " + linkedCluster.getName());
								network.getNetwork().getRow(edge).set("shared name", n.getAttributes().get("name") + " ~ " + linkedCluster.getName());
								eh.flushPayloadEvents();
							}
						}
					}
				}
			}
			
			eh.flushPayloadEvents();
			
			// apply the CnS style to the current view
			ev = new CnSEvent(CnSStyleManager.SET_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
	        ev.addParameter(CnSStyleManager.STYLE, CnSStyleManager.CNS_STYLE);
	        CnSEventManager.handleMessage(ev);
			ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER);
			CnSEventManager.handleMessage(ev);
			view.getView().updateView();
			
			// register in the view manager that the cluster is now expanded
			ev = new CnSEvent(CnSViewManager.SET_EXPANDED, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.CLUSTER, cluster);
			ev.addParameter(CnSViewManager.VIEW, view);
			ev.addParameter(CnSViewManager.EXPANDED, true);
			CnSEventManager.handleMessage(ev);
			
			eh.flushPayloadEvents();
			
			// select the expanded nodes
			for (CnSNode cnsnode : cluster.getNodes()) network.getNetwork().getRow(cnsnode.getCyNode()).set("selected", true);
			eh.flushPayloadEvents();
			
			// clear the flag used to prevent listeners to be called during the cluster expansion process
			view.setModifCluster(false);
		}
	}
}
