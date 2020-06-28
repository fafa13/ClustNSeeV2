/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date Nov 12, 2018
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.partition;

import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSPartitionManager implements CnSEventListener {
	public static final int ADD_PARTITON = 1;
	public static final int GET_PARTITION = 2;
	public static final int SET_PARTITION_NETWORK = 3;
	public static final int GET_CLUSTER_NODE = 4;
	public static final int GET_NODE = 5;
	public static final int SET_PARTITION_VIEW = 6;
	public static final int GET_VIEW = 7;
	public static final int REMOVE_PARTITION = 8;
	
	public static final int PARTITION = 1000;
	public static final int INDEX = 1001;
	public static final int NETWORK = 1002;
	public static final int NODE_SUID = 1003;
	public static final int VIEW = 1004;
	public static final int CLUSTER = 1005;
	public static final int CY_NODE = 1006;
	
	private static CnSPartitionManager instance;
	private Vector<CnSPartition> partitions;
	private HashMap<CnSPartition, CnSView> partition2viewMap;
	private HashMap<CnSView, CnSPartition> view2partitionMap;
	private HashMap<CnSPartition, CnSNetwork> partition2networkMap;
	private HashMap<CnSNetwork, CnSPartition> network2partitionMap;
	
	/**
	 * @param
	 * @return
	 */
	private CnSPartitionManager() {
		super();
		partitions = new Vector<CnSPartition>();
		partition2viewMap = new HashMap<CnSPartition, CnSView>();
		view2partitionMap = new HashMap<CnSView, CnSPartition>();
		partition2networkMap = new HashMap<CnSPartition, CnSNetwork>();
		network2partitionMap = new HashMap<CnSNetwork, CnSPartition>();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		CnSPartition p;
		CnSNetwork n;
		CnSView v;
		Long suid;
		Integer index;
		CnSCluster c;
		CyNode cyNode;
		CnSNode cnsNode;
		
		switch (event.getAction()) {
			case ADD_PARTITON :
				p = (CnSPartition)event.getParameter(PARTITION);
				if (!partitions.contains(p)) partitions.addElement(p);
 				break;
				
			case GET_PARTITION :
				index = (Integer)event.getParameter(INDEX);
				if (index != null) {
					if (partitions.size() > index.intValue())
						ret = partitions.elementAt(index.intValue());
				}
				else {
					v = (CnSView)event.getParameter(VIEW);
					if (v != null) 
						ret = view2partitionMap.get(v);
					else {
						n = (CnSNetwork)event.getParameter(NETWORK);
						if (n != null)
							ret = network2partitionMap.get(n);
						else {
							c = (CnSCluster)event.getParameter(CLUSTER);
							if (c != null)
								for (CnSPartition part : partitions)
									if (part.containsCluster(c)) {
										ret = part;
										break;
									}
						}
					}
				}
				break;
				
			case GET_CLUSTER_NODE :
				p = (CnSPartition)event.getParameter(PARTITION);
				suid = (Long)event.getParameter(NODE_SUID);
				cyNode = (CyNode)event.getParameter(CY_NODE);
				if (suid != null)
					ret = p.getClusterNode(suid);
				else if (cyNode != null)
					ret = p.getClusterNode(cyNode);
				break;
				
			case GET_NODE :
				p = (CnSPartition)event.getParameter(PARTITION);
				if (p != null) {
					suid = (Long)event.getParameter(NODE_SUID);
					ret = p.getNode(suid);
				}
				else {
					cyNode = (CyNode)event.getParameter(CY_NODE);
					cnsNode = null;
					
					for (CnSPartition part : partitions) {
						cnsNode = part.getClusterNode(cyNode);
						if (cnsNode == null) cnsNode = part.getNode(cyNode);
						if (cnsNode != null) {
							ret = cnsNode;
							break;
						}
					}
				}
				break;
				
			case SET_PARTITION_VIEW :
				p = (CnSPartition)event.getParameter(PARTITION);
				v = (CnSView)event.getParameter(VIEW);
				partition2viewMap.putIfAbsent(p, v);
				view2partitionMap.putIfAbsent(v, p);
				break;
				
			case SET_PARTITION_NETWORK :
				p = (CnSPartition)event.getParameter(PARTITION);
				n = (CnSNetwork)event.getParameter(NETWORK);
				partition2networkMap.putIfAbsent(p, n);
				network2partitionMap.putIfAbsent(n, p);
				break;
				
			case GET_VIEW :
				p = (CnSPartition)event.getParameter(PARTITION);
				ret = partition2viewMap.get(p);
				break;
				
			case REMOVE_PARTITION :
				p = (CnSPartition)event.getParameter(PARTITION);
				partitions.remove(p);
				for (CnSPartition part : network2partitionMap.values())
					if (part == p) {
						network2partitionMap.remove(partition2networkMap.get(p));
						break;
					}
				partition2networkMap.remove(p);
				break;
		}
		return ret;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public static CnSPartitionManager getInstance() {
		if (instance == null)
			instance = new CnSPartitionManager();
		return instance;
	}
}


