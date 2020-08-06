/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.network;

import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

/**
 * 
 */
public class CnSNetworkManager implements CnSEventListener, NetworkAboutToBeDestroyedListener {
	public static final int ADD_NETWORK = 1;
	public static final int RENAME_NETWORK = 2;
	public static final int REMOVE_NETWORK = 3;
	public static final int GET_NETWORK = 4;
	public static final int SET_NETWORK_CLUSTER = 5;
			
	public static final int NETWORK = 1000;
	public static final int NETWORK_NAME = 1001;
	public static final int CLUSTER = 1002;
	
	private Vector<CnSNetwork> networks;
	private HashMap<CnSNetwork, CnSCluster> network2clusterMap;
	private HashMap<CnSCluster, CnSNetwork> cluster2networkMap;
	
	private static CnSNetworkManager instance = null;
	
	/**
	 * @param
	 * @return
	 */
	private CnSNetworkManager() {
		super();
		networks = new Vector<CnSNetwork>();
		network2clusterMap = new HashMap<CnSNetwork, CnSCluster>();
		cluster2networkMap = new HashMap<CnSCluster, CnSNetwork>();
	}
	
	public static CnSNetworkManager getInstance() {
		if (instance == null) {
			instance = new CnSNetworkManager();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		CnSNetwork network = null;
		CnSCluster cluster;
		CyNetwork cyNetwork;
		
		switch (event.getAction()) {
			case ADD_NETWORK :
				network = (CnSNetwork)event.getParameter(NETWORK);
				if (!networks.contains(network)) networks.addElement(network);
				break;
			
			case RENAME_NETWORK :
				network = (CnSNetwork)event.getParameter(NETWORK);
				String networkName = (String)event.getParameter(NETWORK_NAME);
				network.getNetwork().getRow(network.getNetwork()).set(CyNetwork.NAME, networkName);
				break;
				
			case REMOVE_NETWORK :
				network = (CnSNetwork)event.getParameter(NETWORK);
				networks.removeElement(network);
				for (CnSNetwork n : cluster2networkMap.values())
					if (n == network) {
						cluster2networkMap.remove(network2clusterMap.get(n));
						break;
					}
				
				network2clusterMap.remove(network);
				for (CnSNetwork net : networks)
					for (CyNode cn : net.getNetwork().getNodeList())
						if (cn.getNetworkPointer() == network.getNetwork())
							cn.setNetworkPointer(null);
				break;
				
			case GET_NETWORK :
				cyNetwork = (CyNetwork)event.getParameter(NETWORK);
				if (cyNetwork != null)
					ret = getNetwork(cyNetwork);
				else {
					cluster = (CnSCluster)event.getParameter(CLUSTER);
					if (cluster != null)
						ret = cluster2networkMap.get(cluster);
				}
				break;
				
			case SET_NETWORK_CLUSTER :
				network = (CnSNetwork)event.getParameter(NETWORK);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				network2clusterMap.putIfAbsent(network, cluster);
				cluster2networkMap.putIfAbsent(cluster, network);
				break;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener#handleEvent(org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent)
	 */
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		if (e.getNetwork() != null)
			if (getNetwork(e.getNetwork()) != null) {
				CnSEvent ev= new CnSEvent(CnSNetworkManager.REMOVE_NETWORK, CnSEventManager.NETWORK_MANAGER);
				ev.addParameter(NETWORK, getNetwork(e.getNetwork()));
				cnsEventOccured(ev);
			}
	}
	
	private CnSNetwork getNetwork(CyNetwork n) {
		CnSNetwork ret = null;
		
		for (CnSNetwork cnsn : networks) {
			if (cnsn.getNetwork() == n) {
				ret = cnsn;
				break;
			}
		}
		return ret;
	}
}
