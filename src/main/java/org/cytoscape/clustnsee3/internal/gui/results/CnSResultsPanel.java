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

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmResult;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

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
	    CnSEvent ev;
	    
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
	    		ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
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
		CnSEvent ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
        CyApplicationManager applicationManager = (CyApplicationManager)CnSEventManager.handleMessage(ev);
        CyNetwork currentNetwork = applicationManager.getCurrentNetwork();
        
        ev = new CnSEvent(CnSPartitionManager.CREATE_PARTITION, CnSEventManager.PARTITION_MANAGER);
        ev.addParameter(CnSPartitionManager.ALGORITHM, algo);
        ev.addParameter(CnSPartitionManager.ALGORITHM_RESULTS, result);
        ev.addParameter(CnSPartitionManager.NETWORK, inputNetwork);
        CnSPartition newPartition = (CnSPartition)CnSEventManager.handleMessage(ev);
        
        clusterListPanel = new CnSClusterListPanel();
		jtp.add(newPartition.getName() + ":" + newPartition.getAlgorithmName(), clusterListPanel);
		
		clusterListPanel.init(newPartition.getClusters());
        applicationManager.setCurrentNetwork(currentNetwork);
	}
}
