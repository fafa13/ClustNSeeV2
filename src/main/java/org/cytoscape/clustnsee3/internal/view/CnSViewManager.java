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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.events.SetSelectedNetworkViewsEvent;
import org.cytoscape.application.events.SetSelectedNetworkViewsListener;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSUserViewState;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.events.UnsetNetworkPointerEvent;
import org.cytoscape.model.events.UnsetNetworkPointerListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;

/**
 * 
 */
public class CnSViewManager implements CnSEventListener, RemovedNodesListener, AddedNodesListener, 
RemovedEdgesListener, AddedEdgesListener, NetworkViewAboutToBeDestroyedListener, 
UnsetNetworkPointerListener, SetSelectedNetworkViewsListener, RowsSetListener, SetCurrentNetworkViewListener {
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
	
	public static final int VIEW = 1000;
	public static final int STATE = 1001;
	public static final int REFERENCE = 1002;
	public static final int CLUSTER = 1003;
	public static final int NETWORK = 1004;
	public static final int EXPANDED = 1005;
	
	private Vector<CnSView> views;
	private CnSView selectedView;
	private HashMap<CnSView, CnSNetwork> view2networkMap;
	private HashMap<CnSNetwork, CnSView> network2viewMap;
	private HashMap<CnSView, CnSCluster> view2clusterMap;
	private HashMap<CnSCluster, CnSView> cluster2viewMap;
	
	private static CnSViewManager instance = null;
	
	private CnSViewManager() {
		super();
		views = new Vector<CnSView>();
		selectedView = null;
		view2networkMap = new HashMap<CnSView, CnSNetwork>();
		network2viewMap = new HashMap<CnSNetwork, CnSView>();
		view2clusterMap = new HashMap<CnSView, CnSCluster>();
		cluster2viewMap = new HashMap<CnSCluster, CnSView>();
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
		Object ret = null, reference;
		CnSView view;
		CnSNetwork network;
		CnSViewState state = null;
		CnSCluster cluster;
		boolean expanded;
		
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
				reference = event.getParameter(REFERENCE); // partition, cluster, null
				if (reference != null) {
					for (CnSView v : views)
						if (v.getReference() != null)
							if (v.getReference() == reference) {
								ret = v;
								break;
							}
				}
				else {
					network = (CnSNetwork)event.getParameter(NETWORK);
					if (network != null)
						ret = network2viewMap.get(network);
					else {
						cluster = (CnSCluster)event.getParameter(CLUSTER);
						if (cluster != null)
							ret = cluster2viewMap.get(cluster);
					}
				}
				break;
					
			case SELECT_CLUSTER :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				for (CnSView v : views) {
					if (v != null) {
						if (v.getView().getNodeView(cluster.getCyNode()) != null) {
							Collection<CyRow> matchingRows = view2networkMap.get(v).getNetwork().getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).getMatchingRows("selected", true);
							for (CyRow row : matchingRows) row.set("selected", false);
							view2networkMap.get(v).getNetwork().getRow(cluster.getCyNode()).set("selected", true);
						}
					}
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
        	if (!cnsv.getModifCluster()) {
        		cnsv.setViewState(new CnSUserViewState());
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
	 * @see org.cytoscape.model.events.RemovedNodesListener#handleEvent(org.cytoscape.model.events.RemovedNodesEvent)
	 */
	@Override
	public void handleEvent(RemovedNodesEvent e) {
		eventOccured(e);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.NetworkAboutToBeDestroyedListener#handleEvent(org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent)
	 */
	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		CnSEvent ev= new CnSEvent(CnSViewManager.DELETE_VIEW, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(VIEW, getView(e.getNetworkView()));
		cnsEventOccured(ev);
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
					v.setViewState(new CnSUserViewState());
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
	 * @see org.cytoscape.model.events.RowsSetListener#handleEvent(org.cytoscape.model.events.RowsSetEvent)
	 */
	@Override
	public void handleEvent(RowsSetEvent e) {
		Collection<RowSetRecord> rsr = e.getColumnRecords("selected");
		String primaryKeyColname = e.getSource().getPrimaryKey().getName();
		if (selectedView != null) {
			List<CyNode> cn = CyTableUtil.getNodesInState(view2networkMap.get(selectedView).getNetwork(), "selected", true);
			if (cn.size() == 1) {
				for (RowSetRecord r : rsr) {
					Long nodeId = r.getRow().get(primaryKeyColname, Long.class);
					if (nodeId == null) continue;
					CyNode node = view2networkMap.get(selectedView).getNetwork().getNode(nodeId);
					if (node != null)
						if ((Boolean)r.getRawValue() == true) {
							CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
							ev.addParameter(CnSResultsPanel.CLUSTER, nodeId);
							CnSEventManager.handleMessage(ev);
						}
				}
			}
			else {
				CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
				//ev.addParameter(CnSResultsPanel.CLUSTER, null);
				CnSEventManager.handleMessage(ev);
			}
		}
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
}
