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

package org.cytoscape.clustnsee3.internal.algorithm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlAlgorithmPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;

/**
 * 
 */
public class CnSAlgorithmManager implements CnSEventListener  {
	public static final int INIT = 1;
	public static final int GET_PANEL = 2;
	public static final int GET_ALGORITHM = 3;
	public static final int GET_ALGORITHM_LIST = 5;
	public static final int GET_ALGORITHM_PARAMETERS = 6;
	public static final int SET_ALGORITHM_PARAMETER = 7;
	public static final int GET_SELECTED_ALGORITHM = 8;
	public static final int SET_SELECTED_ALGORITHM = 9;
	
	public static final int ALGO_NAME = 1000;
	public static final int ALGO_PARAMETERS = 1001;
	public static final int SELECTION = 1002;
	public static final int PARAMETER_NAME = 1003;
	public static final int PARAMETER_VALUE = 1004;
	public static final int PARAMETER_KEY = 1005;

	/**
	 * The list of available algorithms
	 */
	private Hashtable<String, CnSAlgorithm> algorithm = null;
	
	private Hashtable<String, CnSAlgorithmParameters> algorithmParameters = null;
	
	private Hashtable<String, Boolean> algorithmSelection = null;
	
	/**
	 * 
	 */
	private static CnSAlgorithmManager instance;
	/*
	 * 
	 */
	private CnSAlgorithmManager() {
		algorithm = new Hashtable<String, CnSAlgorithm>();
		algorithmParameters = new Hashtable<String, CnSAlgorithmParameters>();
		algorithmSelection = new Hashtable<String, Boolean>();
		CnSAlgorithmEngine.getInstance();
	}

	public String getActionName(int k) {
		switch(k) {
			case INIT : return "INIT";
			case GET_PANEL : return "GET_PANEL";
			case GET_ALGORITHM : return "GET_ALGORITHM";
			case GET_ALGORITHM_LIST : return "GET_ALGORITHM_LIST";
			case GET_ALGORITHM_PARAMETERS : return "GET_ALGORITHM_PARAMETERS";
			case SET_ALGORITHM_PARAMETER : return "SET_ALGORITHM_PARAMETER";
			case GET_SELECTED_ALGORITHM : return "GET_SELECTED_ALGORITHM";
			case SET_SELECTED_ALGORITHM : return "SET_SELECTED_ALGORITHM";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case ALGO_NAME : return "ALGO_NAME";
			case ALGO_PARAMETERS : return "ALGO_PARAMETERS";
			case SELECTION : return "SELECTION";
			case PARAMETER_NAME : return "PARAMETER_NAME";
			case PARAMETER_VALUE : return "PARAMETER_VALUE";
			case PARAMETER_KEY : return "PARAMETER_KEY";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	public static CnSAlgorithmManager getInstance() {
		if (instance == null)
			instance = new CnSAlgorithmManager();
		return instance;
	}
	public void register(String algoClassName, String panelClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
	    CnSAlgorithm algo = (CnSAlgorithm)Class.forName(algoClassName).getDeclaredMethod("getInstance", new Class<?>[0]).invoke(null);
	    if (panelClassName != null) {
	    	Constructor<?> constructor = Class.forName(panelClassName).getConstructor(String.class);
	    	CnSPanel panel = (CnSPanel)constructor.newInstance("");
	    	algo.getParameters().setPanel(panel);
	    }
	    algorithm.put(algo.getName(), algo);
		algorithmParameters.put(algo.getName(), algo.getParameters());
		algorithmSelection.put(algo.getName(), true);
	}

	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		int action = event.getAction();
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
	    
	    if (log) CnSLogger.getInstance().LogCnSEvent(event, this);
		
	    switch (action) {
	    	case INIT:
	    		try {
	    			register("org.cytoscape.clustnsee3.internal.algorithm.CnSFTAlgorithm", null);
	    			register("org.cytoscape.clustnsee3.internal.algorithm.CnSTFitAlgorithm", "org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlAlgorithmTfitOptionsPanel");
	    			register("org.cytoscape.clustnsee3.internal.algorithm.CnSOCGAlgorithm", "org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlAlgorithmOCGPanel");
	    		} 
	    		catch (Exception e) {
	    			String s = "";
	    			for (StackTraceElement el : e.getStackTrace()) s += "\t" + el.toString() + "\n";
	    			JOptionPane.showMessageDialog(null, e.getMessage() + "\n" + s);
	    			e.printStackTrace();
	    		}    		
	    		ret = new CnSEventResult<Integer>(Integer.valueOf(algorithm.size()));
	    		break;
	    		
	    	case GET_PANEL:
	    		ret = new CnSEventResult<CnSControlAlgorithmPanel>(new CnSControlAlgorithmPanel("Algorithm"));
	    		break;
	    		
	    	case GET_ALGORITHM :
	    		ret = new CnSEventResult<CnSAlgorithm>(algorithm.get((String)event.getParameter(ALGO_NAME)));
	    		break;
	    		
	    	case GET_ALGORITHM_LIST :
	    		ret = new CnSEventResult<Enumeration<String>>(algorithm.keys());
	    		break;
	    		
	    	case GET_ALGORITHM_PARAMETERS :
	    		ret = new CnSEventResult<CnSAlgorithmParameters>(algorithmParameters.get(event.getParameter(ALGO_NAME)));
	    		break;
	    		
	    	case SET_ALGORITHM_PARAMETER :
	    		String an = "*";
	    		if (event.getParameter(ALGO_NAME) != null)
	    			an = (String)event.getParameter(ALGO_NAME);
	    		int pk = 0;
	    		if (event.getParameter(PARAMETER_KEY) != null)
	    			pk = (Integer)event.getParameter(PARAMETER_KEY);
	    		Object pv = null;
	    		if (event.getParameter(PARAMETER_VALUE) != null)
	    			pv = event.getParameter(PARAMETER_VALUE);
	    		algorithmParameters.get(an).addParameter(event.getParameter(PARAMETER_NAME).toString(), pk, pv);
	    		break;
	    		
	    	case GET_SELECTED_ALGORITHM :
	    		for (String s : algorithmSelection.keySet())
	    			if (algorithmSelection.get(s)) {
	    				ret = new CnSEventResult<String>(s);
	    				break;
	    			}
	    		break;
	    		
	    	case SET_SELECTED_ALGORITHM :
	    		for (String s : algorithmSelection.keySet()) algorithmSelection.replace(s, false);
	    		algorithmSelection.replace((String)event.getParameter(ALGO_NAME), true);
	    		break;
	    }
	    return ret;
	}
}