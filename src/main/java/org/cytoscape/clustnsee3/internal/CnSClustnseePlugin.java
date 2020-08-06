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
import java.util.Vector;

import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmEngine;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.control.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.info.CnSInfoPanel;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.action.CnSMenuManager;
import org.cytoscape.clustnsee3.internal.gui.results.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;
import org.cytoscape.model.events.AboutToRemoveNodesListener;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.model.events.UnsetNetworkPointerListener;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 */
public class CnSClustnseePlugin implements CnSEventListener {
	private CnSAlgorithmManager algorithmManager;
	private CnSPartitionManager analysisManager;
	private CnSMenuManager menuManager;
	private CnSInfoPanel dataPanel;
	private CnSResultsPanel resultsPanel;
	private CnSAlgorithmEngine algorithmEngine;
	private CnSViewManager viewManager;
	private CnSNetworkManager networkManager;
	private CnSPartitionManager partitionManager;
	private CnSStyleManager styleManager;
	private Vector<ServiceRegistration> ref;
	CnSControlPanel controlPanel;
	private static CnSClustnseePlugin instance;
	public static final int GET_PANEL = 1;
	
	private CnSClustnseePlugin(BundleContext context, CyActivator ca) {
		super();
		algorithmManager = CnSAlgorithmManager.getInstance();
		analysisManager = CnSPartitionManager.getInstance();
		menuManager = CnSMenuManager.getInstance();
		dataPanel = CnSInfoPanel.getInstance();
		resultsPanel = CnSResultsPanel.getInstance();
		algorithmEngine = CnSAlgorithmEngine.getInstance();
		viewManager = CnSViewManager.getInstance();
		networkManager = CnSNetworkManager.getInstance();
		partitionManager = CnSPartitionManager.getInstance();
		styleManager = CnSStyleManager.getInstance();
		CnSEventManager.getCnsEventManager(this, analysisManager, menuManager, dataPanel, resultsPanel, algorithmManager, algorithmEngine, viewManager, networkManager, partitionManager, styleManager, ca);
		CnSEvent ev = new CnSEvent(CnSAlgorithmManager.INIT, CnSEventManager.ALGORITHM_MANAGER);
		CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSClustnseePlugin.GET_PANEL, CnSEventManager.CLUSTNSEE_PLUGIN);
		controlPanel = (CnSControlPanel)CnSEventManager.handleMessage(ev);
		styleManager.init();
		ref = new Vector<ServiceRegistration>();
		ref.addElement(context.registerService(CytoPanelComponent.class.getName(), controlPanel, new Properties()));
		ref.addElement(context.registerService(CytoPanelComponent.class.getName(), resultsPanel, new Properties()));
		ref.addElement(context.registerService(CytoPanelComponent.class.getName(), dataPanel, new Properties()));
		ref.addElement(context.registerService(AboutToRemoveNodesListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(AddedNodesListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(RemovedEdgesListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(AddedEdgesListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(NetworkViewAboutToBeDestroyedListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(NetworkAboutToBeDestroyedListener.class.getName(), networkManager, new Properties()));
		ref.addElement(context.registerService(UnsetNetworkPointerListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(SetCurrentNetworkViewListener.class.getName(), viewManager, new Properties()));
		ref.addElement(context.registerService(SelectedNodesAndEdgesListener.class.getName(), viewManager, new Properties()));
	}
	
	public static CnSClustnseePlugin getInstance(BundleContext context, CyActivator ca) {
		if (instance == null) instance = new CnSClustnseePlugin(context, ca);
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
		}
		return ret;
	}
	public void stop() {
		for (ServiceRegistration sr : ref) sr.unregister();
		ref.clear();
		instance = null;
	}
}