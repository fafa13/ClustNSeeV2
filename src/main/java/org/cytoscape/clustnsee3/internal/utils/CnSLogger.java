/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 15 janv. 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.utils;

import java.util.Enumeration;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

/**
 * 
 */
public final class CnSLogger {
	private static CnSLogger cnslogger = null;
	private Logger logger;
	
	private CnSLogger() {
		CnSEvent ev = new CnSEvent(CyActivator.GET_OSGI_LOGGERFACTORY, CnSEventManager.CY_ACTIVATOR, CnSLogger.class);
    	LoggerFactory lf = (LoggerFactory)CnSEventManager.handleMessage(ev, true).getValue();
    	logger = lf.getLogger(CnSLogger.class);
	}
	public void LogCnSEvent(CnSEvent ev, CnSEventListener target) {
		Enumeration<Integer> keys = ev.getParameters();
		
		Integer i;
		logger.info("CNS\tSource : " + ev.getSource().getSimpleName() + " ; Target : {} ; Action : {}", target.getClass().getSimpleName(), target.getActionName(ev.getAction()));
		//System.err.println("LogCnSEvent : " + "CNS\tSource : " + ev.getSource().getSimpleName() + " ; Target : " + target.getClass().getSimpleName() + " ; Action : " + target.getActionName(ev.getAction()));
		String s = "CNS\tParameters : ";
		
		while (keys.hasMoreElements()) {
			i = keys.nextElement();
			s += target.getParameterName(i) + " -> ";
			s += ev.getParameter(i);
			s += " ; ";
		}
		//System.err.println("LogCnSEvent : " + s);
		logger.info(s);
	}
	public static CnSLogger getInstance() {
		if (cnslogger == null) {
			cnslogger = new CnSLogger();
		}
		return cnslogger;
	}
	public Logger getLogger() {
		return logger;
	}
}
