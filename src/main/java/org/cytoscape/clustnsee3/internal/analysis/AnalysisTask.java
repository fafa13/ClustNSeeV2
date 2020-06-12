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

import java.io.File;
import java.util.ArrayList;

import org.cytoscape.model.CyNode;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.model.CyNetwork;

/**
 * This class represents both an analysis task (in the sense of a Cytoscpae Task) and the analysis result. As a Task,
 * this class can be run throught the Cytoscape TaskManager. It manage the execution of the algorithm associated to the
 * analysis and provide analysis of the algorithm results in order to get a finalized result.
 * 
 * As an analysis result, this class contains: - The list of discovered clusters - The information on the analysis
 * (network, algorithm, parameters...) - Some extra informations provided by the finalization analysis
 * 
 * Moreover, this class contains the methods to export/import the clusters to files.
 * 
 * 
 */

public class AnalysisTask implements Task {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisTask.class);
    private int                analysisID;
    private String             analysisName;
    private ParameterSet       parameters;
    private CyNetwork          rootNetwork;
    private ArrayList<CnSCluster> clusters;

    private boolean            completedSuccessfully = false;
    private boolean            interrupted           = false;

    private TaskMonitor taskMonitor;
    
    /**
     * Create the analysis task
     * 
     * @param rootNetwork The rootNetwork to cluster
     * @param analyzeID the ID associated to the analysis
     * @param analysisName the name associated to the analysis
     * @param params the parameters associated to the analysis
     */
    public AnalysisTask( CyNetwork network, int analysis_id, String analysis_name, ParameterSet params) {

        this.rootNetwork = network;
        this.analysisID = analysis_id;
        this.analysisName = analysis_name;
        this.parameters = params;
    }

    /**
     * Scores and finds clusters in a given rootNetwork
     * 
     * @param rootNetwork The rootNetwork to cluster
     * @param analyzeID the ID associated to the analysis
     * @param params the parameters associated to the analysis
     */
    public AnalysisTask( CyNetwork network, int analysis_id, ParameterSet params) {

        /*this.rootNetwork = network;
        this.analysisID = analysis_id;
        this.analysisName = AnalysisManagerOld.getManager().getNewAnalysisName( network, analysis_id);
        this.parameters = params;*/
    }
    public void run() {
    	run(taskMonitor);
    }

    /**
     * Execute the algorithm to retrieve the cluster list and build all the cluster images that will be used in the
     * result panel
     * 
     */
    @Override
    public void run(TaskMonitor taskMonitor) {

        /*if( taskMonitor == null) {
            throw new IllegalStateException( "The Task Monitor has not been set.");
        }

        try {
            // Retrieve the cluster list
            taskMonitor.setProgress(0);
            taskMonitor.setStatusMessage( "Cluster computation...");
            CnSAlgorithm algorithm = parameters.getAlgorithmInstance();
            algorithm.setTaskMonitor( taskMonitor, rootNetwork.SUID);
            try {
                clusters = algorithm.execute( rootNetwork, this);
            }
            catch( Exception e) {
                LOGGER.error("The execution of the analysis raised an exception : " + e.getMessage());
                e.printStackTrace();
                completedSuccessfully = false;
                return;
            }
            finally {
                parameters.clearAlgorithmInstance();
            }
            // Reorder the cluster by size and assign them an ID relative to this order
            if( clusters != null && clusters.size() > 1) {
                CnSCluster[] array_cluster = clusters.toArray( new CnSCluster[clusters.size()]);
                ClusterUtil.orderClusters( array_cluster, ClusterUtil.SORT_OPTION_SIZE);
                for( int i = 0; i < clusters.size(); i++) {
                    clusters.set( i, array_cluster[ i]);
                    clusters.get( i).setClusterID( i + 1);
                }
            }
            if( interrupted) {
                return;
            }

            taskMonitor.setProgress(0.5);
            taskMonitor.setStatusMessage( "Cluster analysis...");

            // Analyze the resulting clusters
            analyseClusters();
            taskMonitor.setProgress(0.75);

            // Compute and associate their attributes to clusters
            analyseClusterNodesAttributes();
            taskMonitor.setProgress(0.75);

            // Create the cluster nodes
            createClusterNodes();
            taskMonitor.setProgress(0.9);

            // Initialize attributes on root network edges
            initializeEdgeAttributes();

            taskMonitor.setProgress(1);

            // Indicates the algorithm ended normally
            completedSuccessfully = true;

        }
        catch( Exception e) {
            taskMonitor.setException( e, "Clustering cancelled!");
        }*/
    }

    /**
     * Assign the node attributes to the cluster nodes according to their status respect to the clusters
     * 
     */
    private void analyseClusterNodesAttributes() {

        /*CyAttributes cyAttributes = Cytoscape.getNodeAttributes();

        for( int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get( i);
            Iterator<CyNode> itr = cluster.getNestedNetwork().getNodeList().iterator();
            while( itr.hasNext()) {
                CyNode node = itr.next();

                // Add the cluster name to the node cluster list
                ArrayList<String> cluster_list = (ArrayList<String>) cyAttributes.getListAttribute( node.getIdentifier(), Constants.NODE_PARAM_CLUSTER_LIST);

                if( cluster_list == null) {
                    cluster_list = new ArrayList<String>();
                }
                String cluster_id = cluster.getClusterName();
                if( !cluster_list.contains( cluster_id)) {
                    cluster_list.add( cluster_id);
                }
                cyAttributes.setListAttribute( node.SUID, Constants.NODE_PARAM_CLUSTER_LIST, cluster_list);

                // Set the label of the node
                cyAttributes.setAttribute( node.SUID, Constants.NODE_PARAM_LABEL, node.SUID);

                // Indicates the node is not a cluster
                cyAttributes.setAttribute( node.SUID, Constants.NODE_PARAM_IS_CLUSTER, Constants.FALSE);

                // Indicates the number of object the node represent
                cyAttributes.setAttribute( node.SUID, Constants.NODE_PARAM_OBJECT_NUMBER, 1);
            }
            cluster.computeMonoClassedNodes();
            cluster.propagateAnnotations();
        }

        // Compute the nodes that are multiclassed
        for( int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get( i);
            Iterator<CyNode> itr = cluster.getNestedNetwork().getNodeList().iterator();
            while( itr.hasNext()) {
                CyNode node = itr.next();
                ArrayList<String> cluster_list = getClusterList( node);
                if( cluster_list != null && cluster_list.size() > 1) {
                    cluster.addMulticlassedNodeIndex(node.getNetworkPointer().getNodeList().indexOf(node));
                }
            }
        }*/
    }

    /**
     * Analyze the cluster itself by computing several indicators and creating its nested network
     * 
     */
    private void analyseClusters() {

        // Compute fo each cluster the modularity, and the inner and outer connection numbers
        /*for( int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get( i);
            if( cluster != null) {
                cluster.calModularity( rootNetwork);
            }

            CySubNetwork gp = rootNetwork.createGraphPerspective( Utils.convertIntArrayList( cluster.getNodeIndexList()));
            CyNetwork cluster_network = Cytoscape.createNetwork( gp.getNodeIndicesArray(), gp.getEdgeIndicesArray(), cluster.getClusterName(), rootNetwork, false);
            cluster.setNestedNetwork( cluster_network);
        }*/

    }

    /**
     * Create a CyNode for each cluster
     * 
     */
    private void createClusterNodes() {

        /*CyAttributes cyattributes = Cytoscape.getNodeAttributes();

        for( Cluster current_cluster : clusters) {
            String cluster_name = current_cluster.getClusterName();
            CyNode cluster_node = Cytoscape.getCyNode( cluster_name, true);
            cyattributes.setAttribute( cluster_node.SUID, Constants.CLUSTER_PARAM_CLUSTER_ID, cluster_name);
            cyattributes.setAttribute( cluster_node.SUID, Constants.NODE_PARAM_OBJECT_NUMBER, current_cluster.getNodeIndexList().size());
            cyattributes.setAttribute( cluster_node.SUID, Constants.NODE_PARAM_LABEL, cluster_name);
            cyattributes.setAttribute( cluster_node.SUID, Constants.NODE_PARAM_IS_CLUSTER, Constants.TRUE);
            cyattributes.setListAttribute( cluster_node.SUID, Constants.CLUSTER_PARAM_ANNOTATIONS, current_cluster.getAnnotations());
            cluster_node.setNestedNetwork( current_cluster.getNestedNetwork());
            current_cluster.setClusterNode( cluster_node);
        }*/

    }

    /**
     * Initialize some attributes on the root network edges
     * 
     */
    private void initializeEdgeAttributes() {

        /*CyAttributes cyAttributes = Cytoscape.getEdgeAttributes();

        for( int edge_index : rootNetwork.getEdgeIndicesArray()) {
            CyEdge edge = rootNetwork.getEdge( edge_index);

            // Assign a value of 1 to the edge attribute indicating the number of relationship the edge represents
            cyAttributes.setAttribute( edge.SUID, Constants.EDGE_PARAM_EDGES_NUMBER, 1);
            // Assign False to the edge attribute indicating whether the edge represent a interaction or not
            cyAttributes.setAttribute( edge.SUID, Constants.EDGE_PARAM_INTERACTION, Constants.TRUE);
        }*/
    }

    /**
     * Return the list of the cluster the node is assign to in the current analysis
     * 
     * @param node the Node to analyze
     * @return the list of the cluster the node is assign to in the current analysis
     */
    public ArrayList<String> getClusterList( CyNode node) {

        /*ArrayList<String> result = new ArrayList<String>();

        ArrayList<String> cluster_list = (ArrayList<String>) Cytoscape.getNodeAttributes().getListAttribute( node.getIdentifier(), Constants.NODE_PARAM_CLUSTER_LIST);

        // System.out.println( "AnalysisTask.getClusterList() : analysisName = " + analysisName);
        if( cluster_list != null) {
            for( String cluster_name : cluster_list) {
                // System.out.println( "AnalysisTask.getClusterList() :     cluster_name = " + cluster_name);
                if( cluster_name != null && cluster_name.startsWith( analysisName)) {
                    result.add( cluster_name);
                }
            }
        }

        return result.size() > 0 ? result : null;*/
    	return null;
    }

    /**
     * Returns the analyzed (root) network
     * 
     * @return the analyzed (root) network
     */
    public CyNetwork getRootNetwork() {

        return rootNetwork;
    }

    /**
     * Returns the analysis parameters
     * 
     * @return the analysis parameters
     */
    public ParameterSet getParameters() {

        return parameters;
    }

    /**
     * Returns the analysis ID
     * 
     * @return the analysis ID
     */
    public String getAnalysisName() {

        return analysisName;
    }

    /**
     * Returns the ID associated to the analysis
     * 
     * @return the ID associated to the analysis
     */
    public int getAnalysisId() {

        return analysisID;
    }

    /**
     * Indicates if the analysis correctly ended or not
     * 
     * @return true if the analysis ended normally, false if not
     */
    public boolean isCompletedSuccessfully() {

        return completedSuccessfully;
    }

    /**
     * Return an array of the discovered cluster
     * 
     * @return an array of the discovered cluster
     */
    /*public ArrayList<Cluster> getClusters() {

        return clusters;
    }*/

    /**
     * Returns the cluster associated to the given node
     * 
     * @param node The considered node
     * @return the cluster associated to the given node
     */
    /*public Cluster getCluster( CyNode node) {

        if( node != null) {
            for( Cluster cluster : clusters) {
                CyNode cy_node = cluster.getClusterNode();
                if( cy_node == node) {
                    return cluster;
                }
            }
        }

        return null;
    }*/

    /**
     * Return the cluster having the given name, if exists
     * 
     * @param cluster_name the name of the desired cluster
     * @return the cluster having the given name, if exists, null if not
     */
    /*public Cluster getCluster( String cluster_name) {

        for( Cluster cluster : clusters) {
            if( cluster != null && cluster.getClusterName().equals( cluster_name)) {
                return cluster;
            }
        }

        return null;
    }*/

    /**
     * Assign an array of Cluster as analysis discovered clusters
     * 
     * @param clusters an array of Cluster
     */
    /*public void setClusters( ArrayList<Cluster> clusters) {

        this.clusters = clusters;
    }*/

    /**
     * Stop the analysis
     * 
     */
    public void halt() {

        /*this.interrupted = true;
        CnSAlgorithm algo = parameters.getAlgorithmInstance();
        if( algo != null) {
            algo.setCancelled( true);
        }*/
    }

    /**
     * Inform the task monitor on the analysis status
     * 
     * @param taskMonitor the TaskMonitor
     */
    public void setTaskMonitor( TaskMonitor taskMonitor) throws IllegalThreadStateException {

        if( this.taskMonitor != null) {
            throw new IllegalStateException( "Task Monitor has already been set.");
        }
        this.taskMonitor = taskMonitor;
    }

    /**
     * Return the title of the analysis
     * 
     * @returns the title of the analysis
     */
    public String getTitle() {

        StringBuffer state = new StringBuffer( "Identifying Clusters...");
        return state.toString();
    }

    /**
     * Export the analysis result to file
     * 
     * @param output_file the output File
     * @return true if the output succeeded, false if not
     */
    public boolean exportClusters( File output_file) {

        /*boolean export_ok = true;

        if( output_file == null) {
        	LOGGER.error("Export : The provided output file is null");
            return false;
        }

        try {
            FileWriter writer = new FileWriter( output_file);
            // Write the file title
            writer.write( Constants.EXPORT_TITLE_TAG + "\n");
            // Write the main information
            writer.write( Constants.EXPORT_ALGORITHM_TAG + parameters.getAlgorithm() + "\n");
            writer.write( Constants.EXPORT_NETWORK_TAG + parameters.getNetworkID() + "\n");
            writer.write( Constants.EXPORT_SCOPE_TAG + parameters.getScope() + "\n");
            writer.flush();
            // Write the statistics on the repartition of clusters
            writer.write( "#Cluster ID (nb nodes in cluster, nb multi-classed nodes in cluster):\n");
            writer.write( "#");
            for( Cluster cluster : clusters) {
                writer.write( cluster.getClusterID() + "(" + cluster.getNodeIndexList().size() + "," + cluster.getMultiClassedNodeIndexList().size() + "), ");
            }
            writer.write( "\n");
            writer.flush();

            // Write the extra algorithm parameters if any
            HashMap<String, Object> params = parameters.getParameters();
            if( params != null && params.size() > 0) {
                for( Map.Entry<String, Object> entry : params.entrySet()) {
                    if( entry != null && entry.getKey() != null && entry.getValue() != null) {
                        writer.write( Constants.EXPORT_GENERIC_PARAMETER_TAG + entry.getKey() + "=" + entry.getValue().toString() + "\n");
                    }
                }
            }
            writer.flush();

            // Write the list of nodes in scope if any
            ArrayList<CyNode> nodes_in_scope = parameters.getNodesInScope();
            if( nodes_in_scope != null && nodes_in_scope.size() > 0) {
                for( CyNode node : nodes_in_scope) {
                    if( node != null) {
                        writer.write( Constants.EXPORT_NODE_IN_SCOPE_TAG + node.SUID + "\n");
                    }
                }
            }
            writer.flush();

            // Write the list of edges in scope if any
            ArrayList<CyEdge> edges_in_scope = parameters.getEdgesInScope();
            if( edges_in_scope != null && edges_in_scope.size() > 0) {
                for( CyEdge edge : edges_in_scope) {
                    if( edge != null) {
                        String id1 = edge.getSource().SUID;
                        String id2 = edge.getTarget().SUID;
                        if( id1.compareTo( id2) <= 0) {
                            writer.write( Constants.EXPORT_EDGE_IN_SCOPE_TAG + id1 + "\t" + id2 + "\n");
                        }
                        else {
                            writer.write( Constants.EXPORT_EDGE_IN_SCOPE_TAG + id2 + "\t" + id1 + "\n");
                        }
                    }
                }
            }
            writer.write( "\n");
            writer.write( "\n");
            writer.flush();

            // Write the definition of the clusters with their node list
            for( Cluster cluster : clusters) {
                // Write the cluster ID plus its annotations
                ArrayList<String> annotations = cluster.getAnnotations();
                StringBuffer annotation_line = new StringBuffer();
                if( annotations != null) {
                    for( String annotation : annotations) {
                        if( annotation != null && !annotation.isEmpty()) {
                            int index = annotation.indexOf( ":");
                            if( index >= 0 && index < annotation.length() - 2) {
                                String no_ref_annotation = annotation.substring( index + 1);
                                annotation_line.append( no_ref_annotation + "||");
                            }
                            else {
                                LOGGER.error("Export : An annotation was wrongly formatted from cluster '" + cluster.getClusterName() + "' : " + annotation);
                                export_ok = false;
                            }
                        }
                    }
                }
                writer.write( Constants.EXPORT_CLUSTER_TAG + cluster.getClusterID() + "||" + annotation_line.toString() + "\n");
                // Write the list of nodes in the cluster
                for( Integer index : cluster.getNodeIndexList()) {
                    CyNode node = rootNetwork.getNode( index);
                    if( node != null) {
                        writer.write( node.SUID + "\n");
                    }
                }
                writer.write( "\n");
            }
            writer.flush();
            writer.close();

        }
        catch( IOException ioe) {
            LOGGER.error("Export : Unable to open FileWriter on File : " + output_file.getAbsolutePath() + " : " + ioe);
            return false;
        }

        return export_ok;*/
    	return true;
    }

    /**
     * Import the clusters defined in the given input file respect to the given network
     * 
     * @param input_file the input file with the cluster definition
     * @param network the network relative to the input file
     * @return The AnalysisTask filled with all the information as if the analysis was run through a classic algorithm
     * @throws ClustnSeeException if some error is encountered during the parsing of the file
     */
    /*public static AnalysisTask importClusters( File input_file, CyNetwork network) throws ClustnSeeException {

        AnalysisTask analysis = null;

        try {
            FileReader reader = new FileReader( input_file);
            BufferedReader buf_reader = new BufferedReader( reader);
            // read the first line
            String line = buf_reader.readLine();
            if( !line.startsWith( Constants.EXPORT_TITLE_TAG) && !line.startsWith( Constants.EXPORT_OLD_TITLE_TAG)) {
            	LOGGER.error("The provided file is not a Clust&See export file : " + input_file.getAbsolutePath());
                throw new ClustnSeeException( "The provided file is not a Clust&See export file.");
            }
            // Initialize the analysis task
            ParameterSet parameters = new ParameterSet();
            int analysis_id = AnalysisManager.getManager().getNewAnalysisID( network);
            analysis = new AnalysisTask( network, analysis_id, parameters);
            ArrayList<CyNode> nodes_in_scope = new ArrayList<CyNode>();
            ArrayList<CyEdge> edges_in_scope = new ArrayList<CyEdge>();
            ArrayList<Cluster> clusters = new ArrayList<Cluster>();
            // Parse the rest of the file
            while( line != null) {
                // Read the algorithm name
                if( line.startsWith( Constants.EXPORT_ALGORITHM_TAG)) {
                    parameters.setAlgorithm( line.substring( Constants.EXPORT_ALGORITHM_TAG.length()));
                }
                // Read the network name
                else if( line.startsWith( Constants.EXPORT_NETWORK_TAG)) {
                    String network_name = line.substring( Constants.EXPORT_NETWORK_TAG.length());
                    if( !network.getTitle().equals( network_name)) {
                    	LOGGER.error("The current network does not correspond to the cluster file network");
                        throw new ClustnSeeException( "The current network does not correspond to the cluster file network.");
                    }
                    parameters.setNetworkID( network_name);
                }
                // Read the scope
                else if( line.startsWith( Constants.EXPORT_SCOPE_TAG)) {
                    String scope = line.substring( Constants.EXPORT_SCOPE_TAG.length());
                    if( !scope.equals( ParameterSet.SELECTION) && !scope.equals( ParameterSet.NETWORK)) {
                    	LOGGER.error("The scope of the imported file was not recognized : " + scope + ".");
                        throw new ClustnSeeException( "The scope of the imported file was not recognized : " + scope + ".");
                    }
                    parameters.setScope( scope);
                }
                // Read the list of nodes in scope
                else if( line.startsWith( Constants.EXPORT_NODE_IN_SCOPE_TAG)) {
                    String node_id = line.substring( Constants.EXPORT_NODE_IN_SCOPE_TAG.length());
                    CyNode node = Cytoscape.getCyNode( node_id, false);
                    if( node == null) {
                    	LOGGER.error("A node in the imported file is not present in Cytoscape : " + node_id + ".");
                        throw new ClustnSeeException( "A node in the imported file is not present in Cytoscape : " + node_id + ".");
                    }
                    node = network.getNode( node.getRootGraphIndex());
                    if( node == null) {
                    	LOGGER.error("A node in the imported file is not present in the current network : " + node_id + ".");
                        throw new ClustnSeeException( "A node in the imported file is not present in the current network : " + node_id + ".");
                    }
                    nodes_in_scope.add( node);
                }
                // Read the list of edges in scope
                else if( line.startsWith( Constants.EXPORT_EDGE_IN_SCOPE_TAG)) {
                    String edge_line = line.substring( Constants.EXPORT_EDGE_IN_SCOPE_TAG.length());
                    String[] node_ids = edge_line.split( "\t");
                    if( node_ids.length != 2) {
                    	LOGGER.error("An edge is wrongly formatted in the imported file : " + edge_line);
                        throw new ClustnSeeException( "An edge is wrongly formatted in the imported file : " + edge_line);
                    }
                    CyNode source = Cytoscape.getCyNode( node_ids[ 0]);
                    CyNode target = Cytoscape.getCyNode( node_ids[ 1]);
                    CyNode node1 = network.getNode( source.getRootGraphIndex());
                    CyNode node2 = network.getNode( target.getRootGraphIndex());
                    if( node1 == null) {
                    	LOGGER.error("A node in the imported file is not present in the current network : " + node_ids[ 0] + ".");
                        throw new ClustnSeeException( "A node in the imported file is not present in the current network : " + node_ids[ 0] + ".");
                    }
                    if( node2 == null) {
                    	LOGGER.error("A node in the imported file is not present in the current network : " + node_ids[ 1] + ".");
                        throw new ClustnSeeException( "A node in the imported file is not present in the current network : " + node_ids[ 1] + ".");
                    }
                    CyEdge edge = Cytoscape.getCyEdge( node1, node2, null, null, false, false);
                    if( edge == null) {
                    	LOGGER.error("An edge in the imported file is not present in Cytoscape : " + node_ids[ 0] + "<-->" + node_ids[ 1] + ".");
                        throw new ClustnSeeException( "A edge in the imported file is not present in Cytoscape  : " + node_ids[ 0] + "<-->" + node_ids[ 1] + ".");
                    }
                    edge = network.getEdge( edge.getRootGraphIndex());
                    if( edge == null) {
                    	LOGGER.error("An edge in the imported file is not present in the current network : " + node_ids[ 0] + "<-->" + node_ids[ 1] + ".");
                        throw new ClustnSeeException( "A edge in the imported file is not present in the current network : " + node_ids[ 0] + "<-->" + node_ids[ 1] + ".");
                    }
                    edges_in_scope.add( edge);
                }
                // Read the other algorithm generic parameters
                else if( line.startsWith( Constants.EXPORT_GENERIC_PARAMETER_TAG)) {
                    String param_line = line.substring( Constants.EXPORT_GENERIC_PARAMETER_TAG.length());
                    int index = param_line.indexOf( "=");
                    if( index > 0 && index < param_line.length() - 2) {
                        String param_name = param_line.substring( 0, index);
                        String param_value = param_line.substring( index + 1);
                        if( !param_name.isEmpty() && !param_value.isEmpty()) {
                            parameters.setParameter( param_name, param_value);
                        }
                    }
                }
                // Read the definition of a cluster
                else if( line.startsWith( Constants.EXPORT_CLUSTER_TAG)) {
                    String cluster_definition_string = line.substring( Constants.EXPORT_CLUSTER_TAG.length());
                    String[] cluster_definition = cluster_definition_string.split( "\\|\\|");
                    if( cluster_definition.length < 1) {
                    	LOGGER.error("One of the cluster ID is wrongly formatted : " + cluster_definition_string + ".");
                        throw new ClustnSeeException( "One of the cluster ID is wrongly formatted : " + cluster_definition_string + ".");
                    }
                    int cluster_id;
                    ArrayList<String> annotations = new ArrayList<String>();
                    try {
                        cluster_id = Integer.parseInt( cluster_definition[ 0]);
                        for( int i = 1; i < cluster_definition.length; i++) {
                            annotations.add( Constants.PARTITION_PREFIX + analysis_id + ":" + cluster_definition[ i]);
                        }
                    }
                    catch( NumberFormatException nfe) {
                    	LOGGER.error("One of the cluster ID is wrongly formatted (integer expected) : " + cluster_definition[ 0]);
                        throw new ClustnSeeException( "One of the cluster ID is wrongly formatted (integer expected) : " + cluster_definition[ 0]);
                    }
                    // Initialize the cluster
                    Cluster cluster = new Cluster( cluster_id, analysis);
                    cluster.setAnnotations( annotations);
                    line = buf_reader.readLine();
                    // Read the list of nodes in the cluster
                    ArrayList<Integer> cluster_nodes = new ArrayList<Integer>();
                    while( line != null && !line.isEmpty()) {
                        CyNode node = Cytoscape.getCyNode( line, false);
                        node = network.getNode( node.getRootGraphIndex());
                        if( node == null) {
                        	LOGGER.error("A node in the imported file is not present in the current network : " + line + ".");
                            throw new ClustnSeeException( "A node in the imported file is not present in the current network : " + line + ".");
                        }
                        cluster_nodes.add( new Integer( node.getRootGraphIndex()));
                        line = buf_reader.readLine();
                    }
                    // Finalize the cluster
                    cluster.setNodeIndexList( cluster_nodes);
                    // GraphPerspective gpCluster = Algorithm.createGraphPerspective( cluster_nodes, network);
                    // cluster.setGPCluster( gpCluster);
                    clusters.add( cluster);
                }
                line = buf_reader.readLine();
            }
            // Finalize the analysis ParameterSet
            parameters.setNodesInScope( nodes_in_scope);
            parameters.setEdgesInScope( edges_in_scope);
            ParametersManager.registerParams( parameters, analysis.getAnalysisName(), network.SUID);

            // Finalize the AnalysisTask
            analysis.setClusters( clusters);
            analysis.analyseClusters();
            analysis.analyseClusterNodesAttributes();
            analysis.createClusterNodes();
            analysis.initializeEdgeAttributes();
            AnalysisManager.getManager().registerAnalysis( analysis);
        }
        catch( FileNotFoundException fnfe) {
        	LOGGER.error("Unable to open FileReader on File : " + input_file.getAbsolutePath() + " : " + fnfe);
            return null;
        }
        catch( IOException ioe) {
        	LOGGER.error("Unable to open FileReader on File : " + input_file.getAbsolutePath() + " : " + ioe);
            return null;
        }

        return analysis;
    }
*/
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
