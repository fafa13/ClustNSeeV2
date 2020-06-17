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

package org.cytoscape.clustnsee3.internal;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;

import java.util.Properties;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.analysis.CnSPartitionHandler;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.control.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.data.CnSpartitionDetailsPanel;
import org.cytoscape.clustnsee3.internal.gui.menu.action.CnSMenuManager;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.ViewChangedListener;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class CnSClustnseePlugin implements CnSEventListener {
	private CnSAlgorithmManager algorithmManager;
	private CnSPartitionHandler analysisManager;
	private CnSMenuManager menuManager;
	private CnSpartitionDetailsPanel dataPanel;
	private CnSResultsPanel resultsPanel;
	private CnSAlgorithmEngine algorithmEngine;
	private CnSViewManager viewManager;
	private CnSNetworkManager networkManager;
	private static CyAppAdapter adapter;
	
	private static CnSClustnseePlugin instance;
	public static final int GET_PANEL = 1;
	public static final int GET_ADAPTER = 2;
	
	public static final int ADAPTER = 1000;
	
	private CnSClustnseePlugin(BundleContext context, CyAppAdapter appAdapter, CyActivator ca) {
		super();
		algorithmManager = CnSAlgorithmManager.getInstance();
		analysisManager = CnSPartitionHandler.getInstance();
		menuManager = CnSMenuManager.getInstance();
		dataPanel = CnSpartitionDetailsPanel.getInstance();
		resultsPanel = CnSResultsPanel.getInstance();
		algorithmEngine = CnSAlgorithmEngine.getInstance();
		viewManager = CnSViewManager.getInstance();
		networkManager = CnSNetworkManager.getInstance();
		CnSClustnseePlugin.adapter = appAdapter;
		CnSEventManager.getCnsEventManager(this, analysisManager, menuManager, dataPanel, resultsPanel, algorithmManager, algorithmEngine, viewManager, networkManager, ca);
		CnSEvent ev = new CnSEvent(CnSAlgorithmManager.INIT, CnSEventManager.ALGORITHM_MANAGER);
		CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSClustnseePlugin.GET_PANEL, CnSEventManager.CLUSTNSEE_PLUGIN);
		ev.addParameter(CnSClustnseePlugin.ADAPTER, appAdapter);
		CnSControlPanel controlPanel = (CnSControlPanel)CnSEventManager.handleMessage(ev);
		context.registerService(CytoPanelComponent.class.getName(), controlPanel, new Properties());
		context.registerService(CytoPanelComponent.class.getName(), resultsPanel, new Properties());
		context.registerService(CytoPanelComponent.class.getName(), dataPanel, new Properties());
		context.registerService(RemovedNodesListener.class.getName(), viewManager, new Properties());
		context.registerService(AddedNodesListener.class.getName(), viewManager, new Properties());
		context.registerService(RemovedEdgesListener.class.getName(), viewManager, new Properties());
		context.registerService(AddedEdgesListener.class.getName(), viewManager, new Properties());
		context.registerService(NetworkViewAboutToBeDestroyedListener.class.getName(), viewManager, new Properties());
	}
	
	public static CnSClustnseePlugin getInstance(BundleContext context, CyAppAdapter appAdapter, CyActivator ca) {
		if (instance == null) instance = new CnSClustnseePlugin(context, appAdapter, ca);
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		
		switch (event.getAction()) {
			case (GET_PANEL) :
				ret = new CnSControlPanel("Clust&see");
				break;
				
			case (GET_ADAPTER) :
				ret = adapter;
				break;
		}
		return ret;
	}
}