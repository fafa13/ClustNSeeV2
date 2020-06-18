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
import java.util.Vector;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSUserViewState;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.model.events.UnsetNetworkPointerEvent;
import org.cytoscape.model.events.UnsetNetworkPointerListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.ViewChangedEvent;

/**
 * 
 */
public class CnSViewManager implements CnSEventListener, RemovedNodesListener, AddedNodesListener, 
RemovedEdgesListener, AddedEdgesListener, NetworkViewAboutToBeDestroyedListener, UnsetNetworkPointerListener {
	public static final int ADD_VIEW = 1;
	public static final int DELETE_VIEW = 2;
	public static final int SET_SELECTED_VIEW = 3;
	public static final int GET_SELECTED_VIEW = 4;
	public static final int SET_STATE = 5;
	public static final int GET_VIEW = 6;
	public static final int SELECT_CLUSTER = 7;
	
	public static final int VIEW = 1000;
	public static final int STATE = 1001;
	public static final int REFERENCE = 1002;
	public static final int CLUSTER = 1003;
	
	private Vector<CnSView> views;
	private CnSView selectedView;
	private static CnSViewManager instance = null;
	
	private CnSViewManager() {
		super();
		views = new Vector<CnSView>();
		selectedView = null;
	}
	
	public static CnSViewManager getInstance() {
		if (instance == null) {
			instance = new CnSViewManager();
		}
		return instance;
	}
	
	private CnSView getView(CyNetworkView v) {
		CnSView ret = null;
		
		for (CnSView cnsv : views) {
			if (cnsv.getView() == v) {
				ret = cnsv;
				break;
			}
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
		CnSViewState state = null;
		switch(event.getAction()) {
			case ADD_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				if (!views.contains(view)) {
					views.addElement(view);
					
				}
				break;
				
			case DELETE_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				views.removeElement(view);
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
				Object reference = event.getParameter(REFERENCE);
				if (reference != null)
					for (CnSView v : views)
						if (v.getReference() != null)
							if (v.getReference().equals(reference)) {
								ret = v;
								break;
							}
				break;
				
			case SELECT_CLUSTER :
				CnSCluster cluster = (CnSCluster)event.getParameter(CLUSTER);
				for (CnSView v : views) {
					if (v.getView().getNodeView(cluster.getCyNode()) != null) {
						Collection<CyRow> matchingRows = v.getNetwork().getNetwork().getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).getMatchingRows("selected", true);
						for (CyRow row : matchingRows) row.set("selected", false);
						v.getNetwork().getNetwork().getRow(cluster.getCyNode()).set("selected", true);
					}
				}
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
        	cnsv.setViewState(new CnSUserViewState());
        	ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
        	ev.addParameter(CnSNetworkManager.NETWORK, cnsv.getNetwork());
        	ev.addParameter(CnSNetworkManager.NETWORK_NAME, "Copy of " + cnsv.getNetwork().getName());
        	CnSEventManager.handleMessage(ev);
        }
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.view.model.events.ViewChangedListener#handleEvent(org.cytoscape.view.model.events.ViewChangedEvent)
	 */
//	@Override
	public void handleEvent(ViewChangedEvent<?> e) {
		JOptionPane.showMessageDialog(null, e.getSource() + " has changed !");
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
		CnSView cnsv = getView(e.getNetworkView());
        if (cnsv != null) views.removeElement(cnsv);
    }

	/* (non-Javadoc)
	 * @see org.cytoscape.model.events.UnsetNetworkPointerListener#handleEvent(org.cytoscape.model.events.SetNetworkPointerEvent)
	 */
	@Override
	public void handleEvent(UnsetNetworkPointerEvent e) {
		CnSEvent ev = new CnSEvent(CnSNetworkManager.RENAME_NETWORK, CnSEventManager.NETWORK_MANAGER);
		for (CnSView v : views) {
			if (v.getView().getNodeView(e.getNode()) != null) {
				if (!v.isUserView()) {
					v.setViewState(new CnSUserViewState());
					ev.addParameter(CnSNetworkManager.NETWORK, v.getNetwork());
					ev.addParameter(CnSNetworkManager.NETWORK_NAME, "Copy of " + v.getNetwork().getName());
					CnSEventManager.handleMessage(ev);
				}
			}
		}
	}
}
