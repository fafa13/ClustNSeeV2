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
import org.cytoscape.clustnsee3.internal.analysis.annotation.CnSClusterAnnotation;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.task.CnSDiscardAllPartitionsTask;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSClusterViewState;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.task.visualize.ApplyPreferredLayoutTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * 
 */
public class CnSPartitionManager implements CnSEventListener, SessionLoadedListener {
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
	public static final int IMPORT_PARTITION = 15;
	public static final int GET_NODE_CLUSTERS = 16;
	public static final int GET_PARTITIONS = 17;
	public static final int GET_CLUSTER_NODES = 18;
	public static final int GET_ALL_CLUSTERS = 19;
	
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
	public static final int SCOPE = 1010;
	public static final int PARTITION_IMPORT = 1011;
	public static final int ANNOTATION_IMPORT = 1012;
	public static final int NODE_NAME = 1013;
	public static final int TASK_MONITOR = 1014;
	public static final int CLUSTER_ID = 1015;
	
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
	
	public String getActionName(int k) {
		switch(k) {
			case ADD_PARTITON : return "ADD_PARTITON";
			case GET_PARTITION : return "GET_PARTITION";
			case SET_PARTITION_NETWORK : return "SET_PARTITION_NETWORK";
			case GET_CLUSTER_NODE : return "GET_CLUSTER_NODE";
			case GET_NODE : return "GET_NODE";
			case SET_PARTITION_VIEW : return "SET_PARTITION_VIEW";
			case GET_VIEW : return "GET_VIEW";
			case REMOVE_PARTITION : return "REMOVE_PARTITION";
			case GET_CLUSTERS : return "GET_CLUSTERS";
			case CREATE_PARTITION : return "CREATE_PARTITION";
			case GET_PARTITION_NETWORK : return "GET_PARTITION_NETWORK";
			case GET_CLUSTER_LINK : return "GET_CLUSTER_LINK";
			case GET_NB_MULTICLASS_NODES : return "GET_NB_MULTICLASS_NODES";
			case GET_CLUSTER : return "GET_CLUSTER";
			case IMPORT_PARTITION : return "IMPORT_PARTITION";
			case GET_NODE_CLUSTERS : return "GET_NODE_CLUSTERS";
			case GET_PARTITIONS : return "GET_PARTITIONS";
			case GET_CLUSTER_NODES : return "GET_CLUSTER_NODES";
			case GET_ALL_CLUSTERS : return "GET_ALL_CLUSTERS";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {	
		switch(k) {
			case PARTITION : return "PARTITION";
			case INDEX : return "INDEX";
			case NETWORK : return "NETWORK";
			case NODE_SUID : return "NODE_SUID";
			case VIEW : return "VIEW";
			case CLUSTER : return "CLUSTER";
			case CY_NODE : return "CY_NODE";
			case ALGORITHM_RESULTS : return "ALGORITHM_RESULTS";
			case ALGORITHM : return "ALGORITHM";
			case CY_EDGE : return "CY_EDGE";
			case SCOPE : return "SCOPE";
			case PARTITION_IMPORT : return "PARTITION_IMPORT";
			case ANNOTATION_IMPORT : return "ANNOTATION_IMPORT";
			case NODE_NAME : return "NODE_NAME";
			case TASK_MONITOR : return "TASK_MONITOR";
			case CLUSTER_ID : return "CLUSTER_ID";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object cnsEventOccured(CnSEvent event, boolean log) {
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
		String scope, nodeName;
		Vector<Vector<Long>> partition_import;
		Vector<Vector<String>> annotation_import;
		TaskMonitor taskMonitor;
		Vector<CyNode> nodes;
		Vector<CnSCluster> clusters;
		Integer clusterID;
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
		switch (event.getAction()) {
			case ADD_PARTITON :
				p = (CnSPartition)event.getParameter(PARTITION);
				if (!partitions.contains(p)) partitions.addElement(p);
 				break;
 				
			case CREATE_PARTITION :
				inputNetwork = (CyNetwork)event.getParameter(NETWORK);
				algoResults = (CnSAlgorithmResult)event.getParameter(ALGORITHM_RESULTS);
				algorithm = (CnSAlgorithm)event.getParameter(ALGORITHM);
				taskMonitor = (TaskMonitor)event.getParameter(TASK_MONITOR);
				p = createPartition(inputNetwork, algoResults, algorithm, taskMonitor);
				if (!partitions.contains(p)) partitions.addElement(p);
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
				cyNode = (CyNode)event.getParameter(CY_NODE);
				suid = (Long)event.getParameter(NODE_SUID);
				if (p != null) {
					if (suid != null) 
						ret = p.getNode(suid);
					else {
						cnsNode = p.getClusterNode(cyNode);
						if (cnsNode == null) cnsNode = p.getNode(cyNode);
							if (cnsNode != null) {
								ret = cnsNode;
						}
					}
				}
				else {
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
				p = (CnSPartition)event.getParameter(PARTITION);
				if (p != null) {
					for (CnSClusterLink cl : p.getClusterLinks()) {
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
				else
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
				ret = Integer.valueOf(multiclassNodes.size());
				break;
				
			case GET_CLUSTER :
				cyNode = (CyNode)event.getParameter(CY_NODE);
				clusterID = (Integer)event.getParameter(CLUSTER_ID);
				c = null;
				p = (CnSPartition)event.getParameter(PARTITION);
				if (p != null) {
					if (cyNode != null)
						c = p.getCluster(cyNode);
					else if (clusterID != null)
						c = p.getCluster(clusterID);
					if (c != null) ret = c;
				}
				else for (CnSPartition part : partitions) {
					if (cyNode != null)
						c = part.getCluster(cyNode);
					else if (clusterID != null)
						c = part.getCluster(clusterID);
					if (c != null) {
						ret = c;
						break;
					}
				}
				break;
				
			case IMPORT_PARTITION :
				inputNetwork = (CyNetwork)event.getParameter(NETWORK);
				scope = (String)event.getParameter(SCOPE);
				algorithm = (CnSAlgorithm)event.getParameter(ALGORITHM);
				partition_import = (Vector<Vector<Long>>)event.getParameter(PARTITION_IMPORT);
				annotation_import = (Vector<Vector<String>>)event.getParameter(ANNOTATION_IMPORT);
				taskMonitor = (TaskMonitor)event.getParameter(TASK_MONITOR);
				p = importPartition(inputNetwork, partition_import, annotation_import, algorithm, scope, taskMonitor);
				if (!partitions.contains(p)) partitions.addElement(p);
				ret = p;
				break;
				
			case GET_NODE_CLUSTERS :
				nodeName = (String)event.getParameter(NODE_NAME);
				clusters = new Vector<CnSCluster>();
				for (CnSPartition part : partitions) {
					for (CnSCluster cluster : part.getClusters()) {
						if (cluster.contains(nodeName)) {
							clusters.addElement(cluster);
						}
					}
				}
				ret = clusters;
				break;
				
			case GET_PARTITIONS :
				ret = partitions;
				break;
				
			case GET_CLUSTER_NODES :
				nodes = new Vector<CyNode>();
				c = (CnSCluster)event.getParameter(CLUSTER);
				for (CnSNode node : c.getNodes()) nodes.addElement(node.getCyNode());
				ret = nodes;
				break;
				
			case GET_ALL_CLUSTERS :
				clusters = new Vector<CnSCluster>();
				for (CnSPartition part : partitions) clusters.addAll(part.getClusters());
				ret = clusters;
				break;
		}
		return ret;
	}
	
	private CnSPartition importPartition(CyNetwork inputNetwork, Vector<Vector<Long>> imported_partition, Vector<Vector<String>> imported_annotation, CnSAlgorithm algorithm, String scope, TaskMonitor taskMonitor) {
		// get services needed for network and view creation in cytoscape
		CnSEvent ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev, true);
		CyRootNetwork crn = crnm.getRootNetwork(inputNetwork);
		ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev, true);
		// network for each cluster
        CySubNetwork clusterNet = null;
        Vector<CyNode> cynodes_to_keep = new Vector<CyNode>();
        // the new partition to create
        CnSPartition partition = new CnSPartition(algorithm.getName(), algorithm.getParameters(), inputNetwork, scope);
		
        // use the snapshot style
        ev = new CnSEvent(CnSStyleManager.SET_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
        ev.addParameter(CnSStyleManager.STYLE, CnSStyleManager.SNAPSHOT_STYLE);
        CnSEventManager.handleMessage(ev, true);
        
        for( int k = 0; k < imported_partition.size(); k++)
        	for (int index_in_class = 0; index_in_class < imported_partition.get(k).size(); index_in_class++)
            	cynodes_to_keep.addElement(inputNetwork.getNode(imported_partition.get(k).get(index_in_class)));
        
        //System.err.println("----------------------------");
        //for (CyNode nn : cynodes_to_keep) System.err.println(nn.getSUID());
        //System.err.println("----------------------------");
        
        // the main loop on clusters
        taskMonitor.setProgress(0.0);
        for( int k = 0; k < imported_partition.size(); k++) {
        	
        	// create a new cluster
            CnSCluster cluster = new CnSCluster();
            
            // the cluster nodes and edges 
            Vector<CnSNode> nodes = new Vector<CnSNode>();
            Vector<CnSEdge> intEdges = new Vector<CnSEdge>();
            
            // the external links of the cluster
            Vector<CnSEdge> extEdges = new Vector<CnSEdge>();
            
            // creates the nodes of the cluster
            for (int index_in_class = 0; index_in_class < imported_partition.get(k).size(); index_in_class++) {
            	CnSNode cnsNode = partition.addNode(inputNetwork.getNode(imported_partition.get(k).get(index_in_class)), cluster);
        		cnsNode.setAttribute("name", inputNetwork.getRow(cnsNode.getCyNode()).get("name", String.class), String.class);
        		nodes.addElement(cnsNode);
            }
            cluster.setNodes(nodes);
            
            // creates the edges (internal and external) of the cluster             
            List<CyEdge> cedges;
            Iterator<CnSNode> nodesIterator = nodes.iterator();
            while (nodesIterator.hasNext()) {
            	CyNode cnode = nodesIterator.next().getCyNode();
            	cedges = inputNetwork.getAdjacentEdgeList(cnode, CyEdge.Type.ANY);
            	for (CyEdge ce : cedges) {
            		if (cluster.contains(ce.getSource()) && cluster.contains(ce.getTarget())) {
            			CnSEdge cnsEdge = partition.addEdge(ce);
                    	if (!intEdges.contains(cnsEdge)) intEdges.addElement(cnsEdge);
            		}
            		else if (cynodes_to_keep.contains(ce.getSource()) && cynodes_to_keep.contains(ce.getTarget())) {
            			CnSEdge e = new CnSEdge();
            			e.setCyEdge(ce);
            			extEdges.addElement(e);
            		}
            	}
            }
            cluster.setEdges(intEdges);
            cluster.setExtEdges(extEdges);
            
            // add cluster annotations
            for (int i = 0; i < imported_annotation.get(k).size(); i++)
            	cluster.addAnnotation(new CnSClusterAnnotation(imported_annotation.get(k).get(i)));
            
            // Create a new network
            clusterNet = crn.addSubNetwork();
            cluster.setNetwork(clusterNet);
            
            // Add the network to Cytoscape
            networkManager.addNetwork(clusterNet, false);
            
            // Set name for network
            clusterNet.getRow(clusterNet).set(CyNetwork.NAME, String.valueOf(k + 1));
            cluster.setName(String.valueOf(k + 1));
            cluster.setID(k + 1);
            
            // Fill network with cluster nodes and edges 
            for (CnSNode node : cluster.getNodes()) clusterNet.addNode(node.getCyNode());
            for (CnSEdge edge : cluster.getEdges()) clusterNet.addEdge(edge.getCyEdge());
            
            // Set cluster attributes
            cluster.setAttribute("CnS:isCluster", true, Boolean.class);
            cluster.setAttribute("CnS:size", cluster.getNbNodes(), Integer.class);
            cluster.setAttribute(CyNetwork.NAME, cluster.getName(), String.class);
            cluster.setAttribute("canonicalName", "Cluster #" + cluster.getName(), String.class);
			
			// Set cluster internal edges attributes
			for (CnSEdge edge : cluster.getEdges()) {
				edge.setAttribute("CnS:isInteraction", null, Boolean.class);
				edge.setAttribute("CnS:size", 1, Integer.class);
			}
			
			// Set cluster external edges attributes
			for (CnSEdge edge : cluster.getExtEdges()) {
				edge.setAttribute("CnS:isInteraction", null, Boolean.class);
				edge.setAttribute("CnS:size", 1, Integer.class);
			}
			
			// Set cluster nodes attributes
			for (CnSNode node : cluster.getNodes()) {
				node.setAttribute("CnS:isCluster", false, Boolean.class);
				node.setAttribute("CnS:size", 1, Integer.class);
			}
			
			cluster.print();
			
			//System.err.println("Cluster #" + cluster.getName());
			//System.err.println("  CnS:isCluster : " + cluster.getAttributes().get("CnS:isCluster"));
			//System.err.println("  CnS:size : " + cluster.getAttributes().get("CnS:size"));
			//System.err.println("  CyNetwork.NAME : " + cluster.getAttributes().get(CyNetwork.NAME));
			//System.err.println("  canonicalName : " + cluster.getAttributes().get("canonicalName"));
			
            //System.err.print("  calculating modularity ... ");
			cluster.calModularity(clusterNet);
            //System.err.println("done.");
            
            //System.err.print("  making view ... ");
			// create a new view for my network
            CyNetworkView myView = cnvf.createNetworkView(clusterNet);
            
            // myView.updateView();
            networkViewManager.addNetworkView(myView, false);
            //System.err.println("done.");
            
            //System.err.print("  making snapshot ... ");
			
            // create the CnSView and apply the snapshot style
            CnSView view = new CnSView(myView, new CnSClusterViewState(cluster));
            ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
            ev.addParameter(CnSStyleManager.VIEW, view);
            CnSEventManager.handleMessage(ev, true);
            //System.err.print("--- ");
             
            // make the snapshot
        	TaskIterator tit = apltf.createTaskIterator(networkViewManager.getNetworkViews(clusterNet));
            FTTaskObserver to = new FTTaskObserver(myView, cluster);
            tm.execute(tit, to);
            //System.err.println("done.");
            
            // create the network and register it
            CnSNetwork network = new CnSNetwork(clusterNet, inputNetwork);
            ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER, this.getClass());
            ev.addParameter(CnSNetworkManager.NETWORK, network);
            CnSEventManager.handleMessage(ev, true);
            
            // associates the network with the cluster
            ev = new CnSEvent(CnSNetworkManager.SET_NETWORK_CLUSTER, CnSEventManager.NETWORK_MANAGER, this.getClass());
            ev.addParameter(CnSNetworkManager.NETWORK, network);
            ev.addParameter(CnSNetworkManager.CLUSTER, cluster);
            CnSEventManager.handleMessage(ev, true);
            
            // register the cluster view
            ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER, this.getClass());
            ev.addParameter(CnSViewManager.VIEW, view);
            ev.addParameter(CnSViewManager.NETWORK, network);
            ev.addParameter(CnSViewManager.CLUSTER, cluster);
            CnSEventManager.handleMessage(ev, true);
            
            // fill CnS attributes
            for (String key : network.getNodeColumns().keySet())
				for (CnSNode node : cluster.getNodes())
					network.getNetwork().getRow(node.getCyNode()).set(key, node.getAttributes().get(key));
			
			// fill CnS attributes
	        for (String key : network.getEdgeColumns().keySet()) {
	        	for (CnSEdge edge : cluster.getEdges())
	        		network.getNetwork().getRow(edge.getCyEdge()).set(key, edge.getAttributes().get(key));
	        }
	        
	        // make the links between the new cluster and the existing ones
            makeClusterLinks(cluster, partition);
            
            // add the new cluster in the partition
            partition.addCluster(cluster);
            
            taskMonitor.setProgress((double)(k+1) / (double)imported_partition.size());
        }
        
        // go back to the CnS default style
        ev = new CnSEvent(CnSStyleManager.SET_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
        ev.addParameter(CnSStyleManager.STYLE, CnSStyleManager.CNS_STYLE);
        CnSEventManager.handleMessage(ev, true);
        
        return partition;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private CnSPartition createPartition(CyNetwork inputNetwork, CnSAlgorithmResult algoResults, CnSAlgorithm algorithm, TaskMonitor taskMonitor) {
		// get services needed for network and view creation in cytoscape
		CnSEvent ev = new CnSEvent(CyActivator.GET_ROOT_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyRootNetworkManager crnm = (CyRootNetworkManager)CnSEventManager.handleMessage(ev, true);
        CyRootNetwork crn = crnm.getRootNetwork(inputNetwork);
        System.err.println("CRN = " + crn);
        ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyNetworkManager networkManager = (CyNetworkManager)CnSEventManager.handleMessage(ev, true);
        ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyNetworkViewFactory cnvf = (CyNetworkViewFactory)CnSEventManager.handleMessage(ev, true);
        ev = new CnSEvent(CyActivator.GET_APPLY_PREFERRED_LAYOUT_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
        ApplyPreferredLayoutTaskFactory apltf =  (ApplyPreferredLayoutTaskFactory)CnSEventManager.handleMessage(ev, true);
        ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev, true);
        ev = new CnSEvent(CyActivator.GET_SYNCHRONOUS_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        TaskManager<?, ?> tm = (TaskManager<?, ?>)CnSEventManager.handleMessage(ev, true);
        
        // network for each cluster
        CySubNetwork clusterNet = null;
 
        // the new partition to create
        CnSPartition partition = new CnSPartition(algorithm.getName(), algorithm.getParameters(), inputNetwork, algoResults.getScope());
        
        // get algorithm results
        int NbClas = algoResults.getNbClass();
        int[] Kard = algoResults.getCard();
        int[][] Cl = algoResults.getClasses();
        Vector<CyNode> cynodes_to_keep = new Vector<CyNode>();
        HashMap<Integer, Long> algo_to_cyto = algoResults.getAlgoToCyto();
        for( int k = 0; k < NbClas; k++) {
        	for (int index_in_class = 0; index_in_class < Kard[k]; index_in_class++) {
            	int mod_clust_index = Cl[k][ index_in_class];
            	Long cyto_index = algo_to_cyto.get(mod_clust_index);
            	if( cyto_index != null) cynodes_to_keep.addElement(inputNetwork.getNode(cyto_index));
            }
        }
        
        //System.err.println("----------------------------");
        //for (CyNode nn : cynodes_to_keep) System.err.println(nn.getSUID());
        //System.err.println("----------------------------");
        
        // use the snapshot style
        ev = new CnSEvent(CnSStyleManager.SET_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
        ev.addParameter(CnSStyleManager.STYLE, CnSStyleManager.SNAPSHOT_STYLE);
        CnSEventManager.handleMessage(ev, true);
        
        // the main loop on clusters
        for( int k = 0; k < NbClas; k++) {
        	taskMonitor.setProgress((double)k / (double)NbClas);
        	// create a new cluster
            CnSCluster cluster = new CnSCluster();
            
            // the cluster nodes and edges 
            Vector<CnSNode> nodes = new Vector<CnSNode>();
            Vector<CnSEdge> intEdges = new Vector<CnSEdge>();
            
            // the external links of the cluster
            Vector<CnSEdge> extEdges = new Vector<CnSEdge>();
            
            // creates the nodes of the cluster
            for (int index_in_class = 0; index_in_class < Kard[k]; index_in_class++) {
            	int mod_clust_index = Cl[k][ index_in_class];
            	Long cyto_index = algo_to_cyto.get(mod_clust_index);
            	CnSNode cnsNode;
            	if( cyto_index != null){
            		cnsNode = partition.addNode(inputNetwork.getNode(cyto_index), cluster);
            		cnsNode.setAttribute("name", inputNetwork.getRow(cnsNode.getCyNode()).get("name", String.class), String.class);
            		nodes.addElement(cnsNode);
            	}
            }
            cluster.setNodes(nodes);
            
            // creates the edges (internal and external) of the cluster             
            List<CyEdge> cedges;
            Iterator<CnSNode> nodesIterator = nodes.iterator();
            while (nodesIterator.hasNext()) {
            	CyNode cnode = nodesIterator.next().getCyNode();
            	cedges = inputNetwork.getAdjacentEdgeList(cnode, CyEdge.Type.ANY);
            	for (CyEdge ce : cedges) {
            		if (cluster.contains(ce.getSource()) && cluster.contains(ce.getTarget())) {
            			CnSEdge cnsEdge = partition.addEdge(ce);
                    	if (!intEdges.contains(cnsEdge)) intEdges.addElement(cnsEdge);
            		}
            		else if (cynodes_to_keep.contains(ce.getSource()) && cynodes_to_keep.contains(ce.getTarget())) {
            			CnSEdge e = new CnSEdge();
            			e.setCyEdge(ce);
            			extEdges.addElement(e);
            		}
            	}
            }
            cluster.setEdges(intEdges);
            cluster.setExtEdges(extEdges);
            
            // Create a new network
            clusterNet = crn.addSubNetwork();
            cluster.setNetwork(clusterNet);
            
            // Add the network to Cytoscape
            networkManager.addNetwork(clusterNet, false);
            
            // Set name for network
            clusterNet.getRow(clusterNet).set(CyNetwork.NAME, String.valueOf(k + 1));
            cluster.setName(String.valueOf(k + 1));
            cluster.setID(k + 1);
            
            // Fill network with cluster nodes and edges 
            for (CnSNode node : cluster.getNodes()) clusterNet.addNode(node.getCyNode());
            for (CnSEdge edge : cluster.getEdges()) clusterNet.addEdge(edge.getCyEdge());
            
            // Set cluster attributes
            cluster.setAttribute("CnS:isCluster", true, Boolean.class);
            cluster.setAttribute("CnS:size", cluster.getNbNodes(), Integer.class);
            cluster.setAttribute(CyNetwork.NAME, cluster.getName(), String.class);
            cluster.setAttribute("canonicalName", "Cluster #" + cluster.getName(), String.class);
			
			// Set cluster internal edges attributes
			for (CnSEdge edge : cluster.getEdges()) {
				edge.setAttribute("CnS:isInteraction", null, Boolean.class);
				edge.setAttribute("CnS:size", 1, Integer.class);
			}
			
			// Set cluster external edges attributes
			for (CnSEdge edge : cluster.getExtEdges()) {
				edge.setAttribute("CnS:isInteraction", null, Boolean.class);
				edge.setAttribute("CnS:size", 1, Integer.class);
			}
			
			// Set cluster nodes attributes
			for (CnSNode node : cluster.getNodes()) {
				node.setAttribute("CnS:isCluster", false, Boolean.class);
				node.setAttribute("CnS:size", 1, Integer.class);
			}
			
			cluster.print();
			
			//System.err.println("Cluster #" + cluster.getName());
			//System.err.println("  CnS:isCluster : 				" + cluster.getAttributes().get("CnS:isCluster"));
			//System.err.println("  CnS:size : " + cluster.getAttributes().get("CnS:size"));
			//System.err.println("  CyNetwork.NAME : " + cluster.getAttributes().get(CyNetwork.NAME));
			//System.err.println("  canonicalName : " + cluster.getAttributes().get("canonicalName"));
			
			// create a new view for my network
            CyNetworkView myView = cnvf.createNetworkView(clusterNet);
            
            //myView.updateView();
            networkViewManager.addNetworkView(myView, false);
            
            cluster.calModularity(clusterNet);
            
            // create the CnSView and apply the snapshot style
            CnSView view = new CnSView(myView, new CnSClusterViewState(cluster));
            ev = new CnSEvent(CnSStyleManager.APPLY_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
            ev.addParameter(CnSStyleManager.VIEW, view);
            CnSEventManager.handleMessage(ev, true);
            
            // make the snapshot
        	TaskIterator tit = apltf.createTaskIterator(networkViewManager.getNetworkViews(clusterNet));
            FTTaskObserver to = new FTTaskObserver(myView, cluster);
            tm.execute(tit, to);
            
            // create the network and register it
            CnSNetwork network = new CnSNetwork(clusterNet, inputNetwork);
            ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER, this.getClass());
            ev.addParameter(CnSNetworkManager.NETWORK, network);
            CnSEventManager.handleMessage(ev, true);
            
            // associates the network with the cluster
            ev = new CnSEvent(CnSNetworkManager.SET_NETWORK_CLUSTER, CnSEventManager.NETWORK_MANAGER, this.getClass());
            ev.addParameter(CnSNetworkManager.NETWORK, network);
            ev.addParameter(CnSNetworkManager.CLUSTER, cluster);
            CnSEventManager.handleMessage(ev, true);
            
            // register the cluster view
            ev = new CnSEvent(CnSViewManager.ADD_VIEW, CnSEventManager.VIEW_MANAGER, this.getClass());
            ev.addParameter(CnSViewManager.VIEW, view);
            ev.addParameter(CnSViewManager.NETWORK, network);
            ev.addParameter(CnSViewManager.CLUSTER, cluster);
            CnSEventManager.handleMessage(ev, true);
            
            // fill CnS attributes
			for (String key : network.getNodeColumns().keySet())
				for (CnSNode node : cluster.getNodes())
					network.getNetwork().getRow(node.getCyNode()).set(key, node.getAttributes().get(key));
			
			// fill CnS attributes
	        for (String key : network.getEdgeColumns().keySet()) {
	        	for (CnSEdge edge : cluster.getEdges())
	        		network.getNetwork().getRow(edge.getCyEdge()).set(key, edge.getAttributes().get(key));
	        }
            // make the links between the new cluster and the existing ones
            makeClusterLinks(cluster, partition);
            
            // add the new cluster in the partition
            partition.addCluster(cluster);
        }
        
        // go back to the CnS default style
        ev = new CnSEvent(CnSStyleManager.SET_CURRENT_STYLE, CnSEventManager.STYLE_MANAGER, this.getClass());
        ev.addParameter(CnSStyleManager.STYLE, CnSStyleManager.CNS_STYLE);
        CnSEventManager.handleMessage(ev, true);
        
        //partition.sortClusters();
		
        return partition;
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
	
	@SuppressWarnings("unchecked")
	private void makeClusterLinks(CnSCluster cluster, CnSPartition partition) {
		Vector<CnSEdge> commonEdges = null;
		Vector<CnSNode> commonNodes = null;
		Vector<CyNode> commonCyNodes = null;
		
		for (CnSCluster cl : partition.getClusters())
			if (cl != cluster) {
				CnSClusterLink clusterLink = new CnSClusterLink(cluster, cl);
				if (!partition.getClusterLinks().contains(clusterLink)) {
					commonNodes = (Vector<CnSNode>)cluster.getNodes().clone();
					commonNodes.retainAll(cl.getNodes());
					commonCyNodes = new Vector<CyNode>();
					if (commonNodes.size() > 0) {
						for (CnSNode n : commonNodes) {
							clusterLink.addNode(n);
							commonCyNodes.addElement(n.getCyNode());
						}
						clusterLink.setMulticlassEdge(null);
						clusterLink.getMulticlassEdge().setAttribute("CnS:isInteraction", false, Boolean.class);
						clusterLink.getMulticlassEdge().setAttribute("CnS:size", clusterLink.getNodes().size(), Integer.class);
						clusterLink.getMulticlassEdge().setAttribute("interaction", "multiclass", String.class);
						clusterLink.getMulticlassEdge().setAttribute("name", clusterLink.getSource().getName() + " ~ " + clusterLink.getTarget().getName(), String.class);
						clusterLink.getMulticlassEdge().setAttribute("canonicalName", partition.getName() + ":" + clusterLink.getSource().getName() + " ~ " + clusterLink.getTarget().getName(), String.class);
					}
					commonEdges = (Vector<CnSEdge>)cluster.getExtEdges().clone();
					commonEdges.retainAll(cl.getExtEdges());
					if (commonEdges.size() > 0) {
						for (CnSEdge e : commonEdges)
							if (!commonCyNodes.contains(e.getCyEdge().getSource()) && !commonCyNodes.contains(e.getCyEdge().getTarget()))
								clusterLink.addEdge(e);
						clusterLink.setInteractionEdge(null);
						clusterLink.getInteractionEdge().setAttribute("CnS:isInteraction", true, Boolean.class);
						clusterLink.getInteractionEdge().setAttribute("CnS:size", clusterLink.getEdges().size(), Integer.class);
						clusterLink.getInteractionEdge().setAttribute("interaction", "pp", String.class);
						clusterLink.getInteractionEdge().setAttribute("name", clusterLink.getSource().getName() + " - " + clusterLink.getTarget().getName(), String.class);
						clusterLink.getInteractionEdge().setAttribute("canonicalName", partition.getName() + ":" + clusterLink.getSource().getName() + " - " + clusterLink.getTarget().getName(), String.class);
						
					}
					if ((clusterLink.getEdges().size() > 0) || (clusterLink.getNodes().size() > 0)) {
						partition.getClusterLinks().addElement(clusterLink);
					}
				}
			}
	}
	public void handleEvent(SessionLoadedEvent e) {
		System.err.println("Session loaded : " + e.getLoadedFileName());
		//while (partitions.size() > 0)  {
			CnSEvent ev = new CnSEvent(CyActivator.GET_TASK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
			DialogTaskManager dialogTaskManager = (DialogTaskManager)CnSEventManager.handleMessage(ev, true);
			TaskIterator ti = new TaskIterator();
			CnSDiscardAllPartitionsTask task = new CnSDiscardAllPartitionsTask();
			ti.append(task);
			dialogTaskManager.execute(ti);
		//}
			System.err.println("Partitions : " + partitions.size());
	}
}


