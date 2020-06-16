/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.view;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;

/**
 * 
 */
public class CnSViewManager implements CnSEventListener {
	public static final int ADD_VIEW = 1;
	public static final int DELETE_VIEW = 2;
	public static final int SET_SELECTED_VIEW = 3;
	public static final int GET_SELECTED_VIEW = 4;
	public static final int SET_STATE = 5;
	public static final int GET_VIEW = 6;
	
	public static final int VIEW = 1000;
	public static final int STATE = 1001;
	public static final int REFERENCE = 1002;
	
	private Vector<CnSView> views;
	private CnSView selectedView;
	private static CnSViewManager instance = null;
	
	private CnSViewManager() {
		super();
		views = new Vector<CnSView>();
		selectedView = null;
	}
	
	public static CnSViewManager getInstance() {
		if (instance == null) {
			instance = new CnSViewManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		CnSView view;
		CnSViewState state = null;
		switch(event.getAction()) {
			case ADD_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				if (!views.contains(view)) views.addElement(view);
				break;
				
			case DELETE_VIEW :
				view = (CnSView)event.getParameter(VIEW);
				views.removeElement(view);
				break;
				
			case SET_SELECTED_VIEW :
				selectedView = (CnSView)event.getParameter(VIEW);
				break;
				
			case GET_SELECTED_VIEW :
				ret = selectedView;
				break;
			
			case SET_STATE :
				view = (CnSView)event.getParameter(VIEW);
				state = (CnSViewState)event.getParameter(STATE);
				view.setViewState(state);
				break;
				
			case GET_VIEW :
				Object reference = event.getParameter(REFERENCE);
				if (reference != null)
					for (CnSView v : views)
						if (v.getReference() != null)
							if (v.getReference().equals(reference)) {
								ret = v;
								break;
							}
				break;
		}
		return ret;
	}
}
