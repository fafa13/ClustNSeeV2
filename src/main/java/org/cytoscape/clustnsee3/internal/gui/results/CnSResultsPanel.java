/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.results;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.algorithm.FTTaskObserver;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
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
public class CnSResultsPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2970070719634162658L;
	private CnSClusterListPanel clusterListPanel;
	public static final int ADD_PARTITION = 1;
	public static final int GET_SELECTED_CLUSTER = 2;
	public static final int GET_SELECTED_PARTITION = 3;
	public static final int SELECT_CLUSTER = 4;
	public static final int DISCARD_PARTITION = 5;
	
	private static CnSResultsPanel instance;
	private CnSResultsCommandPanel commandPanel;
	private JTabbedPane jtp;
	
	public static final int PARTITION = 1001;
	public static final int RESULT = 1003;
	public static final int NETWORK = 1004;
	public static final int ALGO = 1005;
	public static final int CLUSTER = 1006;
	
	public CnSResultsPanel(String title) {
		super(title);
		initGraphics();
	}
	
	/**
	 * @param
	 * @return
	 */
	public CnSResultsPanel() {
		initGraphics();
	}

	protected void initGraphics() {
		super.initGraphics();
		jtp = new JTabbedPane();
		addComponent(jtp, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 10, 10, 10, 0, 0);
		commandPanel = new CnSResultsCommandPanel();
		addComponent(commandPanel, 0, 1, 1, 1, 1.0, 0.0, SOUTH, HORIZONTAL, 0, 10, 10, 10, 0, 0);
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		int action = event.getAction();
	    Object ret = null;
	    
	    switch (action) {
	    	case ADD_PARTITION:
	    		CnSAlgorithmResult result = (CnSAlgorithmResult)event.getParameter(RESULT);
	    		CyNetwork network = (CyNetwork)event.getParameter(NETWORK);
	    		CnSAlgorithm algo = (CnSAlgorithm)event.getParameter(ALGO);
	    		initResultPanel(result, network, algo);
	    		break;
	    	    
	    	case GET_SELECTED_CLUSTER:
	    		ret = clusterListPanel.getSelectedCluster();
	    		break;
	    		
	    	case GET_SELECTED_PARTITION:
	    		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
	    		ev.addParameter(CnSPartitionManager.INDEX, jtp.getSelectedIndex());
	    		ret = CnSEventManager.handleMessage(ev);
	    		break;
	    		
	    	case SELECT_CLUSTER :
	    		Long nodeId = (Long)event.getParameter(CLUSTER);
	    		if (nodeId == null)
	    			clusterListPanel.selectCluster(-1);
	    		else
	    			clusterListPanel.selectCluster(nodeId);
	    		break;
	    		
	    	case DISCARD_PARTITION :
	    		
	    		break;
	    }
	    return ret;
	}
	public static CnSResultsPanel getInstance() {
		if (instance == null)
			instance = new CnSResultsPanel();
		return instance;
	}
	private void initResultPanel(CnSAlgorithmResult result, CyNetwork inputNetwork, CnSAlgorithm algo) {
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
        ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev);
        
        CySubNetwork myNet = null;
        CyNetwork currentNetwork = applicationManager.getCurrentNetwork();
        CnSPartition newPartition = new CnSPartition(inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class), algo.getName(), algo.getParameters());
        int NbClas = result.getNbClass();
        int[] Kard = result.getCard();
        int[][] Cl = result.getClasses();
        HashMap<Integer, Long> algo_to_cyto = result.getAlgoToCyto();
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
            //while (Kard[kk] == 0) kk++;
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
            //kk++;
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
            			extEdges.addElement(new CnSEdge(ce));
            		}
            	}
            }
            newCluster.setEdges(alEdges);
            newCluster.setExtEdges(extEdges);
            
            // Create a new network
            myNet = crn.addSubNetwork(); //networkFactory.createNetwork();
            newCluster.setNetwork(myNet);
            
            // Add the network to Cytoscape
            networkManager.addNetwork(myNet);
            
            // Set name for network
            myNet.getRow(myNet).set(CyNetwork.NAME, inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class) + ":Cluster #" + (k + 1));
            newCluster.setName(inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class) + ":Cluster #" + (k + 1));
            
            // Fill network with cluster nodes and relevant edges 
            for (CnSNode node : newCluster.getNodes()) myNet.addNode(node.getCyNode());
            for (CnSEdge edge : newCluster.getEdges()) myNet.addEdge(edge.getCyEdge());
            	
            // create a new view for my network
            CyNetworkView myView = cnvf.createNetworkView(myNet);
            networkViewManager.addNetworkView(myView);
            
            newCluster.calModularity(myNet);
            
        	TaskIterator tit = apltf.createTaskIterator(networkViewManager.getNetworkViews(myNet));
            FTTaskObserver to = new FTTaskObserver(myView, newCluster);
            tm.execute(tit, to);
            
            
            CnSNetwork network = new CnSNetwork(myNet);
            ev = new CnSEvent(CnSNetworkManager.ADD_NETWORK, CnSEventManager.NETWORK_MANAGER);
            ev.addParameter(CnSNetworkManager.NETWORK, network);
            CnSEventManager.handleMessage(ev);
            //networkManager.destroyNetwork(myNet);
            
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
            //networkViewManager.destroyNetworkView(myView);
            
            newPartition.addCluster(newCluster);
        }
        newPartition.sortClusters();

        clusterListPanel = new CnSClusterListPanel();
		jtp.add(newPartition.getName() + ":" + newPartition.getAlgorithmName(), clusterListPanel);
		
		ev = new CnSEvent(CnSPartitionManager.ADD_PARTITON, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.PARTITION, newPartition);
		CnSEventManager.handleMessage(ev);
		//partitions.addElement(newPartition);
	    clusterListPanel.init(newPartition.getClusters());
        applicationManager.setCurrentNetwork(currentNetwork);
	}
}
