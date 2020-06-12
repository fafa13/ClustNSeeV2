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
import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

/**
 * 
 */
public class CnSTFitAlgorithm extends CnSAlgorithm {
	public static final String NAME = "TFit";
	public static final int ALPHA = 1001;
	protected static CnSAlgorithm instance = null;
	
    int                        N;
    int                        Na;

    int                        clusterMaxCard;  // cardinal maximum d'une classe

    CnSAlgorithmParameter      alpha;           // limit parameter
    float[][]                  weightedNetwork; // Graphe pondéré

    float[][]                  modularityMatrix; // Matrice des modularités
    float[][]                  fusionVariation; // Variation en cas de fusion
    int[][]                    clusters;        // Classes courantes

    int[]                      nodeCluster;     // Classe de chaque element
    float[]                    edgesWeight;     // Somme des poids des aretes en chaque sommet
    int[]                      clusterCard;     // Nb. d'elements dans les classes courantes
    int[]                      partition;       // Partition courante et finale

    String[]                   nodeName;        // Etiquette des sommets
    int                        clusterNumber;   // Nombre de classes
    HashMap<Long, Integer> cyto2ModClust;

	private CnSTFitAlgorithm() {
		super();
		parameters = new CnSAlgorithmParameters();
		parameters.addParameter("Alpha", CnSTFitAlgorithm.ALPHA, Float.valueOf("1.0"));
	}
	
	public static CnSAlgorithm getInstance() {
		if (instance == null) instance = new CnSTFitAlgorithm();
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm#execute(org.cytoscape.model.CyNetwork, org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmParameters)
	 */
	@Override
	public CnSAlgorithmResult execute(CyNetwork network) {
		alpha = getParameters().getParameter(ALPHA);

		CnSAlgorithmResult complexes = runTFit(network);

        return complexes;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}
	public void setPanel(CnSPanel panel) {
		parameters.setPanel(panel);
	}
	
	   // ######################## TFit ################################################

    /**
     * TFit Algorithm: Find all complexes given a graph.
     * 
     * @param inputNetwork The scored network to find clusters in.
     * @param analysis_id Title of the result
     * @return An array containing an Cluster object for each nodeCluster.
     */
    private CnSAlgorithmResult runTFit( CyNetwork inputNetwork) {
    	
        /*
         * calcule une partition qui optimise un critere de modularité
         */
        String callerID = "Algorithm.TFit";
        System.err.println( "In " + callerID);
        //currentNetwork = inputNetwork;
        CnSEvent ev = new CnSEvent(CnSAlgorithmEngine.GET_SCOPE, CnSEventManager.ALGORITHM_ENGINE);
        String scope = (String)CnSEventManager.handleMessage(ev);
        System.err.println( "Clustering Scope : " + scope);
        List<CyNode> selectedNodes = CyTableUtil.getNodesInState(inputNetwork, "selected", true);
        List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(inputNetwork, "selected", true);
        
        if (scope.equals("Selection")) {
            N = selectedNodes.size();
            Na = selectedEdges.size();
        }
        else {
            N = inputNetwork.getNodeCount();
            Na = inputNetwork.getEdgeCount();
        }

        System.err.println( "Nb. de sommets " + N + ", d'aretes " + Na);
        System.err.println( "Taux d'aretes du graphe " + 2. * Na / N / (N - 1));
        
        clusterMaxCard = N; // cardinal maximum d'une classe

        weightedNetwork = new float[N][N]; // Graphe pondéré

        modularityMatrix = new float[N][N]; // Matrice des modularités
        fusionVariation = new float[N][N + 1]; // Variation en cas de fusion
        clusters = new int[N][N]; // Classes courantes

        nodeCluster = new int[N]; // Classe de chaque element
        edgesWeight = new float[N]; // Somme des poids des aretes en chaque sommet
        clusterCard = new int[N]; // Nb. d'elements dans les classes courantes
        partition = new int[N]; // Partition courante et finale

        nodeName = new String[N];

        /*
         * convert cytoscape indices to ModClust indices update node labels (nodeName)
         */
        cyto2ModClust = new HashMap<Long, Integer>( N);

       Iterator<CyNode> ni;
       if (scope.equals("Selection")) {
           ni = selectedNodes.iterator();
       }
       else {
           ni = inputNetwork.getNodeList().iterator();
       }
       ev = new CnSEvent(CnSAlgorithmEngine.IS_CANCELLED, CnSEventManager.ALGORITHM_ENGINE);
       boolean cancelled = (Boolean)CnSEventManager.handleMessage(ev);
       
        // Retrieve the list of node names
        HashMap<String, CyNode> name_to_node = new HashMap<String, CyNode>();
        for( int i = 0; ni.hasNext() && !cancelled; i++) {
            CyNode node = ni.next();
            nodeName[ i] = inputNetwork.getRow(node).get(CyNetwork.NAME, String.class);
            name_to_node.put( nodeName[ i], node);
        }

        // Order the nodes by aphabetical order and create a table to
        // convert the node index in Cytoscape into an index between 0 and N
        List<String> node_names = Arrays.asList( nodeName);
        Collections.sort( node_names);
        HashMap<Integer, Long> modClust_to_cyto = new HashMap<Integer, Long>();
		for( int i_node_name = 0; i_node_name < node_names.size(); i_node_name++) {
            String node_name = node_names.get( i_node_name);
            nodeName[ i_node_name] = node_name;
            long cyto_index = name_to_node.get( node_name).getSUID();
            cyto2ModClust.put( cyto_index, i_node_name);
            modClust_to_cyto.put(i_node_name, cyto_index);
        }

        Iterator<CyEdge> edges;
        if (scope.equals("Selection")) {
            edges = selectedEdges.iterator();
        }
        else {
            edges = inputNetwork.getEdgeList().iterator();
        }
        while( edges.hasNext() && !cancelled) {
            CyEdge e = edges.next();
            Integer i = cyto2ModClust.get(e.getSource().getSUID());
            Integer j = cyto2ModClust.get(e.getTarget().getSUID());
            if (i != null && j != null) {
            	weightedNetwork[ i.intValue()][ j.intValue()] = 1.0f;
            	weightedNetwork[ j.intValue()][ i.intValue()] = 1.0f;
            }
        }

        clusterNumber = tfitAlgo( 1);
        ClasOut();
        if (scope.equals("Selection"))
          	makeClusters(CyTableUtil.getNodesInState(inputNetwork, "selected", true));
        else
           	makeClusters(inputNetwork.getNodeList());
        
        return new CnSAlgorithmResult(clusters, clusterCard, clusterNumber, modClust_to_cyto);
    }

    private int Louv1() {

        // Calcul de la contribution de chaque element a chaque classe
        for( int i = 0; i < N; i++) {
            for( int k = 1; k < N; k++) {
                fusionVariation[ i][ k] = 0.f;
            }
            for( int j = 0; j < N; j++) {
                fusionVariation[ i][ partition[ j]] = fusionVariation[ i][ partition[ j]] + modularityMatrix[ i][ j];
            }
        }

        int flag = 1;
        int fflag = 0;
        int NbTrans = 0;
        // fflag=1 si au moins 1 transfert dans cet appel de la procedure
        while( flag > 0) {
            flag = 0; // mis a 1 si transfert dans cette boucle
            for( int i = 0; i < N; i++) {
                int OldC = partition[ i];
                float VarMax = fusionVariation[ i][ OldC];
                int NewC = 0;
                for( int k = 1; k <= clusterNumber; k++) {
                    if( fusionVariation[ i][ k] > VarMax) {
                        VarMax = fusionVariation[ i][ k];
                        NewC = k;
                    }
                }
                if( NewC > 0 || VarMax < 0.) {
                    flag = 1;
                    fflag = 1;
                    NbTrans++;
                    for( int j = 0; j < N; j++) {
                        fusionVariation[ j][ OldC] = fusionVariation[ j][ OldC] - modularityMatrix[ j][ i];
                    }
                    if( VarMax < 0.) {
                        clusterNumber++;
                        NewC = clusterNumber;
                        for( int j = 0; j < N; j++) {
                            fusionVariation[ j][ NewC] = 0.f;
                        }
                    }
                    partition[ i] = NewC;
                    for( int j = 0; j < N; j++) {
                        fusionVariation[ j][ NewC] = fusionVariation[ j][ NewC] + modularityMatrix[ j][ i];
                    }
                }
            }
        }
        System.err.println( "Nb. de transferts : " + NbTrans + "\n");

        return fflag;
    }

    private int Louv2() {

        int NewC = 0;
        int OldC = 0;
        float VarMax = 0;

        // poids des connections entre classes
        for( int k = 0; k <= clusterNumber; k++) {
            for( int kk = 0; kk <= clusterNumber; kk++) {
                weightedNetwork[ k][ kk] = 0.f;
            }
        }
        for( int i = 1; i < N; i++) {
            for( int j = 0; j < i; j++) {
                weightedNetwork[ partition[ i]][ partition[ j]] = weightedNetwork[ partition[ i]][ partition[ j]] + modularityMatrix[ i][ j];
                weightedNetwork[ partition[ j]][ partition[ i]] = weightedNetwork[ partition[ j]][ partition[ i]] + modularityMatrix[ i][ j];
            }
        }
        /*
         * for (k=1; k<=clusterNumber; k++) { printf("%2d : ",k); for (kk=1; kk<=clusterNumber; kk++)
         * printf("%7.0f ",weightedNetwork[k][kk]); printf("\n"); } printf("\n");
         */

        for( int k = 1; k <= clusterNumber; k++) {
            nodeCluster[ k] = k;
            weightedNetwork[ k][ k] = 0.f;
            for( int kk = 1; kk <= clusterNumber; kk++) {
                fusionVariation[ k][ kk] = weightedNetwork[ k][ kk];
            }
        }
        // on fusione des qu'il y a une connections > 0 entre classes ?
        int flag = 1;
        int fflag = 0;
        while( flag > 0) {
            flag = 0;
            for( int i = 1; i <= clusterNumber; i++) {
                OldC = nodeCluster[ i];
                VarMax = fusionVariation[ i][ OldC];
                NewC = 0;
                for( int k = 1; k <= clusterNumber; k++) {
                    if( fusionVariation[ i][ k] > VarMax) {
                        VarMax = fusionVariation[ i][ k];
                        NewC = k;
                    }
                }
                if( VarMax < 0.) {
                    clusterNumber++;
                    NewC = clusterNumber;
                    for( int k = 1; k < clusterNumber; k++) {
                        fusionVariation[ k][ clusterNumber] = 0.f;
                        fusionVariation[ clusterNumber][ k] = weightedNetwork[ k][ i];
                    }
                    fusionVariation[ clusterNumber][ OldC] = fusionVariation[ i][ OldC];
                }
                if( NewC > 0) {
                    flag = 1;
                    fflag = 1;
                    nodeCluster[ i] = NewC;
                    // printf("%d -> classe %d\n",i,NewC);
                    // on deplace i de OldC a NewC ; mise a jour de fusionVariation
                    for( int j = 1; j <= clusterNumber; j++) {
                        fusionVariation[ j][ OldC] = fusionVariation[ j][ OldC] - weightedNetwork[ j][ i];
                    }
                    for( int j = 1; j <= clusterNumber; j++) {
                        fusionVariation[ j][ NewC] = fusionVariation[ j][ NewC] + weightedNetwork[ j][ i];
                    }
                    // VueVar(); getchar();
                }
            }

        }
        return fflag;
    }

    private int Renum() {

        clusterNumber = N;
        // Rename classes to avoid empty ones
        for( int i = 0; i < clusterNumber; i++) {
            clusterCard[ i] = 0;
        }
        for( int i = 0; i < N; i++) {
            clusterCard[ partition[ i]] = 1 + clusterCard[ partition[ i]];
        }
        int nbcla = 0;
        int[] newClasNb = new int[clusterNumber];

        for( int i = 0; i < clusterNumber; i++) {
            if( clusterCard[ i] != 0) {
                newClasNb[ i] = nbcla;
                nbcla += 1;
            }
        }
        clusterNumber = nbcla;
        for( int i = 0; i < N; i++) {
            partition[ i] = newClasNb[ partition[ i]];
        }
        return clusterNumber;

    }

    private int tfitAlgo( int typ) {

        // Fusionne les classes initiales tant que la modularité croit
        int i, j;
        float SumMax, ModTot, ModMax;

        // On Evalue les sommes des poids des aretes en chaque sommet
        SumMax = 0.f;
        for( i = 0; i < N; i++) {
            edgesWeight[ i] = 0.f;
            for( j = 0; j < N; j++)
                if( weightedNetwork[ i][ j] > 0)
                    edgesWeight[ i] = edgesWeight[ i] + weightedNetwork[ i][ j];
            SumMax = SumMax + edgesWeight[ i];
        }
        // if (N<20) printf("Somme des ponderations %.2f\n\n",SumMax);

        // Matrice modularityMatrix
        ModTot = 0.f;
        ModMax = 0.f;
        for( i = 1; i < N; i++)
            for( j = 0; j < i; j++) {
                modularityMatrix[ i][ j] = (Float.valueOf(alpha.getValue().toString()) * SumMax * weightedNetwork[ i][ j]) - ((edgesWeight[ i] * edgesWeight[ j]) / Float.valueOf(alpha.getValue().toString()));
                modularityMatrix[ j][ i] = modularityMatrix[ i][ j];
                ModTot = ModTot + modularityMatrix[ i][ j];
                if( weightedNetwork[ i][ j] > 0.)
                    ModMax = ModMax + modularityMatrix[ i][ j];
                weightedNetwork[ j][ i] = 0.f; // La partie supérieure droite marquera à 1 les paires d'éléments réunis
                                               // dans au moins
                // une classe
            }
        if( N < 20)
            EditB();
        // printf("Modularite maximum : %.2f, modularite globale : %.2f\n",ModMax,ModTot);

        // Initialisation des classes par les singletons
        for( i = 0; i < N; i++) {
            clusters[ i][ 0] = i;
            clusterCard[ i] = 1;
            partition[ i] = i;
        }

        clusterNumber = N;
        int fflag1 = 1;
        int fflag2 = 1;

        while( fflag1 > 0 || fflag2 > 0) {
            fflag1 = Louv1();
            Renum();

            fflag2 = Louv2();
            for( i = 0; i < N; i++) {
                partition[ i] = nodeCluster[ partition[ i]];
            }
            clusterNumber = Renum();
        }

        for( i = 0; i < N; i++) {
            partition[ i] = partition[ i] + 1;
        }

        return clusterNumber;
    }

    private void ClasOut()
    /*
     * Edite les classes à partir d'un vecteur de numeros de classe partition[] Calcule la modularité et le taux
     * d'arêtes intraclasses
     */
    {

        int i, j1, j2, k1, k2/*, Aint*/, card;
        short k;
        short ii;
        float Mod;

        for( k = 1; k <= clusterNumber; k++)
            clusterCard[ k] = 0;
        for( i = 0; i < N; i++)
            clusterCard[ partition[ i]]++;
        Mod = 0.f;
        for( k = 1; k <= clusterNumber; k++) {
            card = 0;
            for( ii = 0; ii < N; ii++)
                if( partition[ ii] == k) {
                    nodeCluster[ card] = ii;
                    card++;
                    System.err.print( nodeName[ ii] + " ");
                }
            if( card < 2)
                continue;
            for( k1 = 1; k1 < card; k1++) {
                j1 = nodeCluster[ k1];
                for( k2 = 0; k2 < k1; k2++) {
                    j2 = nodeCluster[ k2];
                    Mod = Mod + modularityMatrix[ j1][ j2];
                    //if( weightedNetwork[ j1][ j2] > 0) Aint++;
                }
            }
        }
        return;
    }

    private void EditB() {

        int i, j;

        System.err.println( "Matrice modularityMatrix\n");
        for( i = 0; i < N; i++) {
            System.err.print( " " + nodeName[ i]);
            for( j = 0; j < N; j++)
                System.err.print( " " + modularityMatrix[ i][ j]);
            System.err.println();
        }
        System.err.println();
        return;
    }

    private void makeClusters(List<CyNode> nodeList) {
    	clusters = new int[clusterNumber][N];
    	int[] nb = new int[clusterNumber];
    	clusterCard = new int[clusterNumber];
    	Iterator<CyNode> nodes;
    	nodes = nodeList.iterator();
		while( nodes.hasNext()) {
			CyNode n = nodes.next();
			int part = partition[cyto2ModClust.get(n.getSUID())];
			clusters[part - 1][nb[part - 1]++] = cyto2ModClust.get(n.getSUID());
			clusterCard[part - 1]++;
		}
    }
}

