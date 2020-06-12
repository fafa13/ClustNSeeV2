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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;

/**
 * the set of all the parameters used in clustering
 */
/**
 * @author spinelli
 * 
 */
public class ParameterSet {

    // scope
    public static String            NETWORK   = "Network";
    public static String            SELECTION = "Selection";

    private CyAppAdapter adapter;
    
    // base variables
    private String                  networkID;
    private String                  scope;
    private ArrayList<CyNode>         nodesInScope;
    private ArrayList<CyEdge>         edgesInScope;
    private String                  algorithm;
    private CnSAlgorithm               algorithmInstance;

    // Table of parameters; key = name of the parameters (String), value = Object
    private HashMap<String, Object> parameters;

    /**
     * Constructor for the parameter set object.
     */
    public ParameterSet(CyAppAdapter adapter) {
    	this.adapter = adapter;
        adapter.getCyApplicationManager().getCurrentNetwork();
		this.networkID = CyIdentifiable.SUID;
        this.scope = NETWORK;
        this.algorithm = "";
        this.algorithmInstance = null;
        this.nodesInScope = new ArrayList<CyNode>();
        this.edgesInScope = new ArrayList<CyEdge>();
        this.parameters = new HashMap<String, Object>();
    }

    /**
     * Copies a parameter set object
     * 
     * @return A copy of the parameter set
     */
    public ParameterSet copy() {

        ParameterSet newParam = new ParameterSet(adapter);
        newParam.setNetworkID( this.networkID);
        newParam.setScope( this.scope);
        newParam.setAlgorithm( this.algorithm);
        newParam.setNodesInScope( this.nodesInScope);
        newParam.setEdgesInScope( this.edgesInScope);

        for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
            newParam.setParameter( entry.getKey(), entry.getValue());
        }

        return newParam;
    }

    /**
     * @return
     */
    public String getNetworkID() {

        return networkID;
    }

    /**
     * @param networkID
     */
    public void setNetworkID(String networkID) {

        this.networkID = networkID;
    }

    /**
     * @return
     */
    public String getScope() {

        return scope;
    }

    /**
     * @param scope
     */
    public void setScope(String scope) {

        this.scope = scope;
        if( NETWORK.equals( scope)) {
            nodesInScope = new ArrayList<CyNode>();
        }
    }

    /**
     * @return
     */
    public String getAlgorithm() {

        return this.algorithm;
    }

    /**
     * @param algorithm
     */
    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;
    }

    /**
     * Returns an instance of the chosen algorithm
     * 
     * @return an instance of the chosen algorithm
     */
    public CnSAlgorithm getAlgorithmInstance() {
    	CnSEvent ev;
    	if (algorithmInstance == null) {
        	ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM, CnSEventManager.ALGORITHM_MANAGER);
    		ev.addParameter(CnSAlgorithmManager.ALGO_NAME, algorithm);
            algorithmInstance = (CnSAlgorithm)CnSEventManager.handleMessage(ev);
        }
        return algorithmInstance;
    }

    /**
     * Remove the reference to the algorithm instance in order to let the gc clean the memory
     * 
     */
    public void clearAlgorithmInstance() {

        this.algorithmInstance = null;
        System.gc();
    }

    /**
     * Returns the list of nodes in analysis scope
     * 
     * @return the list of nodes in analysis scope
     */
    public ArrayList<CyNode> getNodesInScope() {

        return nodesInScope;
    }

    /**
     * Assign the list of nodes in analysis scope
     * 
     * @param nodesInScope
     */
    public void setNodesInScope( ArrayList<CyNode> selected_nodes) {

        nodesInScope = selected_nodes;
    }

    /**
     * Empty the list of nodes in analysis scope
     * 
     */
    public void clearNodesInScope() {

        nodesInScope = new ArrayList<CyNode>();
    }

    /**
     * Returns the list of edges in the analysis scope
     * 
     * @return the list of edges in the analysis scope
     */
    public ArrayList<CyEdge> getEdgesInScope() {

        return edgesInScope;
    }

    /**
     * Assign the list of Edge in the analysis scope
     * 
     * @param nodesInScope
     */
    public void setEdgesInScope( ArrayList<CyEdge> selected_edges) {

        edgesInScope = selected_edges;
    }

    /**
     * Empty the list of edges in the analysis scope
     * 
     */
    public void clearEdgesInScope() {

        edgesInScope = new ArrayList<CyEdge>();
    }

    /**
     * Returns the clustersTable of parameters
     * 
     * @return the clustersTable of parameters: key = name of the parameters (String), value = Object
     */
    public HashMap<String, Object> getParameters() {

        return parameters;
    }

    /**
     * Add a parameter to the parameter clustersTable
     * 
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public void setParameter( String name, Object value) {

        if( name != null && value != null) {
            parameters.put( name, value);
        }
    }

    /**
     * Returns the value associated to the given parameter name.
     * 
     * @param name the name of the desired parameter
     * @return the value of the given parameter
     */
    public Object getParameter( String name) {

        return parameters.get( name);
    }

    /**
     * Returns the float value associated to the given parameter name.
     * 
     * @param name the name of the desired parameter
     * @return the float value of the given parameter
     */
    public float getParameterAsFloat( String name) throws NumberFormatException {

        Object obj = parameters.get( name);

        if( obj instanceof String) {
            return Float.parseFloat( (String) obj);
        }

        throw new NumberFormatException( "The parameter '" + name + "' cannot be converted to float : " + obj);
    }

    /**
     * Returns the int value associated to the given parameter name.
     * 
     * @param name the name of the desired parameter
     * @return the int value of the given parameter
     */
    public int getParameterAsInt( String name) throws NumberFormatException {

        Object obj = parameters.get( name);

        if( obj instanceof String) {
            return Integer.parseInt( (String) obj);
        }

        throw new NumberFormatException( "The parameter '" + name + "' cannot be converted to int : " + obj);
    }

    /**
     * Generates a summary of the parameters.
     * 
     * @return Buffered string summarizing the parameters
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "   Network: " + networkID);
        return sb.toString();
    }

    /**
     * Check if the two given parameter sets contains the same parameters
     * 
     * @param o the object to compare
     * @return true if the given object is a ParameterSet that contains the same parameters as this one
     */
    public boolean isEqualTo( Object o) {

        if( !(o instanceof ParameterSet)) {
            return false;
        }

        ParameterSet psb = (ParameterSet) o;

        // Test the root network
        if( !this.getNetworkID().equals( psb.getNetworkID()) || !this.getAlgorithm().equals( psb.getAlgorithm()))
            return false;

        // Test the scope
        if( !this.getScope().equals( psb.getScope())) {
            return false;
        }

        // In case of a node selection test the content of the selection
        if( this.getScope().equals( ParameterSet.SELECTION)) {
            if( !this.getNodesInScope().containsAll( psb.getNodesInScope()) || !psb.getNodesInScope().containsAll( this.getNodesInScope())) {
                return false;
            }
            if( !this.getEdgesInScope().containsAll( psb.getEdgesInScope()) || !psb.getEdgesInScope().containsAll( this.getEdgesInScope())) {
                return false;
            }
        }

        // Test the list of other parameters
        HashMap<String, Object> params_a = this.getParameters();
        HashMap<String, Object> params_b = psb.getParameters();

        if( params_a.size() != params_b.size()) {
            return false;
        }

        for( Map.Entry<String, Object> entry_a : params_a.entrySet()) {
            if( !entry_a.getValue().equals( params_b.get( entry_a.getKey()))) {
                return false;
            }
        }

        return true;
    }
}