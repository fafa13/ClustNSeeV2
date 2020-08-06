/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.view;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.events.SetSelectedNetworkViewsEvent;
import org.cytoscape.application.events.SetSelectedNetworkViewsListener;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.info.CnSInfoPanel;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.action.CnSExpandClusterNodeAction;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSUserViewState;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.AboutToRemoveNodesEvent;
import org.cytoscape.model.events.AboutToRemoveNodesListener;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.model.events.UnsetNetworkPointerEvent;
import org.cytoscape.model.events.UnsetNetworkPointerListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * 
 */
public class CnSViewManager implements CnSEventListener, AboutToRemoveNodesListener, AddedNodesListener, 
RemovedEdgesListener, AddedEdgesListener, NetworkViewAboutToBeDestroyedListener, 
UnsetNetworkPointerListener, SetSelectedNetworkViewsListener, SelectedNodesAndEdgesListener, SetCurrentNetworkViewListener {
	public static final int ADD_VIEW = 1;
	public static final int DELETE_VIEW = 2;
	public static final int SET_SELECTED_VIEW = 3;
	public static final int GET_SELECTED_VIEW = 4;
	public static final int SET_STATE = 5;
	public static final int GET_VIEW = 6;
	public static final int SELECT_CLUSTER = 7;
	public static final int GET_NETWORK = 8;
	public static final int IS_EXPANDED = 9;
	public static final int SET_EXPANDED = 10;
	public static final int GET_CLUSTER_LOCATION = 11;
	public static final int SET_CLUSTER_LOCATION = 12;
	public static final int RECORD_CLUSTERS_LOCATION = 13;
	public static final int SET_VIEW_PARTITION = 14;
	public static final int GET_VIEW_PARTITION = 15;
	public static final int EXPAND_CLUSTER = 16;
	public static final int GET_CLUSTER_FROM_CY_NODE = 17;
	public static final int REMOVE_VIEW = 18;
	public static final int REMOVE_VIEWS = 19;
	
	public static final int VIEW = 1000;
	public static final int STATE = 1001;
	public static final int REFERENCE = 1002;
	public static final int CLUSTER = 1003;
	public static final int NETWORK = 1004;
	public static final int EXPANDED = 1005;
	public static final int CLUSTER_LOCATION = 1006;
	public static final int PARTITION = 1007;
	public static final int SUID = 1008;
	public static final int CY_NODE = 1009;
	
	private Vector<CnSView> views;
	private CnSView selectedView;
	private HashMap<CnSView, CnSNetwork> view2networkMap;
	private HashMap<CnSNetwork, CnSView> network2viewMap;
	private HashMap<CnSView, CnSCluster> view2clusterMap;
	private HashMap<CnSCluster, CnSView> cluster2viewMap;
	private HashMap<CnSView, CnSPartition> view2partitionMap;
	private HashMap<CnSPartition, CnSView> partition2viewMap;
	
	private static CnSViewManager instance = null;
	
	private CnSViewManager() {
		super();
		views = new Vector<CnSView>();
		selectedView = null;
		view2networkMap = new HashMap<CnSView, CnSNetwork>();
		network2viewMap = new HashMap<CnSNetwork, CnSView>();
		view2clusterMap = new HashMap<CnSView, CnSCluster>();
		cluster2viewMap = new HashMap<CnSCluster, CnSView>();
		view2partitionMap = new HashMap<CnSView, CnSPartition>();
		partition2viewMap = new HashMap<CnSPartition, CnSView>();
	}
	
	public static CnSViewManager getInstance() {
		if (instance == null) {
			instance = new CnSViewManager();
		}
		return instance;
	}
	
	private CnSView getView(CyNetworkView v) {
		CnSView ret = null;
		
		for (CnSView cnsv : views)
			if (cnsv.getView() == v) {
				ret = cnsv;
				break;
			}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		CnSView view;
		CnSNetwork network;
		CnSViewState state = null;
		CnSCluster cluster;
		boolean expanded;
		Double x, y;
		Point2D.Double location;
		CnSEvent ev;
		CnSPartition partition;
		View<CyNode> cnv;
		CyNode cyNode;
		
		switch(event.getAction()) {
			case ADD_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				network = (CnSNetwork)event.getParameter(NETWORK);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				if (!views.contains(view)) {
					views.addElement(view);
					network2viewMap.putIfAbsent(network, view);
					view2networkMap.putIfAbsent(view, network);
					if (cluster != null) {
						view2clusterMap.putIfAbsent(view, cluster);
						cluster2viewMap.putIfAbsent(cluster, view);
					}
				}
				break;
				
			case DELETE_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				views.removeElement(view);
				for (CnSView v : network2viewMap.values())
					if (view == v) {
						network2viewMap.remove(view2networkMap.get(v));
						break;
					}
				view2networkMap.remove(view);
				for (CnSView v : cluster2viewMap.values())
					if (view == v) {
						cluster2viewMap.remove(view2clusterMap.get(v));
						break;
					}
				view2clusterMap.remove(view);
				break;
				
			case SET_SELECTED_VIEW :
				selectedView = (CnSView)event.getParameter(VIEW);
				break;
				
			case GET_SELECTED_VIEW :
				ret = selectedView;
				break;
			
			case SET_STATE :
				view = (CnSView)event.getParameter(VIEW);
				state = (CnSViewState)event.getParameter(STATE);
				view.setViewState(state);
				break;
				
			case GET_VIEW :
				ret = getView(event.getParameter(REFERENCE), (CnSNetwork)event.getParameter(NETWORK), (CnSCluster)event.getParameter(CLUSTER));
				break;
					
			case SELECT_CLUSTER :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				if (cluster == null) {
					Collection<CyRow> matchingRows = view2networkMap.get(selectedView).getNetwork().getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).getMatchingRows("selected", true);
					if (matchingRows.size() > 0)
						for (CyRow row : matchingRows) 
							if (row.get("CnS:isCluster",  Boolean.class) == true) row.set("selected", false);
				}
				else if (cluster.getCyNode() != null)
					for (CnSView v : views)
						if (v != null)
							if (v.getView().getNodeView(cluster.getCyNode()) != null) {
								Collection<CyRow> matchingRows = view2networkMap.get(v).getNetwork().getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).getMatchingRows("selected", true);
								if (matchingRows.size() > 0)
									for (CyRow row : matchingRows) row.set("selected", false);
								view2networkMap.get(v).getNetwork().getRow(cluster.getCyNode()).set("selected", true);
							}
				break;
				
			case GET_NETWORK :
				view = (CnSView)event.getParameter(VIEW);
				ret = view2networkMap.get(view);
				break;
				
			case SET_EXPANDED :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				view = (CnSView)event.getParameter(VIEW);
				expanded = (Boolean)event.getParameter(EXPANDED);
				view.setExpanded(cluster, expanded);
				break;
				
			case IS_EXPANDED :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				view = (CnSView)event.getParameter(VIEW);
				ret = view.isExpanded(cluster);
				break;
				
			case GET_CLUSTER_LOCATION :
				view = (CnSView)event.getParameter(VIEW);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				ret = view.getClusterLocation(cluster);
				break;
				
			case SET_CLUSTER_LOCATION :
				view = (CnSView)event.getParameter(VIEW);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				location = (Point2D.Double)event.getParameter(CLUSTER_LOCATION);
				view.setLocation(cluster, location.x, location.y);
				break;
				
			case RECORD_CLUSTERS_LOCATION :
				view = (CnSView)event.getParameter(VIEW);
				ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.VIEW, view);
				partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				if (partition == null) {
					ev = new CnSEvent(CnSViewManager.GET_VIEW_PARTITION, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.VIEW, view);
					partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				}
				for (CnSCluster cl : partition.getClusters()) {
					if (cl.getCyNode() != null) { 
						cnv = view.getView().getNodeView(cl.getCyNode());
						if (cnv != null) {
							x = cnv.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
							y = cnv.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
							view.setLocation(cl, x, y);
						}
					}
				}
				break;
				
			case SET_VIEW_PARTITION :
				view = (CnSView)event.getParameter(VIEW);
				partition = (CnSPartition)event.getParameter(PARTITION);
				view2partitionMap.putIfAbsent(view, partition);
				partition2viewMap.putIfAbsent(partition, view);
				break;
				
			case GET_VIEW_PARTITION :
				view = (CnSView)event.getParameter(VIEW);
				ret = view2partitionMap.get(view);
				break;
				
			case EXPAND_CLUSTER :
				Long suid = (Long)event.getParameter(SUID);
				CnSExpandClusterNodeAction action = new CnSExpandClusterNodeAction();
				action.doAction(suid);
				break;
				
			case GET_CLUSTER_FROM_CY_NODE:
				cyNode = (CyNode)event.getParameter(CY_NODE);
				if (selectedView.getView().getNodeView(cyNode) != null) {
					for (CnSCluster cnsCluster : selectedView.getClusters()) {
						if (cnsCluster.getCyNode() == cyNode) {
							ret = cnsCluster;
							break;
						}
					}
				}
				break;
				
			case REMOVE_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				network = (CnSNetwork)event.getParameter(NETWORK);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partition = (CnSPartition)event.getParameter(PARTITION);
				if (views.contains(view)) {
					views.removeElement(view);
					network2viewMap.remove(network, view);
					view2networkMap.remove(view, network);
					
					if (cluster != null) {
						view2clusterMap.remove(view, cluster);
						cluster2viewMap.remove(cluster, view);
					}
					else {
						view2clusterMap.remove(cluster2viewMap.get(cluster));
						cluster2viewMap.remove(cluster);
					}
					if (partition != null) {
						partition2viewMap.remove(partition, view);
						view2partitionMap.remove(view, partition);
					}
				}
				break;
				
			case REMOVE_VIEWS :
				partition = (CnSPartition)event.getParameter(REFERENCE);
				Vector<CnSView> deleted_views = new Vector<CnSView>();
				for (CnSView v : views) {
					//System.err.print("removing " + v + " ... ");
					if (v.getReference() == partition) {
						//System.err.println("yes");
						deleted_views.addElement(v);
						partition2viewMap.remove(partition, v);
						view2partitionMap.remove(v, partition);
					}
					//else
						//System.err.println("no");
				}
				views.removeAll(deleted_views);
				ret = deleted_views;
				break;
		}
		return ret;
	}
	
	private void eventOccured(AbstractCyEvent<CyNetwork> e) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
        Collection<CyNetworkView> views = networkViewManager.getNetworkViews(e.getSource());
        CnSView cnsv = null;
        for (CyNetworkView v : views) {
        	cnsv = getView(v);
        	if (cnsv != null) break;
        }
        if (cnsv != null) {
        	
        	if (!cnsv.isUserView() && !cnsv.getModifCluster()) {
        		Vector<CnSCluster> cls = cnsv.getState().getClusters();
        		cnsv.setViewState(new CnSUserViewState(cnsv.getReference()));
        		cnsv.getState().getClusters().addAll(cls);
        		ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
        		ev.addParameter(CnSNetworkManager.NETWORK, view2networkMap.get(cnsv));
        		ev.addParameter(CnSNetworkManager.NETWORK_NAME, "Copy of " + view2networkMap.get(cnsv).getName());
        		CnSEventManager.handleMessage(ev);
        		
        	}	
        }
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.AddedEdgesListener#handleEvent(org.cytoscape.model.events.AddedEdgesEvent)
	 */
	@Override
	public void handleEvent(AddedEdgesEvent e) {
		eventOccured(e);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.RemovedEdgesListener#handleEvent(org.cytoscape.model.events.RemovedEdgesEvent)
	 */
	@Override
	public void handleEvent(RemovedEdgesEvent e) {
		eventOccured(e);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.AddedNodesListener#handleEvent(org.cytoscape.model.events.AddedNodesEvent)
	 */
	@Override
	public void handleEvent(AddedNodesEvent e) {
		eventOccured(e);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.NetworkAboutToBeDestroyedListener#handleEvent(org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent)
	 */
	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		if (getView(e.getNetworkView()) != null) {
			CnSEvent ev= new CnSEvent(CnSViewManager.DELETE_VIEW, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(VIEW, getView(e.getNetworkView()));
			cnsEventOccured(ev);
		}
    }

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.UnsetNetworkPointerListener#handleEvent(org.cytoscape.model.events.SetNetworkPointerEvent)
	 */
	@Override
	public void handleEvent(UnsetNetworkPointerEvent e) {
		CnSEvent ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
		for (CnSView v : views) {
			if (v.getView().getNodeView(e.getNode()) != null) {
				if (!v.isUserView() && !v.getModifCluster()) {
					Vector<CnSCluster> cls = v.getState().getClusters();
	        		v.setViewState(new CnSUserViewState(v.getReference()));
	        		v.getState().getClusters().addAll(cls);
					ev.addParameter(CnSNetworkManager.NETWORK, view2networkMap.get(v));
					ev.addParameter(CnSNetworkManager.NETWORK_NAME, "Copy of " + view2networkMap.get(v).getName());
					CnSEventManager.handleMessage(ev);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.events.SetSelectedNetworkViewsListener#handleEvent(org.cytoscape.application.events.SetSelectedNetworkViewsEvent)
	 */
	@Override
	public void handleEvent(SetSelectedNetworkViewsEvent e) {
		if (e.getNetworkViews().size() > 0) 
			selectedView = getView(e.getNetworkViews().get(0));
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.events.SetCurrentNetworkViewListener#handleEvent(org.cytoscape.application.events.SetCurrentNetworkViewEvent)
	 */
	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {
		if (e.getNetworkView() != null) {
			selectedView = getView(e.getNetworkView());
			if (selectedView != null) selectedView.updateNodeContextMenu();
		}
		else
			selectedView = null;
	}
	
	private CnSView getView(Object reference, CnSNetwork network, CnSCluster cluster) {
		CnSView ret = null;
		
		if (reference != null) {
			for (CnSView v : views)
				if (v.getReference() != null)
					if (v.getReference() == reference) {
						ret = v;
						break;
					}
		}
		else {
			if (network != null)
				ret = network2viewMap.get(network);
			else {
				if (cluster != null)
					ret = cluster2viewMap.get(cluster);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.AboutToRemoveNodesListener#handleEvent(org.cytoscape.model.events.AboutToRemoveNodesEvent)
	 */
	@Override
	public void handleEvent(AboutToRemoveNodesEvent e) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
        Collection<CyNetworkView> views = networkViewManager.getNetworkViews(e.getSource());
        CnSView cnsv = null;
        for (CyNetworkView v : views) {
        	cnsv = getView(v);
        	if (cnsv != null) break;
        }
        if (cnsv != null) {
        	for (CyNode cyNode : e.getNodes()) {
        		CnSCluster cluster = null; 
        		for (CnSCluster cnsCluster : selectedView.getClusters())
					if (cnsCluster.getCyNode() == cyNode) {
						cluster = cnsCluster;
						break;
					}
        		if (!cnsv.getModifCluster() && cluster != null) cnsv.removeCluster(cluster);
        	}
        	if (!cnsv.isUserView() && !cnsv.getModifCluster()) {
        		Vector<CnSCluster> cls = cnsv.getState().getClusters();
        		cnsv.setViewState(new CnSUserViewState(cnsv.getReference()));
        		cnsv.getState().getClusters().addAll(cls);
        		ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
        		ev.addParameter(CnSNetworkManager.NETWORK, view2networkMap.get(cnsv));
        		ev.addParameter(CnSNetworkManager.NETWORK_NAME, "Copy of " + view2networkMap.get(cnsv).getName());
        		CnSEventManager.handleMessage(ev);
        	}	
        }
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.SelectedNodesAndEdgesListener#handleEvent(org.cytoscape.model.events.SelectedNodesAndEdgesEvent)
	 */
	@Override
	public void handleEvent(SelectedNodesAndEdgesEvent e) {
		if (selectedView != null) {
			Collection<CyNode> cn = e.getSelectedNodes();
			if (cn.size() == 1) {
				for (CyNode node : cn) {
					CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.VIEW, selectedView);
					CnSPartition p = view2partitionMap.get(selectedView);
					if (p == null) p = (CnSPartition)CnSEventManager.handleMessage(ev);
					
					ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER_NODE, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.PARTITION, p);
					ev.addParameter(CnSPartitionManager.CY_NODE, node);
					CnSNode cnsn = (CnSNode)CnSEventManager.handleMessage(ev);
					
					if (cnsn != null) {
						ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
						ev.addParameter(CnSResultsPanel.CLUSTER, node.getSUID());
						CnSEventManager.handleMessage(ev);
					}
					else {
						ev = new CnSEvent(CnSPartitionManager.GET_NODE, CnSEventManager.PARTITION_MANAGER);
						ev.addParameter(CnSPartitionManager.CY_NODE, node);
						cnsn = (CnSNode)CnSEventManager.handleMessage(ev);
						if (cnsn != null) {
							ev = new CnSEvent(CnSInfoPanel.INIT, CnSEventManager.INFO_PANEL);
							ev.addParameter(CnSInfoPanel.NODE, cnsn);
							ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.NODE_DETAILS);
							CnSEventManager.handleMessage(ev);
							
							ev = new CnSEvent(CnSInfoPanel.SELECT_PANEL, CnSEventManager.INFO_PANEL);
							ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.NODE_DETAILS);
							CnSEventManager.handleMessage(ev);
						}
					}
				}
			}
			else if (cn.size() >= 2 || cn.size() == 0) {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
				CnSEventManager.handleMessage(ev);
				ev = new CnSEvent(CnSInfoPanel.CLEAR, CnSEventManager.INFO_PANEL);
				ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.NODE_DETAILS);
				CnSEventManager.handleMessage(ev);
			}
			
			Collection<CyEdge> ce = e.getSelectedEdges();
			if (ce.size() == 1) {
				for (CyEdge edge : ce) {
					CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER_LINK, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.CY_EDGE, edge);
					CnSClusterLink clusterLink = (CnSClusterLink)CnSEventManager.handleMessage(ev);
					
					ev = new CnSEvent(CnSInfoPanel.INIT, CnSEventManager.INFO_PANEL);
					ev.addParameter(CnSInfoPanel.EDGE, edge);
					if (clusterLink != null) ev.addParameter(CnSInfoPanel.CLUSTER_LINK, clusterLink);
					ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.EDGE_DETAILS);
					ev.addParameter(CnSInfoPanel.VIEW, selectedView);
					ev.addParameter(CnSInfoPanel.NETWORK, view2networkMap.get(selectedView));
					CnSEventManager.handleMessage(ev);
								
					ev = new CnSEvent(CnSInfoPanel.SELECT_PANEL, CnSEventManager.INFO_PANEL);
					ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.EDGE_DETAILS);
					CnSEventManager.handleMessage(ev);
				}
			}
			else if (ce.size() >= 2 || ce.size() == 0) {
				CnSEvent ev = new CnSEvent(CnSInfoPanel.CLEAR, CnSEventManager.INFO_PANEL);
				ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.EDGE_DETAILS);
				CnSEventManager.handleMessage(ev);
			}
		}
	}
}
