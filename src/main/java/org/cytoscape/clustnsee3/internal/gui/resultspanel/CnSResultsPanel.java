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

package org.cytoscape.clustnsee3.internal.gui.resultspanel;

import java.awt.Component;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSResultsPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = 2970070719634162658L;
	
	public static final int ADD_PARTITION = 1;
	public static final int GET_SELECTED_CLUSTER = 2;
	public static final int GET_SELECTED_PARTITION = 3;
	public static final int SELECT_CLUSTER = 4;
	public static final int DISCARD_PARTITION = 5;
	public static final int SORT_RESULTS = 6;
	public static final int GET_CLUSTER_NAME = 7;
	public static final int IMPORT_PARTITION = 8;
	
	public static final int PARTITION = 1001;
	public static final int RESULT = 1003;
	public static final int NETWORK = 1004;
	public static final int ALGO = 1005;
	public static final int CLUSTER = 1006;
	public static final int CLUSTER_NAME = 1007;
	public static final int SCOPE = 1008;
	public static final int ANNOTATION = 1009;
	public static final int TASK_MONITOR = 1010;
	
	private HashMap<CnSPartition, CnSClusterListPanel> clusterListPanel;
	private static CnSResultsPanel instance;
	private CnSResultsCommandPanel commandPanel;
	private JTabbedPane jtp;
	private CnSResultsSortPanel sortPanel;
	private JLabel nbClustersLabel;
	
	public CnSResultsPanel(String title) {
		super(title);
		clusterListPanel = new HashMap<CnSPartition, CnSClusterListPanel>();
		initGraphics();
	}
	
	/**
	 * @param
	 * @return
	 */
	public CnSResultsPanel() {
		clusterListPanel = new HashMap<CnSPartition, CnSClusterListPanel>();
		initGraphics();
		initListeners();
	}
	
	public String getActionName(int k) {
		switch(k) {
			case ADD_PARTITION : return "ADD_PARTITION";
			case GET_SELECTED_CLUSTER : return "GET_SELECTED_CLUSTER";
			case GET_SELECTED_PARTITION : return "GET_SELECTED_PARTITION";
			case SELECT_CLUSTER : return "SELECT_CLUSTER";
			case DISCARD_PARTITION : return "DISCARD_PARTITION";
			case SORT_RESULTS : return "SORT_RESULTS";
			case GET_CLUSTER_NAME : return "GET_CLUSTER_NAME";
			case IMPORT_PARTITION : return "IMPORT_PARTITION";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case PARTITION : return "PARTITION";
			case RESULT : return "RESULT";
			case NETWORK : return "NETWORK";
			case ALGO : return "ALGO";
			case CLUSTER : return "CLUSTER";
			case CLUSTER_NAME : return "CLUSTER_NAME";
			case SCOPE : return "SCOPE";
			case ANNOTATION : return "ANNOTATION";
			case TASK_MONITOR : return "TASK_MONITOR";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	private void initListeners() {
		jtp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (jtp.getSelectedIndex() != -1) {
					CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
					ev.addParameter(CnSPartitionManager.INDEX, jtp.getSelectedIndex());
					CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
					sortPanel.init(partition);
					nbClustersLabel.setText(String.valueOf(partition.getClusters().size()));
					ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL, this.getClass());
					ev.addParameter(CnSPartitionPanel.PARTITION, partition);
					CnSEventManager.handleMessage(ev, true);
				}
				else {
					sortPanel.init(null);
					nbClustersLabel.setText("");
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL, this.getClass());
					CnSEventManager.handleMessage(ev, true);
				}
			}
		});
	}

	public void initGraphics() {
		super.initGraphics();
		sortPanel = new CnSResultsSortPanel();
		addComponent(sortPanel, 0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 10, 10, 0, 10, 0, 0);
		
		CnSPanel centerPanel = new CnSPanel();
		centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		centerPanel.addComponent(new JLabel("Nb. of clusters :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 0, 0, 0, 0);
		nbClustersLabel = new JLabel();
		centerPanel.addComponent(nbClustersLabel, 1, 0, 1, 1, 1.0, 0.0, WEST, NONE, 5, 5, 0, 5, 0, 0);
		jtp = new JTabbedPane();
		centerPanel.addComponent(jtp, 0, 1, 2, 1, 1.0, 1.0, CENTER, BOTH, 5, 5, 5, 5, 0, 0);
		
		addComponent(centerPanel, 0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, 10, 10, 0, 10, 0, 0);
		commandPanel = new CnSResultsCommandPanel();
		addComponent(commandPanel, 0, 2, 1, 1, 1.0, 0.0, SOUTH, HORIZONTAL, 10, 10, 10, 10, 0, 0);
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
	@SuppressWarnings("unchecked")
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		int action = event.getAction();
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
	    CnSEvent ev;
	    final CnSAlgorithmResult result;
	    Vector<Vector<Long>> imported_partition;
	    Vector<Vector<String>> imported_annotation;
	    final CyNetwork network;
	    final CnSAlgorithm algo;
	    String scope;
	    final TaskMonitor taskMonitor;
	    
	    if (log) CnSLogger.LogCnSEvent(event, this);
		
	    switch (action) {
	    	case ADD_PARTITION:
	    		result = (CnSAlgorithmResult)event.getParameter(RESULT);
	    		network = (CyNetwork)event.getParameter(NETWORK);
	    		algo = (CnSAlgorithm)event.getParameter(ALGO);
	    		taskMonitor = (TaskMonitor)event.getParameter(TASK_MONITOR);
	    		initResultPanel(result, network, algo, taskMonitor);
			
	    		commandPanel.setEnabled(true);
	    		sortPanel.setEnabled(true);
	    		break;
	    		
	    	case IMPORT_PARTITION :
	    		imported_partition = (Vector<Vector<Long>>)event.getParameter(RESULT);
	    		imported_annotation = (Vector<Vector<String>>)event.getParameter(ANNOTATION);
	    		network = (CyNetwork)event.getParameter(NETWORK);
	    		algo = (CnSAlgorithm)event.getParameter(ALGO);
	    		scope = (String)event.getParameter(SCOPE);
	    		taskMonitor = (TaskMonitor)event.getParameter(TASK_MONITOR);
	    		initResultPanel(imported_partition, imported_annotation, network, algo, scope, taskMonitor);
	    		commandPanel.setEnabled(true);
	    		sortPanel.setEnabled(true);
	    		break;
	    	    
	    	case GET_SELECTED_CLUSTER:
	    		if (jtp.getModel().getSelectedIndex() > -1)
	    			ret = new CnSEventResult<CnSCluster>(((CnSClusterListPanel)jtp.getComponentAt(jtp.getModel().getSelectedIndex())).getSelectedCluster());
	    		break;
	    		
	    	case GET_SELECTED_PARTITION:
	    		ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
	    		if (jtp.getSelectedIndex() >= 0) ev.addParameter(CnSPartitionManager.INDEX, jtp.getSelectedIndex());
	    		ret = CnSEventManager.handleMessage(ev, true);
	    		break;
	    		
	    	case SELECT_CLUSTER :
	    		
	    		Long nodeId = (Long)event.getParameter(CLUSTER);
	    		int index;
	    		if (nodeId != null) {
	    			index = jtp.getModel().getSelectedIndex();
	    			if (index != -1) {
	    				((CnSClusterListPanel)jtp.getComponentAt(index)).selectCluster(nodeId);
	    				sortPanel.setSelectedCluster(nodeId);
	    			}
	    			else
	    				sortPanel.setSelectedCluster(0L);
	    		}
	    		else {
	    			Integer name = (Integer)event.getParameter(CLUSTER_NAME);
	    			if (name == null || name == 0) {
	    				index = jtp.getModel().getSelectedIndex();
	    				if (index != -1) ((CnSClusterListPanel)jtp.getComponentAt(index)).selectCluster(-1);
	    				sortPanel.setSelectedCluster(0L);
	    			}
	    			else {
	    				index = jtp.getModel().getSelectedIndex();
	    				if (index != -1) {
	    					System.err.println("CnSResultsPanel.SELECT_CLUSTER(" + name + ")");
	    					((CnSClusterListPanel)jtp.getComponentAt(index)).selectCluster(name);
	    					sortPanel.setSelectedCluster(name);
	    				}
	    			}
	    		}
	    			
	    		break;
	    		
	    	case DISCARD_PARTITION :
	    		CnSPartition partition = (CnSPartition)event.getParameter(PARTITION);
	    		taskMonitor = (TaskMonitor)event.getParameter(TASK_MONITOR);
	    		Vector<CnSCluster> clusters = partition.getClusters();
	    		
	    		ev = new CnSEvent(CyActivator.GET_NETWORK_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
	    	    CyNetworkManager crnm = (CyNetworkManager)CnSEventManager.handleMessage(ev, true).getValue();
	    	    ev = new CnSEvent(CyActivator.GET_NETWORK_VIEW_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
	            CyNetworkViewManager networkViewManager = (CyNetworkViewManager)CnSEventManager.handleMessage(ev, true).getValue();
	            
	    	    ev = new CnSEvent(CnSViewManager.REMOVE_VIEWS, CnSEventManager.VIEW_MANAGER, this.getClass());
    			ev.addParameter(CnSViewManager.REFERENCE, partition);
    			Vector<CnSView> deleted_views = (Vector<CnSView>)CnSEventManager.handleMessage(ev, true).getValue();
    			int N = clusters.size() + deleted_views.size();
    			int n = 0;
				for (CnSCluster cl : clusters) {
					taskMonitor.setProgress((double)n / (double)N);
	    			ev = new CnSEvent(CnSNetworkManager.GET_NETWORK, CnSEventManager.NETWORK_MANAGER, this.getClass());
	    	    	ev.addParameter(CnSNetworkManager.CLUSTER, cl);
	    	    	CnSNetwork cnsNetwork = (CnSNetwork)CnSEventManager.handleMessage(ev, true).getValue();
	    	    	
	    	    	if (cnsNetwork != null) {
	    	    		crnm.destroyNetwork(cnsNetwork.getNetwork());
	    	    		
	    	    		ev = new CnSEvent(CnSNetworkManager.REMOVE_NETWORK, CnSEventManager.NETWORK_MANAGER, this.getClass());
	    	    		ev.addParameter(CnSNetworkManager.NETWORK, cnsNetwork);
	    	    		CnSEventManager.handleMessage(ev, true);
	    	    	
	    	    		ev = new CnSEvent(CnSViewManager.REMOVE_VIEW, CnSEventManager.VIEW_MANAGER, this.getClass());
	    	    		ev.addParameter(CnSViewManager.NETWORK, cnsNetwork);
	    	    		ev.addParameter(CnSViewManager.CLUSTER, cl);
	    	    		CnSEventManager.handleMessage(ev, true);
	    	    	}
	    	    	n++;
	    	    }
	    	    
	    	    for (CnSView v : deleted_views) {
	    	    	taskMonitor.setProgress((double)n / (double)N);
    				if (crnm.networkExists(v.getView().getModel().getSUID())) {
    					networkViewManager.destroyNetworkView(v.getView());
    					crnm.destroyNetwork(v.getView().getModel());
    				}
    				n++;
    			}
    			jtp.remove(clusterListPanel.get(partition));
	    	    clusterListPanel.remove(partition);
	    		if (clusterListPanel.size() == 0) {
	    			commandPanel.setEnabled(false);
	    			sortPanel.setEnabled(false);
	    			ev = new CnSEvent(CnSPartitionPanel.CLEAR, CnSEventManager.PARTITION_PANEL, this.getClass());
	    			CnSEventManager.handleMessage(ev, true);
	    		}
	    		break;
	    		
	    	case SORT_RESULTS :
	    		if (jtp.getModel().getSelectedIndex() != -1 ) {
	    			((CnSClusterListPanel)jtp.getComponentAt(jtp.getModel().getSelectedIndex())).sort();
	    			jtp.repaint();
	    		}
	    		break;
	    		
	    	case GET_CLUSTER_NAME :
	    		long cluster_suid = (Long)event.getParameter(CLUSTER);
	    		if (jtp.getModel().getSelectedIndex() != -1)
	    			ret = new CnSEventResult<Object>(((CnSClusterListPanel)jtp.getComponentAt(jtp.getModel().getSelectedIndex())).getClusterName(cluster_suid));
	    		break;
	    }
	    return ret;
	}
	public static CnSResultsPanel getInstance() {
		if (instance == null)
			instance = new CnSResultsPanel();
		return instance;
	}
	private void initResultPanel(Vector<Vector<Long>> imported_partition, Vector<Vector<String>> imported_annotation, CyNetwork inputNetwork, CnSAlgorithm algo, String scope, TaskMonitor taskMonitor) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
        
        ev = new CnSEvent(CnSPartitionManager.IMPORT_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
        ev.addParameter(CnSPartitionManager.ALGORITHM, algo);
        ev.addParameter(CnSPartitionManager.SCOPE, scope);
        ev.addParameter(CnSPartitionManager.PARTITION_IMPORT, imported_partition);
        ev.addParameter(CnSPartitionManager.ANNOTATION_IMPORT, imported_annotation);
        ev.addParameter(CnSPartitionManager.NETWORK, inputNetwork);
        ev.addParameter(CnSPartitionManager.TASK_MONITOR, taskMonitor);
        CnSPartition newPartition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
        
        CnSClusterListPanel clp = new CnSClusterListPanel();
		clp.init(newPartition.getClusters());
        jtp.add(newPartition.getName() + ":" + newPartition.getAlgorithmName(), clp);
		clusterListPanel.putIfAbsent(newPartition, clp);
		nbClustersLabel.setText(String.valueOf(newPartition.getClusters().size()));
		applicationManager.setCurrentNetwork(inputNetwork);
		jtp.setSelectedComponent(clp);
		
		ev = new CnSEvent(CnSNodeAnnotationManager.REFRESH_CLUSTER_HASMAP, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
        //ev.addParameter(CnSNodeAnnotationManager.NETWORK, inputNetwork);
        ev.addParameter(CnSNodeAnnotationManager.PARTITION, newPartition);
        ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
        CnSEventManager.handleMessage(ev, true);
	}
	
	private void initResultPanel(CnSAlgorithmResult result, CyNetwork inputNetwork, CnSAlgorithm algo, TaskMonitor taskMonitor) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
        CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev, true).getValue();
        
        ev = new CnSEvent(CnSPartitionManager.CREATE_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
        ev.addParameter(CnSPartitionManager.ALGORITHM, algo);
        ev.addParameter(CnSPartitionManager.ALGORITHM_RESULTS, result);
        ev.addParameter(CnSPartitionManager.NETWORK, inputNetwork);
        ev.addParameter(CnSPartitionManager.TASK_MONITOR, taskMonitor);
        CnSPartition newPartition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
        
        CnSClusterListPanel clp = new CnSClusterListPanel();
		clp.init(newPartition.getClusters());
        jtp.add(newPartition.getName() + ":" + newPartition.getAlgorithmName(), clp);
		clusterListPanel.putIfAbsent(newPartition, clp);
		nbClustersLabel.setText(String.valueOf(newPartition.getClusters().size()));
		applicationManager.setCurrentNetwork(inputNetwork);
		jtp.setSelectedComponent(clp);
		
		ev = new CnSEvent(CnSNodeAnnotationManager.REFRESH_CLUSTER_HASMAP, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
        //ev.addParameter(CnSNodeAnnotationManager.NETWORK, inputNetwork);
        ev.addParameter(CnSNodeAnnotationManager.PARTITION, newPartition);
        ev.addParameter(CnSNodeAnnotationManager.TASK, taskMonitor);
        CnSEventManager.handleMessage(ev, true);
	}
}
