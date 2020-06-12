package org.cytoscape.clustnsee3.internal.analysis;

/*******************************************************************************/
/*                                                                             */
/* Copyright (C) 2013 Lionel Spinelli  (IML, Luminy, Marseille)                */
/*                                                                             */
/* with contributions from:                                                    */
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)  */
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)       */
/* Philippe Gambette (LIGM, Marne-la-Vallée)                                   */
/*                                                                             */
/*                                                                             */
/*                                                                             */
/* This program is free software: you can redistribute it and/or modify        */
/* it under the terms of the GNU General Public License as published by        */
/* the Free Software Foundation, either version 3 of the License, or           */
/* (at your option) any later version.                                         */
/*                                                                             */
/* This program is distributed in the hope that it will be useful,             */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of              */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                */
/* GNU General Public License for more details.                                */
/*                                                                             */
/* You should have received a copy of the GNU General Public License           */
/* along with this program. If not, see <http://www.gnu.org/licenses/>.        */
/*                                                                             */
/*******************************************************************************/

import java.util.HashMap;

import org.cytoscape.app.CyAppAdapter;

/**
 * This class is a manager in charge of the management of analysis parameters. This class maintains list of ParameterSet
 * associated to their respective network. Each network is associated to a single ParameterSet a copy of which is
 * registered when a new analysis is done on the network. Hence, ParameterSet act as singletons for a given network.
 * 
 * This class uses only static method accessiblity.
 * 
 */

public class ParametersManager {

    private static HashMap<String, ParameterSet> currentParams = new HashMap<String, ParameterSet>();
    private static HashMap<String, ParameterSet> resultParams  = new HashMap<String, ParameterSet>();

    private static CyAppAdapter adapter;
    
    public ParametersManager(CyAppAdapter adapter) {
    	ParametersManager.adapter = adapter;
    }
    
    /**
     * Get a copy of the current parameters for a particular network. usage: Parameters.getInstance().getParamsCopy();
     */
    public static ParameterSet getParamsCopy( String networkID) {

        if( networkID != null) {
            return ((ParameterSet) currentParams.get( networkID)).copy();
        }
        else {
            ParameterSet newParams = new ParameterSet(adapter);
            return newParams.copy();
        }
    }

    /**
     * 
     * @return
     */
    public static HashMap<String, ParameterSet> getAllParamSets() {

        return resultParams;
    }

    /**
     * 
     * @param resultSet
     * @return
     */
    public static ParameterSet getResultParams( String resultSet) {

        return ((ParameterSet) resultParams.get( resultSet));
    }

    /**
     * @param resultTitle
     */
    public static void removeResultParams( String resultTitle) {

        resultParams.remove( resultTitle);
    }

    /**
     * Current parameters can only be updated using this method.
     */
    public static ParameterSet registerParams( ParameterSet newParams, String analysis_id, String network_id) {

        // cannot simply equate the params and newParams classes since that creates a permanent reference
        // and prevents us from keeping 2 sets of the class such that the saved version is not altered
        // until this method is called
        ParameterSet currentParamSet = newParams.copy();
        // replace with new value
        currentParams.put( network_id, currentParamSet);
        ParameterSet resultParamSet = newParams.copy();
        resultParams.put( analysis_id, resultParamSet);
        return resultParamSet;
    }
}
