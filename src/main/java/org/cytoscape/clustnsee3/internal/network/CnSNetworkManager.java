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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
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
	public static final int GET_NODES_WITH_VALUE = 6;
	public static final int SELECT_CLUSTER = 7;
			
	public static final int NETWORK = 1000;
	public static final int NETWORK_NAME = 1001;
	public static final int CLUSTER = 1002;
	public static final int COLNAME = 1003;
	public static final int VALUE = 1004;
	
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
	
	public String getActionName(int k) {
		switch(k) {
			case ADD_NETWORK : return "ADD_NETWORK";
			case RENAME_NETWORK : return "RENAME_NETWORK";
			case REMOVE_NETWORK : return "REMOVE_NETWORK";
			case GET_NETWORK : return "GET_NETWORK";
			case SET_NETWORK_CLUSTER : return "SET_NETWORK_CLUSTER";
			case GET_NODES_WITH_VALUE : return "GET_NODES_WITH_VALUE";
			case SELECT_CLUSTER : return "SELECT_CLUSTER";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case NETWORK : return "NETWORK";
			case NETWORK_NAME : return "NETWORK_NAME";
			case CLUSTER : return "CLUSTER";
			case COLNAME : return "COLNAME";
			case VALUE : return "VALUE";
			default : return "UNDEFINED_PARAMETER";
		}
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
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
		Object value;
		CnSNetwork network = null;
		CnSCluster cluster;
		CyNetwork cyNetwork;
		String colName;
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
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
					ret = new CnSEventResult<CnSNetwork>(getNetwork(cyNetwork));
				else {
					cluster = (CnSCluster)event.getParameter(CLUSTER);
					if (cluster != null)
						ret = new CnSEventResult<CnSNetwork>(cluster2networkMap.get(cluster));
				}
				break;
				
			case SET_NETWORK_CLUSTER :
				network = (CnSNetwork)event.getParameter(NETWORK);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				network2clusterMap.putIfAbsent(network, cluster);
				cluster2networkMap.putIfAbsent(cluster, network);
				break;
				
			case GET_NODES_WITH_VALUE :
				cyNetwork = (CyNetwork)event.getParameter(NETWORK);
				colName = (String)event.getParameter(COLNAME);
				value = event.getParameter(VALUE);
				ret = new CnSEventResult<Set<CyNode>>(getNodesWithValue(cyNetwork, cyNetwork.getDefaultNodeTable(), colName, value));
				break;
				
			case SELECT_CLUSTER :
				network = (CnSNetwork)event.getParameter(NETWORK);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				
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
				CnSEvent ev= new CnSEvent(CnSNetworkManager.REMOVE_NETWORK, CnSEventManager.NETWORK_MANAGER, this.getClass());
				ev.addParameter(NETWORK, getNetwork(e.getNetwork()));
				cnsEventOccured(ev, true);
			}
	}
	
	private CnSNetwork getNetwork(CyNetwork n) {
		CnSNetwork ret = null;
		
		for (CnSNetwork cnsn : networks) {
			if (cnsn.getNetwork() == n) {
				ret = cnsn;
				break;
			}
			else if (cnsn.getBaseNetwork() == n) {
				ret = cnsn;
				break;
			}
		}
		return ret;
	}
	
	/**
     * Get all the nodes with a given attribute value.
     *
     * This method is effectively a wrapper around {@link CyTable#getMatchingRows}.
     * It converts the table's primary keys (assuming they are node SUIDs) back to
     * nodes in the network.
     *
     * Here is an example of using this method to find all nodes with a given name:
     *
     * {@code
     *   CyNetwork net = ...;
     *   String nodeNameToSearchFor = ...;
     *   Set<CyNode> nodes = getNodesWithValue(net, net.getDefaultNodeTable(), "name", nodeNameToSearchFor);
     *   // nodes now contains all CyNodes with the name specified by nodeNameToSearchFor
     * }
     * @param net The network that contains the nodes you are looking for.
     * @param table The node table that has the attribute value you are looking for;
     * the primary keys of this table <i>must</i> be SUIDs of nodes in {@code net}.
     * @param colname The name of the column with the attribute value
     * @param value The attribute value
     * @return A set of {@code CyNode}s with a matching value, or an empty set if no nodes match.
     */
    private static Set<CyNode> getNodesWithValue(final CyNetwork net, final CyTable table, final String colname, final Object value) {
        final Collection<CyRow> matchingRows = table.getMatchingRows(colname, value);
        final Set<CyNode> nodes = new HashSet<CyNode>();
        final String primaryKeyColname = table.getPrimaryKey().getName();
        for (final CyRow row : matchingRows) {
            final Long nodeId = row.get(primaryKeyColname, Long.class);
            if (nodeId == null) continue;
            final CyNode node = net.getNode(nodeId);
            if (node == null) continue;
            nodes.add(node);
        }
        return nodes;
    }
}
