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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class CnSOCGAlgorithm extends CnSAlgorithm {
	public static final String NAME = "OCG";
	public static final int OPTIONS = 1002;
	public static final int INITIAL_CLUSTERS = 1003;
	public static final int CLASS_SYSTEM = 1004;
	public static final int CLUSTER_MAX_CARDINAL = 1005;
	public static final int CLUSTER_MIN_NUMBER = 1006;
	protected static CnSAlgorithm instance = null;
	int[]                           Kard, Q, BestKard;
    int                               NbCliq, NbClIni, NbClas, NbCar, BestNbClas, verbose, FuStyl = 0, logfile = 0;
    long[][]                          Var;
    char                              MaxCar                                       = 0;
    double                            Krit, BestCrit, OldCrit, eps = .001, SumDg2 = 0.;

    /* Choice of Initial Class System. Choices are: */
    /* 1 : Maximal Cliques */
    /* 2 : Edges */
    /* 3 : Centered Cliques (default) */
    int                               typ                                          = 3;

    String 								 optionsChoice								  = "Default options";

    /* Maximum allowed class cardinality. Can take any */
    /* integer value from 0, the default meaning no */
    /* constraint upwards */
    int                               CardMax                                      = 0;

    /* Choice of class system for Centered Cliques. Choices are: */
    /* 0: Final class system, needs the expected minimum */
    /* number of clusters and the maximum cardinality */
    /* of the final clusters. */
    /* 1: The class system that maximizes modularity (default) */
    int                               FCS                                          = 1;

    // Minimum number of expected classes (0 means no constraint)
    int                               ClCh                                         = 2;

    // N is the number of nodes
    int                               N                                            = 0;
    // Na is the number of edges
    int                               Na                                           = 0;

    // A is the to dimensional adjacency table : A[i][j]=A[j][i] iff vertices i and j are linked
    int[][]                           A;
    // B is the to dimensional table containing the (integer) modularity value of any pair (i,j)
    int[][]                           B;
    // ModMax is the maximal value reached by the modularity
    double                            ModMax;
    // Clas is a one dimensional array : Clas[i] is the number of class the node i is in
    int[]                           Clas;
    // Cl represent a classes
    // It is a double array: first index: class number, second index: number of the node in the class,
    // value = id of the node
    int[][]                         Cl;
    // BestCl represents the best classes encountered during the computation
    // It is a double array: first index: class number, second index: number of the node in the class,
    // value = id of the node
    int[][]                         BestCl;
    // Dg is a one dimensional array : Dg[i] is the degree (nb. of adjacent vertices) of vertex i
    int[]                           Dg;
    // DgMax is the maximum degree reach by the nodes
    int                               DgMax;
    // Names of the nodes
    String[]                          nodeName;
    // Map that convert cytoscape node indexes to algorithm indexes
    private HashMap<Long, Integer> cyto2ModClust;

    private CnSAlgorithmParameter options, initialClusters, classSystem, clusterMaxCardinal, clusterMinNumber;

	private CnSOCGAlgorithm() {
		super();
		parameters = new CnSAlgorithmParameters();
		parameters.addParameter("Options", OPTIONS, "Default options");
		parameters.addParameter("Initial clusters", INITIAL_CLUSTERS, "Centered cliques");
		parameters.addParameter("Class system", CLASS_SYSTEM, "Maximize modularity");
		parameters.addParameter("Cluster max. cardinal", CLUSTER_MAX_CARDINAL, 0);
		parameters.addParameter("Cluster min. number", CLUSTER_MIN_NUMBER, 2);
	}

	public static CnSAlgorithm getInstance() {
		if (instance == null) instance = new CnSOCGAlgorithm();
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm#execute(org.cytoscape.model.CyNetwork, org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmParameters)
	 */
	@Override
	public CnSAlgorithmResult execute(CyNetwork network) {
		CnSAlgorithmResult complexes = run( network);

        return complexes;
	}
	public CnSAlgorithmResult run( CyNetwork inputNetwork) {
        verbose = 1;
        //CnSEvent ev = new CnSEvent(CnSAlgorithmManager.GET_ALGORITHM_PARAMETERS, CnSEventManager.ALGORITHM_MANAGER);
		//CnSAlgorithmParameters params = (CnSAlgorithmParameters)CnSEventManager.handleMessage(ev);

        options = getParameters().getParameter(OPTIONS);
        initialClusters = getParameters().getParameter(INITIAL_CLUSTERS);
        classSystem = getParameters().getParameter(CLASS_SYSTEM);
        clusterMaxCardinal = getParameters().getParameter(CLUSTER_MAX_CARDINAL);
        clusterMinNumber = getParameters().getParameter(CLUSTER_MIN_NUMBER);
        return runOCG( inputNetwork);
    }

	private CnSAlgorithmResult runOCG( CyNetwork inputNetwork) {
        int VarMod, card = 1;
        long Mod, BestMod = 0;
        double var, VarMax;

        /* Establish a hierarchy of overlapping classes optimizing the modularity criterion */
        readNetwork( inputNetwork);
        if( FCS == 1) {
            CardMax = N;
            ClCh = 2;
        }
        else {
            if( CardMax == 0) {
                CardMax = N;
            }
            if( ClCh == 0) {
                ClCh = 2;
            }
        }
        optionsChoice = options.getValue().toString();
        //typ = initialClusters.getValue().toString();

        if (optionsChoice.equals("Default options"))
        	NbClIni = StarCliq();
        else 
        	switch( typ) {
        		case 1:
        			NbClIni = Clique();
        			break;

        		case 2:
        			NbClIni = ClasArete( CardMax);
        			break;

        		case 3:
        			NbClIni = StarCliq();
        	}
        /*if (typ.equals("Maximal cliques")) 
            NbClIni = Clique();
        else if (typ.equals("Edges"))
            NbClIni = ClasArete( CardMax);
        else if (typ.equals("Centered cliques"))
            NbClIni = StarCliq();*/

        //JOptionPane.showConfirmDialog(null,  NbClIni);

        Mod = Modularity();
        // System.out.println( "OCG.runOCG() : modularity = " + Mod);

        // MMod = 1. * Mod / Na / Na / 2 - SumDg2 / Na / Na / 4;

        /**********************************************************************************************/
        /* Matrix Var contains two types of information, one in the upper right and the other */
        /* in the bottom left. */
        /*                                                    */
        /* When i > j, Var[i][j] is the modularity variation given by the fusion of classes i and j. */
        /* When i < j, Var[i][j] = 1 if they can be merged and 0 if not. */
        /* Two classes can be merged if they are connected and if their fusion does not pass CardMax. */
        /**********************************************************************************************/

        Var = new long[NbClIni][NbClIni]; // malloc((NbClIni) * sizeof(long *));
        BestCl = new int[NbClIni][NbClIni]; // malloc((NbClIni) * sizeof(short *));
        BestKard = new int[NbClIni + 1]; // malloc((NbClIni+1) * sizeof(short));

        for( int i = 0; i < NbClIni; i++) {
            Var[ i][ i] = -1;
            for( int j = 0; j < i; j++) {
                Var[ i][ j] = -Mod;
                Var[ j][ i] = 0;
            }
        }

        // Initialization of the Var table comparing any two initial classes
        for( int cl1 = 1; cl1 < NbClIni; cl1++) {
            for( int cl2 = 0; cl2 < cl1; cl2++) {
                VarMod = 0;
                int fus1 = 0;
                for( int i = 0; i < N; i++) {
                    Clas[ i] = 0;
                }
                for( int k1 = 0; k1 < Kard[ cl1]; k1++) {
                    int i = Cl[ cl1][ k1];
                    Clas[ i] = 1;
                    for( int k2 = 0; k2 < Kard[ cl2]; k2++) {
                        int j = Cl[ cl2][ k2];
                        Clas[ j] = 1;
                        if( i == j) {
                            fus1 = 1; // Classes connexes
                        }
                        else if( i < j && A[ j][ i] == 1) {
                            fus1 = 1; // Connected classes
                        }
                        else if( i > j && A[ i][ j] == 1) {
                            fus1 = 1; // Connected classes
                        }

                        if( i < j) {
                            VarMod += (1 - A[ i][ j]) * B[ i][ j];
                        }
                        else if( i > j) {
                            VarMod += (1 - A[ j][ i]) * B[ i][ j];
                        }
                    }
                    int kk = 0;
                    for( int k = 0; k < N; k++) {
                        if( Clas[ k] > 0) { // Note test was if( Clas[k]){...}
                            kk++;
                        }
                    }
                    if( kk > CardMax) {
                        fus1 = 0;
                    }
                }
                Var[ cl1][ cl2] = VarMod;
                Var[ cl2][ cl1] = fus1;
            }
        }

        // for( int ivari = 0; ivari < NbClIni; ivari++){
        // for( int jvarj = 0; jvarj < NbClIni; jvarj++){
        // System.out.println( "var " + ivari + ":" + jvarj + "=" + Var[ivari][jvarj]);
        // }
        // }

        NbClas = NbClIni;

        card = 1;
        while( NbClas > ClCh) {

            VarMax = -1. * ModMax;
            int cl1 = -1;
            int cl2 = -1;
            for( int ii = 1; ii < NbClIni; ii++) {
                if( Kard[ ii] == 0) {
                    continue; // old classes are indicated by setting their cardinality to 0
                }
                for( int jj = 0; jj < ii; jj++) {
                    if( Kard[ jj] == 0) {
                        continue;
                    }
                    // Can we fuse them?
                    if( Var[ jj][ ii] == 0) {
                        continue; // no
                    }
                    if( FuStyl == 0) {
                        card = Kard[ ii] * Kard[ jj]; // average gain
                    }
                    var = 1. * Var[ ii][ jj] / card;
                    if( var > VarMax) {
                        VarMax = var;
                        cl1 = jj;
                        cl2 = ii;
                    }
                }
            }
            if( cl1 < 0) {
                break;
            }
            for( int k1 = 0; k1 < Kard[ cl1]; k1++) {
                int i = Cl[ cl1][ k1];
                for( int k2 = 0; k2 < Kard[ cl2]; k2++) {
                    int j = Cl[ cl2][ k2];
                    // The joined pairs are indicated in the lower left part of A
                    if( i < j) {
                        A[ i][ j] = 1;
                    }
                    else if( i > j) {
                        A[ j][ i] = 1;
                    }
                }
            }

            Mod = Modularity();

            for( int k = 0; k < N; k++) {
                Clas[ k] = 0;
            }
            for( int k1 = 0; k1 < Kard[ cl1]; k1++) {
                Clas[ Cl[ cl1][ k1]] = 1;
            }
            for( int k2 = 0; k2 < Kard[ cl2]; k2++) {
                Clas[ Cl[ cl2][ k2]] = 1;
            }
            short kk = 0;
            for( short k = 0; k < N; k++) {
                if( Clas[ k] > 0) { // note : test was if(Clas[k]){...}
                    Cl[ cl1][ kk] = k;
                    kk++;
                }
            }

            Kard[ cl1] = kk;
            Kard[ cl2] = 0; // Cl[cl1][] will contain the new class

            if( Mod >= BestMod) {
                kk = 0;
                for( int k = 0; k < NbClIni; k++) {
                    if( Kard[ k] == 0) {
                        continue;
                    }
                    for( int i = 0; i < Kard[ k]; i++) {
                        BestCl[ kk][ i] = Cl[ k][ i];
                    }
                    BestKard[ kk] = Kard[ k];
                    kk++;
                }
                BestNbClas = NbClas - 1;
                BestMod = Mod;
            }
            // Updating table Var
            for( int ii = 0; ii < NbClIni; ii++) {
                if( Kard[ ii] == 0 || cl1 == ii || cl2 == ii) {
                    continue;
                }
                long fus1 = 0;
                long fus2 = 0;
                if( ii < cl1) {
                    fus1 = Var[ ii][ cl1];
                }
                else {
                    fus1 = Var[ cl1][ ii];
                }
                if( ii < cl2) {
                    fus2 = Var[ ii][ cl2];
                }
                else {
                    fus2 = Var[ cl2][ ii];
                }
                if( fus1 == 0 && fus2 == 0) { // Can be fused with neither one nor the other
                    if( ii < cl1) {
                        Var[ ii][ cl1] = 0;
                    }
                    else {
                        Var[ cl1][ ii] = 0;
                    }
                    continue;
                }
                // verifying the cardinality condition and computing Var[ii][cl1]
                VarMod = 0;
                for( int k = 0; k < N; k++) {
                    Clas[ k] = 0;
                }
                for( int k = 0; k < Kard[ ii]; k++) {
                    int i = Cl[ ii][ k];
                    Clas[ i] = 1;
                    for( int k1 = 0; k1 < Kard[ cl1]; k1++) {
                        int j = Cl[ cl1][ k1];
                        Clas[ j] = 1;
                        if( i < j) {
                            VarMod += (1 - A[ i][ j]) * B[ i][ j];
                        }
                        else if( i > j) {
                            VarMod += (1 - A[ j][ i]) * B[ i][ j];
                        }
                    }
                }

                kk = 0;
                // cardinality of the eventual fusion
                for( int k = 0; k < N; k++) {
                    if( Clas[ k] > 0) {
                        kk++;
                    }
                }
                if( kk > CardMax) {
                    fus1 = 0;
                }
                else {
                    fus1 = 1;
                }

                if( ii > cl1) {
                    Var[ ii][ cl1] = VarMod;
                    Var[ cl1][ ii] = fus1;
                }
                else {
                    Var[ cl1][ ii] = VarMod;
                    Var[ ii][ cl1] = fus1;
                }
            }
            NbClas--;
        }
        // Going back to classes making the largest modularity
        if( FCS == 1) {
            for( int k = 0; k < BestNbClas; k++) {
                Kard[ k] = BestKard[ k];
                for( int i = 0; i < Kard[ k]; i++) {
                    Cl[ k][ i] = BestCl[ k][ i];
                }
            }
            NbClas = BestNbClas;
            for( int k = NbClas + 1; k < NbClIni; k++) {
                Kard[ k] = 0;
            }
        }
        else {
            int kk = 0;
            for( int k = 0; k < NbClIni; k++) {
                if( Kard[ k] == 0) {
                    continue;
                }
                for( int i = 0; i < Kard[ k]; i++) {
                    Cl[ kk][ i] = Cl[ k][ i];
                }
                Kard[ kk] = Kard[ k];
                kk++;
            }
            NbClas = kk;
        }

        // Making singletons with vertices having a negative contribution to the mudularity of their class
        Effacer();

        for( int i = 0; i < N - 1; i++) {
            for( int j = i + 1; j < N; j++) {
                A[ i][ j] = 0;
            }
        }
        for( int k = 0; k < NbClas; k++) {
            for( int i = 0; i < Kard[ k] - 1; i++) {
                int ii = Cl[ k][ i];
                for( int j = i + 1; j < Kard[ k]; j++) {
                    int jj = Cl[ k][ j];
                    if( ii < jj) {
                        A[ ii][ jj] = 1;
                    }
                    else {
                        A[ jj][ ii] = 1;
                    }
                }
            }
        }

        Mod = Modularity();

        ClasOut( NbClas, Mod);
        HashMap<Integer, Long> modClust_to_cyto = reverseMap( cyto2ModClust);
        
        return new CnSAlgorithmResult(Cl, Kard, NbClas, modClust_to_cyto);
    }

	/**
     * Retrieve the information from the analyzed network
     * 
     * @param inputNetwork The network to analyze
     * @param analysis The contextual analysis
     */
    private void readNetwork( CyNetwork inputNetwork) {
    	Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        CnSEvent ev = new CnSEvent(CnSAlgorithmEngine.GET_SCOPE, CnSEventManager.ALGORITHM_ENGINE);
        String scope = (String)CnSEventManager.handleMessage(ev);

        optionsChoice = options.getValue().toString();
        if (initialClusters.getValue().toString().equals("Maximal cliques"))
        	typ = 1;
        else if (initialClusters.getValue().toString().equals("Edges"))
        	typ = 2;
        else if (initialClusters.getValue().toString().equals("Centered cliques"))
        	typ = 3;

        CardMax = ((Integer)clusterMaxCardinal.getValue()).intValue();
        if (classSystem.getValue().toString().equals("Maximize modularity"))
        	FCS = 1;
        else if (classSystem.getValue().toString().equals("Final class"))
        	FCS = 0;
        ClCh = ((Integer)clusterMinNumber.getValue()).intValue();

        verbose = 1;
        logfile = 1;

        if( typ != 3) {
        	LOGGER.error("Choice of class system should be 'Final Class System' (-s 0) unless Centered Cliques (-i 3) were chosen as the Initial Class System.");
            return;
        }

        if( typ != 1 && typ != 2 && typ != 3) {
        	LOGGER.error("Parameter error : Type of initial classes: maximal cliques (1) edges (2) centered cliques (3) (default=3)");
            return;
        }

        if( FCS == 0) {
            if( CardMax == 0 || ClCh == 2) {
            	LOGGER.error("Parameter error : The Final Class System (-s 0) requires either limiting the maximum class cardinality or, setting a minimum number of expected classes (-m)");
                return;
            }
        }

        if( FCS != 0 && FCS != 1) {
        	LOGGER.error("Parameter error : Choose either Final Class System (0) or the one maximizing the modularity (1)  (default=0)");
            return;
        }

        if( typ == 1 || typ == 2 && FCS == 1) {
        	LOGGER.error("Parameter error : The final Class System maximizing the modularity (-s 1) can only be used with the centered cliques (-i 3): " + FCS + ":" + CardMax + ": (-c 0)");
            return;
        }

        if( FCS == 1 && CardMax != 0) {
        	LOGGER.error("Parameter error : The final Class System maximizing the modularity (-s 1) can only be used without setting the maximum cardinality for each class\n");
            return;
        }

        if( FCS == 1 && ClCh != 2) {
        	LOGGER.error("Parameter error : The final Class System maximizing the modularity can only be used with a minimum number of expected classes of 2 (-m 2). Try using '-s 0'.");
            return;
        }

        Iterator<CyNode> ni;
        Iterator<CyEdge> edges;
        List<CyNode> selectedNodes = CyTableUtil.getNodesInState(inputNetwork, "selected", true);
        List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(inputNetwork, "selected", true);
        if (scope.equals("Selection")) {
            N = selectedNodes.size();
            Na = selectedEdges.size();
            ni = selectedNodes.iterator();
            edges = selectedEdges.iterator();
        }
        else {
            N = inputNetwork.getNodeCount();
            Na = inputNetwork.getEdgeCount();
            ni = inputNetwork.getNodeList().iterator();
            edges = inputNetwork.getEdgeList().iterator();
        }

        // initialize the matrices and other parameters
        A = new int[N][N]; // malloc((N) * sizeof(char *));
        B = new int[N][N]; // malloc((N) * sizeof(int *));
        Clas = new int[N]; // malloc((N) * sizeof(short));
        Dg = new int[N];
        for( int i = 0; i < N; i++) {
            for( int j = 0; j < N; j++) {
                A[ i][ j] = 0;
            }
        }
        nodeName = new String[N];
        cyto2ModClust = new HashMap<Long, Integer>( N);

        // Retrieve the list of node names
        HashMap<String, CyNode> name_to_node = new HashMap<String, CyNode>();
        ev = new CnSEvent(CnSAlgorithmEngine.IS_CANCELLED, CnSEventManager.ALGORITHM_ENGINE);
        boolean cancelled = (Boolean)CnSEventManager.handleMessage(ev);

        for( int i = 0; ni.hasNext() && !cancelled; i++) {
            CyNode node = ni.next();
            nodeName[ i] = inputNetwork.getRow(node).get(CyNetwork.NAME, String.class);;
            name_to_node.put( nodeName[ i], node);
        }

        // Order the nodes by alphabetical order and create a table to
        // convert the node index in Cytoscape into an index between 0 and N
        List<String> node_names = Arrays.asList( nodeName);
        Collections.sort( node_names);
        for( int i_node_name = 0; i_node_name < node_names.size(); i_node_name++) {
            String node_name = node_names.get( i_node_name);
            nodeName[ i_node_name] = node_name;
            long cyto_index = name_to_node.get( node_name).getSUID();
            cyto2ModClust.put( cyto_index, i_node_name);
        }

        // Build the proximity matrix
        int count_edges = 0;
        while( edges.hasNext() && !cancelled) {
            CyEdge e = edges.next();
            Integer i = cyto2ModClust.get(e.getSource().getSUID());
            Integer j = cyto2ModClust.get(e.getTarget().getSUID());
            // Assign a value to the proximity matrix (removing loop edges)
            if (i != null && j != null)
            if( i.intValue() != j.intValue()) {
                A[ i.intValue()][ j.intValue()] = 1;
                A[ j.intValue()][ i.intValue()] = 1;
                count_edges++;
                // System.out.println( "A " + i + ":" + j + " -- " + nodeName[ i] + ":" + nodeName[j]);
            }
        }
        Na = count_edges;

        // Evaluates the vertices degree, and DgMax, its maximum value
        DgMax = 0;
        for( int i = 0; i < N; i++) {
            Dg[ i] = 0;
            for( int j = 0; j < N; j++) {
                if( A[ i][ j] > 0) {
                    Dg[ i]++;
                }
                if( Dg[ i] > DgMax) {
                    DgMax = Dg[ i];
                }
                SumDg2 = SumDg2 + 1. * Dg[ i] * Dg[ i];
            }
        }

        // Evaluates the Modularity values and ModMax, its maximum
        ModMax = 0;
        for( int i = 1; i < N; i++) {
            for( int j = 0; j < i; j++) {
                B[ i][ j] = 2 * Na * A[ i][ j] - Dg[ i] * Dg[ j];
                B[ j][ i] = B[ i][ j];
                ModMax += A[ i][ j] * B[ i][ j];
            }
        }

        return;
    }

    /**
     * Enumeration of all the maximal cliques of a graph Barthelemy & Guenoche, Trees and Proximity Representations, J.
     * Wiley, 1991
     * 
     * @return
     */
    private int Clique() {
    	Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        if( verbose == 1) {
            LOGGER.debug("Starting Initial classes : maximal cliques");
        }

        int NbY, Nl, Long, cli, flag1, flag2;

        Q = new int[N]; // malloc((N) * sizeof(short));
        int[] X = new int[N]; // malloc((N) * sizeof(short));
        int[] Y = new int[N]; // malloc((N) * sizeof(short));

        // Cl is a two dimensional table that will contain all the maximal cliques as initial classes
        // Each row corresponds to a clique

        Nl = 2 * N;
        Long = Nl; // Allocating Nl rows
        Cl = new int[Nl][N]; // malloc((Nl) * sizeof(short *));

        Cl[ 0][ 0] = 1;
        for( int j = 1; j < N; j++) {
            if( A[ j][ 0] > 0) { // Note: test was if( A[j][0]){...}
                Cl[ 0][ j] = 1;
            }
            else {
                Cl[ 0][ j] = 0;
            }
        }

        NbCliq = 1;

        // Sequential algorithm described in the book
        for( short i = 1; i < N - 1; i++) {
            for( int j = 0; j < i; j++) {
                X[ j] = 0;
            }
            X[ i] = 1;
            for( int j = i + 1; j < N; j++) {
                if( A[ j][ i] > 0) { // note the test was if( A[j][i]){...}
                    X[ j] = 1;
                }
                else {
                    X[ j] = 0;
                }
            }

            // Allocating again
            cli = 0;
            for( int k = 0; k < NbCliq; k++) {
                if( Cl[ k][ i] > 0) {
                    cli++;
                }
            }

            if( NbCliq + 2 * cli + 1 > Long) {
                int NewLong = Long + Nl;
                int[][] new_Cl = new int[NewLong][N];
                System.arraycopy( Cl, 0, new_Cl, 0, Cl.length);
                // Cl = realloc(Cl,NewLong*sizeof(short *));
                // assert (Cl != NULL);
                // for (k=Long; k<NewLong; k++){
                // Cl[k] = malloc(N * sizeof(short));
                // assert (Cl[k] != NULL);
                // }
                Cl = new_Cl;
                Long = NewLong;
            }
            for( int k = 0; k < NbCliq; k++) {
                if( Cl[ k][ i] == 0) {
                    continue;
                }
                NbY = 0;
                for( int j = i + 1; j < N; j++) {
                    if( Cl[ k][ j] == 1 && X[ j] == 0) {
                        Y[ NbY] = j;
                        NbY++;
                    }
                }

                if( NbY > 0) {
                    for( int j = 0; j < N; j++) {
                        Q[ j] = Cl[ k][ j];
                    }
                    Cl[ k][ 0] = -1;
                    Q[ i] = 0;
                    flag1 = Inclus();
                    Q[ i] = 1;
                    for( int j = 0; j < NbY; j++) {
                        Q[ Y[ j]] = 0;
                    }
                    flag2 = Inclus();
                    if( flag1 + flag2 == 2) {
                        continue;
                    }
                    for( int j = 0; j < N; j++) {
                        Cl[ k][ j] = Cl[ NbCliq - 1][ j];
                    }
                    NbCliq--;
                }
            }
            for( int j = 0; j < N; j++) {
                Q[ j] = X[ j];
            }
            Inclus();
        }
        Kard = new int[NbCliq]; // malloc((NbCliq) * sizeof(short));
        // Eliminating the unused rows and coding cliques as item listes
        int k = 0;
        for( int i = 0; i < NbCliq; i++) {
            if( Cl[ i][ 0] < 0) {
                continue;
            }
            short kk = 0;
            for( short j = 0; j < N; j++) {
                if( Cl[ i][ j] > 0) { // Note : test was if( Cl[i][j]){...}
                    Cl[ k][ kk] = j;
                    kk++;
                }
            }
            Kard[ k] = kk;
            k++;
        }
        NbCliq = k;

        return NbCliq;
    }

    /**
     * Search if array Q is included in one of the cliques in Table Cl If it is not the case, it is added
     * 
     * @return A if the array was included, 0 if not
     */
    private int Inclus() {

        int flag;
        for( int k = 0; k < NbCliq; k++) {
            if( Cl[ k][ 0] < 0) {
                continue;
            }
            flag = 1;
            for( int j = 0; j < N; j++) {
                if( Cl[ k][ j] < Q[ j]) {
                    flag = 0;
                    break;
                }
            }
            if( flag == 1) {
                return 1;
            }
        }

        // Not included
        for( int j = 0; j < N; j++) {
            Cl[ NbCliq][ j] = Q[ j];
        }

        NbCliq++;

        return 0;
    }

    /**
     * The initial class system is the set of edges Cl is a two dimensional table that will contain all the initial
     * classes Kard is an array containing the number of elements in each class
     * 
     * @param CardMax
     * @return
     */
    private int ClasArete( int CardMax) {
    	Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        if( verbose == 1) {
            LOGGER.debug("Starting Initial classes : edges");
        }

        Cl = new int[Na][CardMax]; // malloc((Na) * sizeof(short *));
        Kard = new int[Na]; // malloc((Na) * sizeof(short));
        for( int i = 0; i < Na; i++) {
            // Cl[i] = malloc (CardMax * sizeof(short));
            Kard[ i] = 2;
        }
        int k = 0;
        for( short i = 1; i < N; i++) {
            for( short j = 0; j < i; j++) {
                if( A[ i][ j] > 0) { // Note: test was if( A[i][j]){...}
                    Cl[ k][ 0] = j;
                    Cl[ k][ 1] = i;
                    k++;
                }
            }
        }
        return k;
    }

    /**
     * Compute the centered cliques as initial class system Cl is a two dimensional table that will contain all the
     * centered cliques as initial classes Kard is an array containing the number of elements in each clique D is a one
     * dimensional array indicating the internal degre of each vertex (nb. of adjacent vertices in its class) X is a
     * working vector
     * 
     * @return
     */
    private int StarCliq() {
    	Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        if( verbose == 1) {
            LOGGER.debug("Starting Initial classes : centered cliques");
        }

        // for( int i = 0; i < N; i++){
        // for( int j = 0; j < N; j ++){
        // if( A[i][j] != 0){
        // System.out.println(i + ":" + j + "=" + A[i][j]);
        // }
        // }
        // }

        int DgMax, /*mis,*/ NbClas = 0;
        short[] X = new short[N];
        short[] D = new short[N];

        Cl = new int[N][N]; // malloc((N) * sizeof(short *));
        Kard = new int[N]; // malloc((N) * sizeof(short));
        for( int i = 0; i < N; i++) {
            // Cl[i] = malloc ((N) * sizeof(short));
            X[ i] = 0;
        }
        if( verbose == 1) {
        	LOGGER.debug("OCG : Calculating Initial class System...");
        }
        for( short i = 0; i < N; i++) {
            Cl[ NbClas][ 0] = i;
            short card = 1;
            int NbAdj = 0;
            for( short j = 0; j < N; j++) {
                if( A[ i][ j] == 1) {
                    X[ NbAdj] = j;
                    D[ NbAdj] = 1;
                    NbAdj++;
                }
            }
            // Computation of degrees relative to X
            for( short k = 1; k < NbAdj; k++) {
                for( short j = 0; j < k; j++) {
                    if( A[ X[ k]][ X[ j]] == 1) {
                        D[ k]++;
                        D[ j]++;
                    }
                }
            }

            int kk = 0;
            for( int Adj = 0; Adj < NbAdj; Adj++) {
                DgMax = 0;
                for( int k = 0; k < NbAdj; k++) {
                    if( D[ k] >= DgMax) {
                        DgMax = D[ k];
                        kk = k;
                    }
                }
                // Not enough adjacent vertices
                if( DgMax < card) {
                    break;
                }

                short jj = X[ kk];
                D[ kk] = 0;
                kk = 0;
                for( int k = 0; k < card; k++) {
                    int j = Cl[ NbClas][ k];
                    if( A[ jj][ j] == 1) {
                        kk++;
                    }
                }
                if( kk - card >= 0) {
                    Cl[ NbClas][ card] = jj;
                    card++;
                }
            }

            Kard[ NbClas] = card;

            // Is it a new class ?
            int flag = 0;
            for( int k = 0; k < NbClas; k++) {
                if( Kard[ k] < card) {
                    continue;
                }
                for( int j = 0; j < N; j++) {
                    X[ j] = 0;
                }
                for( int j = 0; j < Kard[ k]; j++) {
                    X[ Cl[ k][ j]] = 1;
                }
                flag = 1;
                for( int j = 0; j < Kard[ NbClas]; j++) {
                    if( X[ Cl[ NbClas][ j]] == 0) {
                        flag = 0;
                    }
                }
                // test if the new class exists
                if( flag == 1) {
                    break;
                }
            }
            if( flag == 0) {
                NbClas++;
            }
        }
        if( verbose == 1) {
        	LOGGER.debug("Calculating initial class system : Done");
        }
        if( verbose == 1) {
        	LOGGER.debug("Nb. of classes = " + NbClas);
        }
        int mis = Fermeture( N);
        if( verbose == 1) {
        	LOGGER.debug("Nb. of edges not within the classes :" + mis);
        }

        return NbClas;
    }

    /**
     * Updates the upper right part of Table A When i > j, A[i][j]=1 iff vertices i and j are joined in at least one
     * class When i < j, A[i][j]=1 iff there is an edge between i and j
     * 
     * @param NbClas
     * @return
     */
    private int Fermeture( int NbClas) {

        int jj, kk, mis = 0;

        for( int i = 1; i < N; i++) {
            for( int j = 0; j < i; j++) {
                A[ j][ i] = 0;
            }
        }
        for( int i = 0; i < NbClas; i++) {
            for( int j = 1; j < Kard[ i]; j++) {
                jj = Cl[ i][ j];
                for( int k = 0; k < j; k++) {
                    kk = Cl[ i][ k];
                    if( kk > jj) {
                        A[ jj][ kk] = 1;
                    }
                    else if( kk < jj) {
                        A[ kk][ jj] = 1;
                    }
                }
            }
        }

        // Count the edges such the two ends are not joined in any class
        for( int i = 1; i < N; i++) {
            for( int j = 0; j < i; j++) {
                if( A[ i][ j] == 1 && A[ j][ i] == 0) {
                    mis++;
                }
            }
        }

        // System.out.println( "OCG.Fermeture() :");
        // checkMatrixA();

        return mis;
    }

    /**
     * Computing the integer modularity value
     * 
     */
    private long Modularity() {

        long Sum = 0;

        for( int i = 1; i < N; i++) {
            for( int j = 0; j < i; j++) {
                Sum += A[ j][ i] * B[ i][ j];
            }
        }

        return Sum;
    }

    private void Effacer() {

        int ii, jj;
        long Cont;

        // Calculate the contribution of each element within its class
        for( int k = 0; k < NbClas; k++) {

            for( int i = 0; i < N; i++) {
                Clas[ i] = 0;
            }
            for( int i = 0; i < Kard[ k]; i++) {
                ii = Cl[ k][ i];
                Cont = 0;
                for( int j = 0; j < Kard[ k]; j++) {
                    jj = Cl[ k][ j];
                    Cont += B[ ii][ jj];
                }
                if( Cont >= 0) {
                    Clas[ ii] = 1;
                }
                else {
                    // NbElEl++;
                }
            }
            short card = 0;
            for( short i = 0; i < N; i++) {
                if( Clas[ i] > 0) { // Note: test was if( Clas[i] ){
                    Cl[ k][ card] = i;
                    card++;
                }
            }
            Kard[ k] = card;

        }
        return;
    }

    private void ClasOut( int NbCl, long Mod) {

        int i, j, k, kk, ko;

        System.out.println( "#################################################\n");
        // printf("# %s results on graph file %s\n", argv[0],FichE);
        System.out.println( "# Graph file has " + N + " Vertices and " + Na + " edges");
        System.out.println( "# Degre maximum: " + DgMax);
        System.out.println( "# Rate of edges: " + (1. * Na / N / (N - 1)));
        System.out.println( "# Rate of edges(double) : " + (1. * Na / (double) N / (double) (N - 1)));
        System.out.println( "# Rate of edges(float) : " + (1. * Na / (float) N / (float) (N - 1)));

        ko = 0;
        for( i = 1; i < N; i++) {
            for( j = 0; j < i; j++) {
                if( A[ i][ j] > 0 && A[ j][ i] > 0) {
                    ko++;
                }
            }
        }
        System.out.println( "# Rate of intraclass edges: " + 1. * ko / Na + " (" + Na + "," + ko + ")");
        System.out.println( "# Rate of intraclass edges (double): " + 1. * ko / (double) Na + " (" + Na + "," + ko + ")");
        System.out.println( "# Rate of intraclass edges (float): " + 1. * ko / (float) Na + " (" + Na + "," + ko + ")");

        if (typ == 1)
            System.out.println( "# Initial classes are: maximal cliques\n");
        else if (typ == 2)
            System.out.println( "# Initial classes are: edges\n");
        else if (typ == 3)
            System.out.println( "# Initial classes are: centered cliques\n");

        System.out.println( "# Fusion is according to: ");
        if( FuStyl == 0) {
            System.out.println( "the average gain\n");
        }
        else {
            System.out.println( "the total gain\n");
        }
        if( FCS == 1) {
            System.out.println( "# Class System is: maximizing the modularity\n");
        }
        else {
            System.out.println( "# Class System is: Final Class System\n\n");
            System.out.println( "# Maximum Class Cardinality: " + CardMax);
            System.out.println( "# Minimum Number of Classes" + ClCh);
        }
        System.out.println( "#################################################\n");

        for( i = 0; i < N; i++)
            Clas[ i] = 0;
        for( k = 0; k < NbCl; k++) {
            for( i = 0; i < Kard[ k]; i++) {
                Clas[ Cl[ k][ i]]++;
            }
        }
        k = 0;
        kk = 0;
        for( i = 0; i < N; i++)
            if( Clas[ i] == 0)
                kk++;
            else if( Clas[ i] > 1)
                k++;
        if( kk > 0) {
            System.out.println( "\nUnclustered nodes : " + kk);
            for( i = 0; i < N; i++)
                if( Clas[ i] == 0)
                    System.out.println( "  " + nodeName[ i]);
        }
        else {
            System.out.println( "Unclustered nodes " + kk + " : None");
        }
        System.out.println( "Multiclustered nodes :" + k);
        for( i = 0; i < N; i++) {
            if( Clas[ i] > 1) {
                System.out.println( nodeName[ i] + "(" + Clas[ i] + ")");
            }
        }

        /* Now print the classes */
        System.out.println( "\n\nFinal classes (" + NbCl + "), Modularity = " + Mod);
        for( k = 0; k < NbCl; k++) {
            System.out.println( ">Class " + k + "( " + Kard[ k] + "nodes):");
            for( i = 0; i < Kard[ k]; i++) {
                System.out.print( nodeName[ Cl[ k][ i]] + "  ");
            }
            System.out.println( "");
        }
        return;
    }

    /**
     * Reverse the keys and values in the given map
     * 
     * @param map The map to reverse
     * @return a reversed map
     */
    private HashMap<Integer, Long> reverseMap( HashMap<Long, Integer> map) {

        HashMap<Integer, Long> result = new HashMap<Integer, Long>();

        for( Map.Entry<Long, Integer> entry : map.entrySet()) {
            result.put( entry.getValue(), entry.getKey());
        }

        return result;
    }

    /* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}
}