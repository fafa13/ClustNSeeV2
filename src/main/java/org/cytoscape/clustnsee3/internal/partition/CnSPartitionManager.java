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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.algorithm.FTTaskObserver;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSClusterViewState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

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
	public static final int GET_CLUSTERS = 9;
	public static final int CREATE_PARTITION = 10;
	public static final int GET_PARTITION_NETWORK = 11;
	public static final int GET_CLUSTER_LINK = 12;
	public static final int GET_NB_MULTICLASS_NODES = 13;
	public static final int GET_CLUSTER = 14;
	
	public static final int PARTITION = 1000;
	public static final int INDEX = 1001;
	public static final int NETWORK = 1002;
	public static final int NODE_SUID = 1003;
	public static final int VIEW = 1004;
	public static final int CLUSTER = 1005;
	public static final int CY_NODE = 1006;
	public static final int ALGORITHM_RESULTS = 1007;
	public static final int ALGORITHM = 1008;
	public static final int CY_EDGE = 1009;
	
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
		CyEdge cyEdge;
		CnSNode cnsNode;
		CyNetwork inputNetwork;
		CnSAlgorithmResult algoResults;
		CnSAlgorithm algorithm;
		
		switch (event.getAction()) {
			case ADD_PARTITON :
				p = (CnSPartition)event.getParameter(PARTITION);
				if (!partitions.contains(p)) partitions.addElement(p);
 				break;
 				
			case CREATE_PARTITION :
				inputNetwork = (CyNetwork)event.getParameter(NETWORK);
				algoResults = (CnSAlgorithmResult)event.getParameter(ALGORITHM_RESULTS);
				algorithm = (CnSAlgorithm)event.getParameter(ALGORITHM);
				p = createPartition(inputNetwork, algoResults, algorithm);
				if (!partitions.contains(ret)) partitions.addElement(p);
				ret = p;
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
				
			case GET_CLUSTERS :
				p = (CnSPartition)event.getParameter(PARTITION);
				cyNode = (CyNode)event.getParameter(CY_NODE);
				cnsNode = p.getNode(cyNode);
				ret = cnsNode.getClusters();
				break;
				
			case GET_PARTITION_NETWORK :
				p = (CnSPartition)event.getParameter(PARTITION);
				ret = partition2networkMap.get(p);
				break;
				
			case GET_CLUSTER_LINK :
				cyEdge = (CyEdge)event.getParameter(CY_EDGE);
				for (CnSPartition part : partitions) {
					for (CnSClusterLink cl : part.getClusterLinks()) {
						if (cl.getInteractionEdge() != null)
							if (cyEdge == cl.getInteractionEdge().getCyEdge()) {
								ret = cl;
								break;
							}
						if (cl.getMulticlassEdge() != null)
							if (cyEdge == cl.getMulticlassEdge().getCyEdge()) {
								ret = cl;
								break;
							}
					}
				}
				break;
				
			case GET_NB_MULTICLASS_NODES :
				p = (CnSPartition)event.getParameter(PARTITION);
				c = (CnSCluster)event.getParameter(CLUSTER);
				Vector<CnSNode> multiclassNodes = new Vector<CnSNode>();
				
				for (CnSClusterLink cl : p.getClusterLinks())
					if (cl.getNodes().size() > 0 && (cl.getSource() == c || cl.getTarget() == c))
						for (CnSNode node : cl.getNodes())
							if (!multiclassNodes.contains(node))
								multiclassNodes.addElement(node);
				ret = new Integer(multiclassNodes.size());
				break;
				
			case GET_CLUSTER :
				cyNode = (CyNode)event.getParameter(CY_NODE);
				for (CnSPartition part : partitions) {
					CnSCluster cc = part.getCluster(cyNode);
					if (cc != null) {
						ret = cc;
						break;
					}
				}
				break;
		}
		return ret;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private CnSPartition createPartition(CyNetwork inputNetwork, CnSAlgorithmResult algoResults, CnSAlgorithm algorithm) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev);
        CyRootNetwork crn = crnm.getRootNetwork(inputNetwork);
        ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev);
        ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR);
        CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev);
        ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR);
        ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev);
        ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev);
        ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR);
        TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev);
        
        CySubNetwork myNet = null;
        CnSPartition newPartition = new CnSPartition(algorithm.getName(), algorithm.getParameters(), inputNetwork, algoResults.getScope());
        int NbClas = algoResults.getNbClass();
        int[] Kard = algoResults.getCard();
        int[][] Cl = algoResults.getClasses();
        HashMap<Integer, Long> algo_to_cyto = algoResults.getAlgoToCyto();
        Vector<CyNode> cynodes_to_keep = new Vector<CyNode>();
        for( int k = 0; k < NbClas; k++) {
        	for (int index_in_class = 0; index_in_class < Kard[k]; index_in_class++) {
            	int mod_clust_index = Cl[k][ index_in_class];
            	Long cyto_index = algo_to_cyto.get(mod_clust_index);
            	if( cyto_index != null) cynodes_to_keep.addElement(inputNetwork.getNode(cyto_index));
            }
        }
        for( int k = 0; k < NbClas; k++) {
            CnSCluster newCluster = new CnSCluster();
            Vector<CnSNode> alNodes = new Vector<CnSNode>();
            Vector<CnSEdge> alEdges = new Vector<CnSEdge>();
            Vector<CnSEdge> extEdges = new Vector<CnSEdge>();
            
            for (int index_in_class = 0; index_in_class < Kard[k]; index_in_class++) {
            	int mod_clust_index = Cl[k][ index_in_class];
            	Long cyto_index = algo_to_cyto.get(mod_clust_index);
            	CnSNode cnsNode;
            	if( cyto_index != null){
            		cnsNode = newPartition.addNode(inputNetwork.getNode(cyto_index), newCluster);
            		alNodes.addElement(cnsNode);
            	}
            }
            newCluster.setNodes(alNodes);
            
            List<CyEdge> cedges;
            Iterator<CnSNode> nodesIterator = alNodes.iterator();
            while (nodesIterator.hasNext()) {
            	CyNode cnode = nodesIterator.next().getCyNode();
            	cedges = inputNetwork.getAdjacentEdgeList(cnode, CyEdge.Type.ANY);
            	for (CyEdge ce : cedges) {
            		if (newCluster.contains(ce.getSource()) && newCluster.contains(ce.getTarget())) {
            			CnSEdge cnsEdge = newPartition.addEdge(ce);
                    	if (!alEdges.contains(cnsEdge)) alEdges.addElement(cnsEdge);
            		}
            		else if (cynodes_to_keep.contains(ce.getSource()) && cynodes_to_keep.contains(ce.getTarget())) {
            			CnSEdge e = new CnSEdge();
            			e.setCyEdge(ce);
            			extEdges.addElement(e);
            		}
            	}
            }
            newCluster.setEdges(alEdges);
            newCluster.setExtEdges(extEdges);
            
            // Create a new network
            myNet = crn.addSubNetwork();
            newCluster.setNetwork(myNet);
            
            // Add the network to Cytoscape
            networkManager.addNetwork(myNet);
            
            // Set name for network
            myNet.getRow(myNet).set(CyNetwork.NAME, /*inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class) + ":Cluster #" + */String.valueOf(k + 1));
            newCluster.setName(/*inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class) + ":" + Cluster #" + */String.valueOf(k + 1));
            
            // Fill network with cluster nodes and relevant edges 
            for (CnSNode node : newCluster.getNodes()) myNet.addNode(node.getCyNode());
            for (CnSEdge edge : newCluster.getEdges()) myNet.addEdge(edge.getCyEdge());
            
            // Set cluster attributes
            newCluster.setAttribute("CnS:isCluster", true, Boolean.class);
			newCluster.setAttribute("CnS:size", newCluster.getNbNodes(), Integer.class);
			
            // create a new view for my network
            CyNetworkView myView = cnvf.createNetworkView(myNet);
            myView.updateView();
            networkViewManager.addNetworkView(myView);
            
            newCluster.calModularity(myNet);
            
        	TaskIterator tit = apltf.createTaskIterator(networkViewManager.getNetworkViews(myNet));
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
            
            CnSView view = new CnSView(myView, new CnSClusterViewState(newCluster));
            ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER);
            ev.addParameter(CnSViewManager.VIEW, view);
            ev.addParameter(CnSViewManager.NETWORK, network);
            ev.addParameter(CnSViewManager.CLUSTER, newCluster);
            CnSEventManager.handleMessage(ev);
            
            newPartition.addCluster(newCluster);
        }
        newPartition.sortClusters();
		
        return newPartition;
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


