/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 30 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.view.style;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * 
 */
public class CnSStyleManager implements CnSEventListener {
	public static final int SET_CURRENT_STYLE = 1;
	public static final int APPLY_CURRENT_STYLE = 2;
	public static final int REMOVE_CNS_STYLES = 3;
	public static final int INIT = 4;
	
	public static final int STYLE = 1001;
	public static final int VIEW = 1002;
	
	public static final int CNS_STYLE = 10001;
	public static final int SNAPSHOT_STYLE = 10002;
	
	private static  CnSStyleManager instance = null;
	
	private HashMap<Integer, VisualStyle> style;
	private VisualStyle currentStyle;
	
	
	private HashMap<Integer, String> action;
	private HashMap<Integer, String> parameter;
	/**
	 * @param
	 * @return
	 */
	private CnSStyleManager() {
		super();
		style = new HashMap<Integer, VisualStyle>();
		action = new HashMap<Integer, String>();
		action.put(SET_CURRENT_STYLE, "SET_CURRENT_STYLE");
		action.put(APPLY_CURRENT_STYLE, "APPLY_CURRENT_STYLE");
		action.put(REMOVE_CNS_STYLES, "REMOVE_CNS_STYLES");
		action.put(INIT, "INIT");
		parameter = new HashMap<Integer, String>();
		parameter.put(STYLE, "STYLE");
		parameter.put(VIEW, "VIEW");
		parameter.put(CNS_STYLE, "CNS_STYLE");
		parameter.put(SNAPSHOT_STYLE, "SNAPSHOT_STYLE");
	}
	
	public String getActionName(int k) {
		switch(k) {
			case SET_CURRENT_STYLE : return "SET_CURRENT_STYLE";
			case APPLY_CURRENT_STYLE : return "APPLY_CURRENT_STYLE";
			case REMOVE_CNS_STYLES : return "REMOVE_CNS_STYLES";
			case INIT : return "INIT";
			default : return "UNDEFINED_ACTION : " + k;
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case STYLE : return "STYLE";
			case VIEW : return "VIEW";
			case CNS_STYLE : return "CNS_STYLE";
			case SNAPSHOT_STYLE : return "SNAPSHOT_STYLE";
			default : return "UNDEFINED_PARAMETER : " + k;
		}
	}
	
	public static CnSStyleManager getInstance() {
		if (instance == null) instance = new CnSStyleManager();
		return instance;
	}
	
	public void init() {
		Set<VisualStyle> vsSet;
		VisualStyle vs;
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_LOAD_VIZMAP_FILE_TASK_FACTORY, CnSEventManager.CY_ACTIVATOR, this.getClass());
		LoadVizmapFileTaskFactory lvtf = (LoadVizmapFileTaskFactory)CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.GET_VIZMAP_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		VisualMappingManager vmm = (VisualMappingManager)CnSEventManager.handleMessage(ev, true);
		
		boolean dejala = false;
		for (VisualStyle vss : vmm.getAllVisualStyles()) {
			if (vss.getTitle().startsWith("CnS_default")) {
				dejala = true;
				break;
			}
		}
		if (!dejala) {
			InputStream is = getClass().getResourceAsStream("/org/cytoscape/clustnsee3/internal/resources/cns.xml");
			if (is != null) {
				vsSet = lvtf.loadStyles(is);
				vs = vsSet.iterator().next();
				vmm.addVisualStyle(vs);
				//vmm.setCurrentVisualStyle(vs);
				style.put(CNS_STYLE, vs);
				currentStyle = vs;
			}
		}
		dejala = false;
		for (VisualStyle vss : vmm.getAllVisualStyles()) {
			if (vss.getTitle().startsWith("CnS_snapshot")) {
				dejala = true;
				break;
			}
		}
		if (!dejala) {
			InputStream is = getClass().getResourceAsStream("/org/cytoscape/clustnsee3/internal/resources/snapshot.xml");
			if (is != null) {
				vsSet = lvtf.loadStyles(is);
				vs = vsSet.iterator().next();
				vmm.addVisualStyle(vs);
				style.put(SNAPSHOT_STYLE, vs);
			}
		}
		/*if (style.get(CNS_STYLE) != null) {
			ev = new CnSEvent(CyActivator.GET_VIZMAP_MANAGER, CnSEventManager.CY_ACTIVATOR);
			VisualMappingManager vmm = (VisualMappingManager)CnSEventManager.handleMessage(ev);
			vmm.setCurrentVisualStyle(style.get(CNS_STYLE));
		}*/
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event, boolean log) {
		Object ret = null;
		CnSEvent ev;
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
		switch (event.getAction()) {
			case SET_CURRENT_STYLE :
				currentStyle = style.get(event.getParameter(STYLE));
				break;
				
			case APPLY_CURRENT_STYLE :
				ev = new CnSEvent(CyActivator.GET_VIZMAP_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
				VisualMappingManager vmm = (VisualMappingManager)CnSEventManager.handleMessage(ev, true);
				
				CnSView view = (CnSView)event.getParameter(VIEW);
				if (view == null) {
					ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER, this.getClass());
					view = (CnSView)CnSEventManager.handleMessage(ev, true);
				}
				if (view != null) {
					vmm.setVisualStyle(currentStyle, view.getView());
					if (currentStyle == null) currentStyle = style.get(event.getParameter(STYLE));
					if (currentStyle != null) currentStyle.apply(view.getView());
					view.getView().updateView();
				}
				break;
				
			case REMOVE_CNS_STYLES :
				ev = new CnSEvent(CyActivator.GET_VIZMAP_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
				vmm = (VisualMappingManager)CnSEventManager.handleMessage(ev, true);
				for (Integer vs : style.keySet()) vmm.removeVisualStyle(style.get(vs));
				currentStyle = null;
				
			case INIT :
				init();
				break;
		}
		return ret;
	}
}
