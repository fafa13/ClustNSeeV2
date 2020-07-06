package org.cytoscape.clustnsee3.internal.algorithm;

/*************************************************************************/
/*                                                                       */
/* Copyright (C) 2011 Alain Guenoche */
/* guenoche@iml.univ-mrs.fr */
/*                                                                       */
/* with contributions from: */
/* Benoit Robisson and Charles E. Chapple */
/*                                                                       */
/*                                                                       */
/*                                                                       */
/* This program is free software: you can redistribute it and/or modify */
/* it under the terms of the GNU General Public License as published by */
/* the Free Software Foundation, either version 3 of the License, or */
/* (at your option) any later version. */
/*                                       */
/* This program is distributed in the hope that it will be useful, */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the */
/* GNU General Public License for more details. */
/*                                       */
/* You should have received a copy of the GNU General Public License */
/* along with this program. If not, see <http://www.gnu.org/licenses/>. */
/*                                                                       */
/*************************************************************************/

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CnSFTAlgorithm extends CnSAlgorithm {

    public static final String NAME = "FT";
    protected static CnSAlgorithm instance = null;

    int                        N;
    int                        Na;

    int                        CardMax;    // cardinal maximum d'une classe

    float[][]                  A;          // Graphe pondéré

    float[][]                  B;          // Matrice des modularités
    float[][]                  Var;        // Variation en cas de fusion
    int[][]                    Cl;         // Classes courantes

    int[]                      Clas;       // Classe de chaque element
    float[]                    Sum;        // Somme des poids des aretes en chaque sommet
    int[]                      Kard;       // Nb. d'elements dans les classes courantes
    int[]                      Part;       // Partition courante et finale

    String[]                   Et;         // Etiquette des sommets
    int                        NbClas;     // Nombre de classes

    private CnSFTAlgorithm() {
        super();
        parameters = new CnSAlgorithmParameters();
    }
    
    public static CnSAlgorithm getInstance() {
		if (instance == null) instance = new CnSFTAlgorithm();
		return instance;
	}
    
    @Override
    public CnSAlgorithmResult execute(CyNetwork network) {
    	CnSAlgorithmResult complexes = modClust(network);

        return complexes;
    }

    // ######################## ModClust ################################################

    /**
     * ModClust Algorithm: Find all complexes given a graph.
     * 
     * @param inputNetwork The scored network to find clusters in.
     * @param analysis_id Title of the result
     * @return An array containing an Cluster object for each nodeCluster.
     */
    public CnSAlgorithmResult modClust(CyNetwork inputNetwork) {
    	/*
         * calcule une partition qui optimise un critere de modularité
         */
        String callerID = "Algorithm.FT";
        System.err.println( "In " + callerID);
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

        System.err.println("Nb. de sommets " + N + ", d'aretes " + Na);
        System.err.println("Taux d'aretes du graphe " + 2. * Na / N / (N - 1));
        
        CardMax = N;				// cardinal maximum d'une classe

        A = new float[N][N];		// Graphe pondéré

        B = new float[N][N];		// Matrice des modularités
        Var = new float[N][N + 1];	// Variation en cas de fusion
        Cl = new int[N][N];		// Classes courantes

        Clas = new int[N];		// Classe de chaque element
        Sum = new float[N];			// Somme des poids des aretes en chaque sommet
        Kard = new int[N + 1];	// Nb. d'elements dans les classes courantes
        Part = new int[N];		// Partition courante et finale

        Et = new String[N];

        /*
         * convert cytoscape indices to ModClust indices update node labels (nodeName)
         */
        HashMap<Long, Integer> cyto2ModClust = new HashMap<Long, Integer>(N);

        Iterator<CyNode> ni;
        if (scope.equals("Selection")) {
            ni = selectedNodes.iterator();
        }
        else {
            ni = inputNetwork.getNodeList().iterator();
        }
        ev = new CnSEvent(CnSAlgorithmEngine.IS_CANCELLED, CnSEventManager.ALGORITHM_ENGINE);
        boolean cancelled = (Boolean)CnSEventManager.handleMessage(ev);
        
        HashMap<Integer, Long> algo_to_cyto = new HashMap<Integer, Long>();
		for (int i = 0; ni.hasNext() && !cancelled; i++) {
        	CyNode n = (CyNode)ni.next();
            long cyto_index = n.getSUID();
            cyto2ModClust.put(cyto_index, i);
            algo_to_cyto.put(i, cyto_index);
            Et[i] = CyIdentifiable.SUID;
        }
        
        Iterator<CyEdge> edges;
        if (scope.equals("Selection")) {
            edges = selectedEdges.iterator();
        }
        else {
            edges = inputNetwork.getEdgeList().iterator();
        }
        while (edges.hasNext() && !cancelled) {
            CyEdge e = edges.next();
            Integer vi = cyto2ModClust.get(e.getSource().getSUID());
            Integer vj = cyto2ModClust.get(e.getTarget().getSUID());
            if (vi != null && vj != null) {
            	A[vi.shortValue()][vj.shortValue()] = 1.0f;
            	A[vj.shortValue()][vi.shortValue()] = 1.0f;
            }
        }
        
        Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        NbClas = Newman( 1);
        ClasOut();
        int[][] classes = new int[N][N];
        int[] card = new int[N];
        for (int i = 0; i < N; i++) {
        	card[i] = 0;
        	for (int j = 0; j < N; j++)
        		classes[i][j] = 0;
        }
        for (int i = 0; i < N; i++) {
        	classes[Part[i] - 1][card[Part[i] - 1]++] = i;
        }
        String l = "";
        LOGGER.info("*** Nb class : " + NbClas);
        for (int i = 0; i < N; i++) if (card[i] > 0) l += i + " ";
        LOGGER.info("*** " + l);
        for (int i = 0; i < NbClas; i++) {
        	LOGGER.info("*** Cluster #" + i + " : " + card[i] + " nodes");
        	l = "*** ";
        	for (int j = 0; j < card[i]; j++) l += algo_to_cyto.get(classes[i][j]) + " ";
        	LOGGER.info(l);
        	LOGGER.info("***");
        	LOGGER.info("***");
        }
        return new CnSAlgorithmResult(classes, card, NbClas, algo_to_cyto);
    }
    
    int Newman( int typ) {

        // Fusionne les classes initiales tant que la modularité croit
        int i, ii, j, jj, k, k1, k2, cl1, cl2 = 0, fus1, fus2, card;
        short i1, ks;
        float SumMax, ModTot, ModMax, VarMod, var, VarMax, Mod = 0.f;

        // On Evalue les sommes des poids des aretes en chaque sommet
        SumMax = 0.f;
        for( i = 0; i < N; i++) {
            Sum[ i] = 0.f;
            for( j = 0; j < N; j++)
                if( A[ i][ j] > 0)
                    Sum[ i] = Sum[ i] + A[ i][ j];
            SumMax = SumMax + Sum[ i];
        }
        // if (N<20) printf("Somme des ponderations %.2f\n\n",SumMax);

        // Matrice modularityMatrix
        ModTot = 0.f;
        ModMax = 0.f;
        for( i = 1; i < N; i++)
            for( j = 0; j < i; j++) {
                B[ i][ j] = SumMax * A[ i][ j] - Sum[ i] * Sum[ j];
                B[ j][ i] = B[ i][ j];
                ModTot = ModTot + B[ i][ j];
                if( A[ i][ j] > 0.)
                    ModMax = ModMax + B[ i][ j];
                A[ j][ i] = 0.f; // La partie supérieure droite marquera à 1 les paires d'éléments réunis dans au moins
                                 // une classe
            }
        if( N < 20)
            EditB();
        // printf("Modularite maximum : %.2f, modularite globale : %.2f\n",ModMax,ModTot);

        // Initialisation des classes par les singletons
        for( i1 = 0; i1 < N; i1++) {
            Cl[ i1][ 0] = i1;
            Kard[ i1] = 1;
        }

        /*
         * Calcul des variations induites par la fusion de toute paire de classes fusionVariation[i][j] est la variation
         * de modularité engendrée par la fusion des classes quand i > j et 1 si elles sont fusionnables (0 sinon) quand
         * i < j (partie supérieure droite) Deux classes sont fusionnables si elles sont connexes et que union ne
         * dépasse pas clusterMaxCard
         */

        // Initialisation de fusionVariation
        for( cl1 = 1; cl1 < N; cl1++) {
            for( cl2 = 0; cl2 < cl1; cl2++)
            // Peut-on les fusionner ?
            {
                VarMod = 0.f;
                fus1 = 0;
                for( i = 0; i < N; i++)
                    Clas[ i] = 0;
                for( k1 = 0; k1 < Kard[ cl1]; k1++) {
                    i = Cl[ cl1][ k1];
                    Clas[ i] = 1;
                    for( k2 = 0; k2 < Kard[ cl2]; k2++) {
                        j = Cl[ cl2][ k2];
                        Clas[ j] = 1;
                        if( i == j)
                            fus1 = 1; // Classes connexes
                        else if( i < j && A[ j][ i] > 0.)
                            fus1 = 1; // Classes connexes
                        else if( i > j && A[ i][ j] > 0.)
                            fus1 = 1; // Classes connexes
                        if( i < j)
                            VarMod = VarMod + (1.f - A[ i][ j]) * B[ i][ j];
                        else if( i > j)
                            VarMod = VarMod + (1.f - A[ j][ i]) * B[ i][ j];
                    }
                    card = 0; // cardinal de l'union
                    for( k = 0; k < N; k++)
                        if( Clas[ k] != 0)
                            card++; // MODIF BOOL => INT
                    if( card > CardMax)
                        fus1 = 0;
                } // printf("Classes %d et %d ; VarMod %.2f\n",cl2,cl1,VarMod);
                Var[ cl1][ cl2] = VarMod;
                Var[ cl2][ cl1] = 1.f * fus1;
            }
        }
        if( N < 20)
            VueVar();

        // printf("\nFusions\n\n");
        NbClas = (short) N;
        while( NbClas > 2) {
            VarMax = -1.f;
            for( ii = 1; ii < N; ii++) {
                if( Kard[ ii] == 0)
                    continue; // ancienne classe
                for( jj = 0; jj < ii; jj++) {
                    if( Kard[ jj] == 0)
                        continue;
                    // Peut-on les fusionner ?
                    if( Var[ jj][ ii] < 1.)
                        continue; // non
                    card = Kard[ ii] * Kard[ jj];
                    var = 1.f * Var[ ii][ jj] / card; // variation moyenne rapportée nb. d'aretes interclasses
                    if( var > VarMax) {
                        VarMax = var;
                        cl1 = jj;
                        cl2 = ii;
                    }
                }
            }
            if( VarMax < 0.) {
                break; /* System.err.println("Aucune paire fusionnable"); */
            }
            // printf("Fusion des classes %d et %d : variation moyenne %.2f\n",cl1+1,cl2+1,VarMax);

            // printf("Mise a jour de weightedNetwork\n");
            for( k1 = 0; k1 < Kard[ cl1]; k1++) {
                i = Cl[ cl1][ k1];
                for( k2 = 0; k2 < Kard[ cl2]; k2++) {
                    j = Cl[ cl2][ k2];
                    // Les paires par fermeture transitive sont marquées dans la partie supérieure droite de
                    // weightedNetwork
                    if( i < j)
                        A[ i][ j] = 1.f;
                    else if( i > j)
                        A[ j][ i] = 1.f;
                }
            }

            // printf("Mise a jour de clusters\n");
            for( k = 0; k < N; k++)
                Clas[ k] = 0;
            for( k1 = 0; k1 < Kard[ cl1]; k1++)
                Clas[ Cl[ cl1][ k1]] = 1;
            for( k2 = 0; k2 < Kard[ cl2]; k2++)
                Clas[ Cl[ cl2][ k2]] = 1;
            card = 0;
            for( ks = 0; ks < N; ks++)
                if( Clas[ ks] != 0) {
                    Cl[ cl1][ card] = ks;
                    card++;
                } // MODIF INT => BOOL
            Kard[ cl1] = (short) card;
            Kard[ cl2] = 0;
            // printf("New Classe %d : Nb. d'elements %d, Modularity %.1f\n",cl1+1,clusterCard[cl1],Mod);

            // Mise à jour de fusionVariation
            for( ii = 0; ii < N; ii++) {
                if( Kard[ ii] == 0 || cl1 == ii || cl2 == ii)
                    continue;
                fus1 = 0;
                fus2 = 0;
                if( ii < cl1)
                    fus1 = (int) Var[ ii][ cl1];
                else
                    fus1 = (int) Var[ cl1][ ii]; // MODIF CAST float => int
                if( ii < cl2)
                    fus2 = (int) Var[ ii][ cl2];
                else
                    fus2 = (int) Var[ cl2][ ii]; // MODIF CAST float => int
                // printf("%d %d %d | ",ii+1,fus1,fus2);
                if( fus1 == 0 && fus2 == 0) // fusionnable avec ni l'une ni l'autre donc avec l'union
                {
                    if( ii < cl1)
                        Var[ ii][ cl1] = 0;
                    else
                        Var[ cl1][ ii] = 0;
                    continue;
                }
                // reste à vérifier la condition de cardinalité et à calculer fusionVariation[ii][cl1]
                VarMod = 0.f;
                for( k = 0; k < N; k++)
                    Clas[ k] = 0;
                for( k = 0; k < Kard[ ii]; k++) {
                    i = Cl[ ii][ k];
                    Clas[ i] = 1;
                    for( k1 = 0; k1 < Kard[ cl1]; k1++) {
                        j = Cl[ cl1][ k1];
                        Clas[ j] = 1;
                        if( i < j)
                            VarMod = VarMod + (1.f - A[ i][ j]) * B[ i][ j];
                        else if( i > j)
                            VarMod = VarMod + (1.f - A[ j][ i]) * B[ i][ j];
                    }
                }
                card = 0; // cardinal de la fusion éventuelle
                for( k = 0; k < N; k++)
                    if( Clas[ k] != 0)
                        card++;
                if( card > CardMax)
                    fus1 = 0;
                else
                    fus1 = 1;

                if( ii > cl1) {
                    Var[ ii][ cl1] = VarMod;
                    Var[ cl1][ ii] = fus1;
                }
                else {
                    Var[ cl1][ ii] = VarMod;
                    Var[ ii][ cl1] = fus1;
                }
            }
            NbClas--; // printf("\n");
            // VueVar(); fflush(stdin); getchar();
        } // printf("\n");
        if( typ == 1) {
            Mod = Modularity();
            System.err.println( "Nb. de classes " + NbClas + ", Modularity = " + Mod + "\n");
        }

        NbClas = 0;
        for( i = 0; i < N; i++) {
            if( Kard[ i] == 0)
                continue;
            NbClas++;
            for( ii = 0; ii < Kard[ i]; ii++) {
                j = Cl[ i][ ii];
                // printf("%*s ",-NbCar,nodeName[j]);
                Part[ j] = NbClas;
            }
            // printf("| ");
        } // printf("\n");
        Transfert( typ);
        return NbClas;
    }

    void EditB() {

        int i, j;

        System.err.println( "Matrice modularityMatrix\n");
        for( i = 0; i < N; i++) {
            System.err.print( " " + Et[ i]);
            for( j = 0; j < N; j++)
                System.err.print( " " + B[ i][ j]);
            System.err.println();
        }
        System.err.println();
        return;
    }

    void VueVar() {

        int i, j;
        for( i = 0; i < N; i++) {
            if( Kard[ i] == 0)
                continue;
            System.err.print( " " + (i + 1));
            for( j = 0; j < N; j++) {
                if( Kard[ j] == 0)
                    continue;
                System.err.print( " " + Var[ i][ j]);
            }
            System.err.println();
        }
        System.err.println();
        return;
    }

    float Modularity()
    /* Evaluation de la modularite */
    {

        int i, j;
        float Sum = 0.f;

        for( i = 1; i < N; i++)
            for( j = 0; j < i; j++)
                Sum += A[ j][ i] * B[ i][ j];
        return Sum;
    }

    void Transfert( int typ) {

        int i, ii, j, flag = 1, NbTrans = 0;
        int k, kk;
        float gain, gainmax, sumax;

        // Calcul de la contribution de chaque element a chaque classe
        for( i = 0; i < N; i++) {
            for( k = 1; k < N; k++)
                Var[ i][ k] = 0.f;
            for( j = 0; j < N; j++)
                Var[ i][ Part[ j]] = Var[ i][ Part[ j]] + B[ i][ j];
            sumax = -100.f * N;
            for( k = 1; k <= NbClas; k++)
                if( Var[ i][ k] > sumax)
                    sumax = Var[ i][ k];
            if( sumax > 0.)
                Var[ i][ 0] = sumax;
            else
                Var[ i][ 0] = 0.f; // score de la meilleure classe ou singleton
            // printf("%2d : %4d : ",i+1,fusionVariation[i][0]);
            // for (k=1; k<=clusterNumber; k++) printf("%4d ",fusionVariation[i][k]); printf("\n");
        }
        if( typ == 2)
            for( i = 0; i < N; i++) {
                System.err.print( " " + Et[ i] + " (" + Part[ i] + ") : " + Var[ i][ 0]);
                for( k = 1; k <= NbClas; k++)
                    System.err.print( Var[ i][ k] + " ");
                System.err.println();
            }

        while( flag > 0) {
            gainmax = 0.f;
            ii = -1;
            for( i = 0; i < N; i++) {
                gain = Var[ i][ 0] - Var[ i][ Part[ i]];
                if( gain > gainmax) {
                    gainmax = gain;
                    ii = i;
                }
            }
            if( ii == -1) {
                flag = 0;
                continue;
            }
            NbTrans++;
            // C'est ii qu'on deplace ; mise a jour de fusionVariation
            kk = Part[ ii];
            for( j = 0; j < N; j++)
                Var[ j][ kk] = Var[ j][ kk] - B[ j][ ii];
            sumax = -1.f;
            kk = 0; // cherche la meilleure classe
            for( k = 1; k <= NbClas; k++)
                if( Var[ ii][ k] > sumax) {
                    sumax = Var[ ii][ k];
                    kk = k;
                }
            if( kk == 0) {
                NbClas++;
                kk = NbClas;
            }
            Part[ ii] = kk;
            if( typ == 2)
                System.err.println( Et[ ii] + " -> classe " + kk + " : gain " + gainmax);
            for( j = 0; j < N; j++) {
                Var[ j][ kk] = Var[ j][ kk] + B[ j][ ii];
                sumax = -100.f * N;
                for( k = 1; k <= NbClas; k++)
                    if( Var[ j][ k] > sumax)
                        sumax = Var[ j][ k];
                if( sumax > 0.)
                    Var[ j][ 0] = sumax;
                else
                    Var[ j][ 0] = 0.f;
            }
        }
        if( typ == 1)
            System.err.println( "Nb. de transferts : " + NbTrans + "\n");
        return;
        /*
         * for (i=0; i<N; i++) { System.err.print(" "+nodeName[i]+" : "+fusionVariation[i][0]+" : "); for (k=1;
         * k<=clusterNumber; k++) System.err.print(fusionVariation[i][k]+" "); System.err.println(); } //getchar();
         */
    }

    void ClasOut()
    /*
     * Edite les classes à partir d'un vecteur de numeros de classe partition[] Calcule la modularité et le taux
     * d'arêtes intraclasses
     */
    {

        int i, j1, j2, k1, k2, /*Aint,*/ card;
        short k;
        short ii;
        float Mod;

        for( k = 1; k <= NbClas; k++)
            Kard[ k] = 0;
        for( i = 0; i < N; i++)
            Kard[ Part[ i]]++;
        Mod = 0.f;
        
        for( k = 1; k <= NbClas; k++) {
        	//s += "Classe " + k + " Nb. d'elements " + Kard[ k] + "\n";
            card = 0;
            for( ii = 0; ii < N; ii++)
                if( Part[ ii] == k) {
                    Clas[ card] = ii;
                    card++;
                }
            if( card < 2)
                continue;
            for( k1 = 1; k1 < card; k1++) {
                j1 = Clas[ k1];
                for( k2 = 0; k2 < k1; k2++) {
                    j2 = Clas[ k2];
                    Mod = Mod + B[ j1][ j2];
                    //if( A[ j1][ j2] > 0) Aint++;
                }
            }
        }
        return;
    }

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithm#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}
}