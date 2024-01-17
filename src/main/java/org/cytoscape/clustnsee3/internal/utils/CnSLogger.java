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

import java.util.Iterator;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public final class CnSLogger {
	public static final void LogCnSEvent(CnSEvent ev, CnSEventListener target) {
		Iterator<Integer> keys = ev.getParameters().asIterator();
		final Logger LOGGER = LoggerFactory.getLogger(target.getClass());
		
		Integer i;
		LOGGER.info("CNS\tSource : " + ev.getSource().getSimpleName() + " ; Target : {} ; Action : {}", target.getClass().getSimpleName(), target.getActionName(ev.getAction()));
		String s = "CNS\tParameters : ";
		
		while (keys.hasNext()) {
			i = keys.next();
			s += target.getParameterName(i) + " -> ";
			s += ev.getParameter(i);
			s += " ; ";
		}
		LOGGER.info(s);
	}
}
