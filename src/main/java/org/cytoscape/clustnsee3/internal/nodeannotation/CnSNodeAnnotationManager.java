/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 déc. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSPredicate;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.nodeannotation.trie.CnSAnnotationTrie;
import org.cytoscape.clustnsee3.internal.nodeannotation.trie.CnSTrieNode;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 */
public class CnSNodeAnnotationManager implements CnSEventListener {
	private static CnSNodeAnnotationManager instance = null;

	public static final int LOAD_ANNOTATIONS = 1;
	public static final int ADD_ANNOTATION = 2;
	public static final int REMOVE_ANNOTATION = 3;
	public static final int SET_NODE_ANNOTATION = 4;
	public static final int REMOVE_NODE_ANNOTATION = 5;
	public static final int GET_NODES = 6;
	public static final int GET_ANNOTATIONS = 7;
	public static final int PRINT_ANNOTATIONS = 8;
	public static final int PARSE_ANNOTATIONS = 9;
	public static final int LOOK_FOR_ANNOTATIONS = 10;
	public static final int GET_CLUSTER_ANNOTATIONS = 11;
	public static final int CLEAR_CLUSTERS_ANNOTATION = 12;
	public static final int GET_ANNOTATION = 13;
	public static final int GET_ANNOTATED_NODES = 14;
	public static final int GET_ANNOTATED_CLUSTERS = 15;
	public static final int UNLOAD_ANNOTATIONS = 16;
	public static final int ANNOTATE_NETWORK = 17;
	public static final int DEANNOTATE_NETWORK = 18;
	public static final int GET_NETWORK_ANNOTATIONS = 19;
	public static final int IS_NETWORK_ANNOTATED = 20;
	public static final int GET_NETWORK_ANNOTATED_NODES = 21;
	public static final int GET_NETWORK_MAPPED_ANNOTATIONS = 22;
	public static final int GET_BH_HYPERGEOMETRIC = 23;
	public static final int GET_ENRICHED_CLUSTERS = 24;
	public static final int GET_TOP3_CLUSTERS = 25;
	public static final int REFRESH_CLUSTER_HASMAP = 26;
	public static final int REMOVE_ENRICHMENT = 27;
	public static final int SET_ALPHA = 28;
	public static final int GET_ALPHA = 29;
	public static final int COMPUTE_ENRICHMENT = 30;
	public static final int GET_BH_FILTERED_ANNOTATIONS = 31;
	
	public static final int VALUE = 1001;
	public static final int NODE = 1002;
	public static final int ANNOTATION = 1003;
	public static final int FILE = 1004;
	public static final int NETWORK = 1005;
	public static final int FROM_LINE = 1006;
	public static final int PREFIX = 1007;
	public static final int CLUSTER = 1008;
	public static final int ANNOTATION_FILE = 1009;
	public static final int HG_THRESHOLD = 1010;
	public static final int PARTITION = 1011;
	public static final int ALPHA = 1012;
	public static final int TASK = 1013;

	private TreeMap<CnSNodeAnnotation, CnSNodeNetworkSet> annotations;
	private HashMap<CnSNodeAnnotation, Vector<CnSCluster>> annotation2cluster;
	private HashMap<CyNode, Vector<CnSNodeAnnotation>> cyNodes;
	private HashMap<CnSCluster, Vector<CnSNodeAnnotation>> cluster2annotation;
	private Vector<CnSNodeAnnotationFile> files;
	private HashMap<CnSNodeAnnotationFile, Vector<CyNetwork>> annotatedNetworks;
	private HashMap<CnSCluster, Vector<CnSAnnotationClusterPValue>> cluster2pv;
	private HashMap<CnSNodeAnnotation, Vector<CnSAnnotationClusterPValue>> annotation2pv;
	private CnSAnnotationTrie annotationTrie;

	private  double currentAlpha = 0.05D;
			
	private CnSNodeAnnotationManager() {
		super();
		annotations = new TreeMap<CnSNodeAnnotation, CnSNodeNetworkSet>();
		cyNodes = new HashMap<CyNode, Vector<CnSNodeAnnotation>>();
		cluster2annotation = new HashMap<CnSCluster, Vector<CnSNodeAnnotation>>();
		annotation2cluster = new HashMap<CnSNodeAnnotation, Vector<CnSCluster>>();
		files = new Vector<CnSNodeAnnotationFile>();
		annotationTrie = new CnSAnnotationTrie();
		annotatedNetworks = new HashMap<CnSNodeAnnotationFile, Vector<CyNetwork>>();
		cluster2pv = new HashMap<CnSCluster, Vector<CnSAnnotationClusterPValue>>();
		annotation2pv = new HashMap<CnSNodeAnnotation, Vector<CnSAnnotationClusterPValue>>();
	}
	
	private int getClusterAnnotationNumber(CnSCluster cluster) {
		int n = 0;
		for (CnSNode node : cluster.getNodes()) {
			if (cyNodes.get(node.getCyNode()) != null) {
				n++;
			}
		}
		return n;
	}

	public static CnSNodeAnnotationManager getInstance() {
		if (instance == null) {
			instance = new CnSNodeAnnotationManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	//public CnSEventResult<? extends Object> cnsEventOccured(CnSEvent event) {
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		String value, s;
		CnSNodeAnnotation annotation;
		File inputFile;
		int fromLine;
		BufferedReader br;
		String[] word, anno;
		Iterator<CyRow> it;
		int found_nodes, mapped_annotations;
		HashSet<String> nodes_in_file;
		CyNetwork network;
		Vector<String> annots;
		CnSCluster cluster;
		HashSet<String> annotations_in_file;
		CnSNodeAnnotationFile af;
		
		switch (event.getAction()) {
			case PARSE_ANNOTATIONS :
				inputFile = (File)event.getParameter(FILE);
				fromLine = (Integer)event.getParameter(FROM_LINE);
				network = (CyNetwork)event.getParameter(NETWORK);
				annots = new Vector<String>();
				try {
					br = new BufferedReader(new FileReader(inputFile));
					for (int i = 1; i < fromLine; i++) br.readLine();
					found_nodes = mapped_annotations = 0;
					nodes_in_file = new HashSet<String>();
					annotations_in_file = new HashSet<String>();
					while ((s = br.readLine()) != null) {
						word = s.split("\t");
						nodes_in_file.add(word[0]);
						anno = word[1].split(";");
						for (String q : anno)
							if (! q.equals("")) 
								annotations_in_file.add(q);
						it = network.getDefaultNodeTable().getMatchingRows("shared name", word[0]).iterator();
						if (it.hasNext()) {
							found_nodes++;
							//node = network.getNode(it.next().get(network.getDefaultNodeTable().getPrimaryKey().getName(), Long.class));
							for (String q : anno)
								if (! q.equals("")) 
									if (!annots.contains(q)) {
										mapped_annotations++;
										annots.addElement(q);
									}
						}
					}
					br.close();

					int[] results = new int[6];
					results[0] = nodes_in_file.size();
					results[1] = annotations_in_file.size();
					results[2] = found_nodes;
					results[3] = mapped_annotations;
					results[4] = network.getNodeCount();
					results[5] = annotations_in_file.size();

					ret = results;
					System.err.println("Total nodes in file : " + nodes_in_file.size());
					System.err.println("Total annotations in file : " + annotations_in_file.size());
					System.err.println("Total nodes in graph : " + network.getNodeCount());
					System.err.println("Found nodes in graph : " + found_nodes);
					System.err.println("Mapped annotations in graph : " + mapped_annotations);
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case LOAD_ANNOTATIONS :
				inputFile = (File)event.getParameter(FILE);
				fromLine = (Integer)event.getParameter(FROM_LINE);
				TaskMonitor taskMonitor = (TaskMonitor)event.getParameter(TASK);
				CnSNodeAnnotationFile aif = new CnSNodeAnnotationFile(inputFile, fromLine);

				files.addElement(aif);

				taskMonitor.setTitle("Importing annotations from " + aif.getFile().getName());
				int N = 0, p = 0;
				try {
					br = new BufferedReader(new FileReader(aif.getFile()));
					for (int i = 1; i < fromLine; i++) br.readLine();
					while ((s = br.readLine()) != null) N++;
					br.close();
					br = new BufferedReader(new FileReader(aif.getFile()));
					for (int i = 1; i < fromLine; i++) br.readLine();
					while ((s = br.readLine()) != null) {
						word = s.split("\t");
						anno = word[1].split(";");
						for (String q : anno)
							if (! q.equals("")) {
								CnSTrieNode w = annotationTrie.addWord(q);
								aif.addElement(w, word[0]);
							}
						p++;
						taskMonitor.setProgress((double)p / (double)N * 0.9D);
					}
					br.close();
					addAnnotations(aif);
					taskMonitor.setProgress(0.98);
					makeCyNodesHashMap();
					makeClustersHashMap();
					taskMonitor.setProgress(1.0);
					ret = aif;
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				break;

			case ADD_ANNOTATION :
				value = (String)event.getParameter(VALUE);
				inputFile = (File)event.getParameter(FILE);
				break;

			case REMOVE_ANNOTATION :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				break;

			case REMOVE_NODE_ANNOTATION :
				value = (String)event.getParameter(VALUE);
				break;

			case GET_NODES :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				ret = annotations.get(annotation);
				if (ret == null) ret = new CnSNodeNetworkSet();
				break;

			case GET_ANNOTATIONS :
				Vector<CnSNodeAnnotation> v = new Vector<CnSNodeAnnotation>(annotations.keySet());
				ret = v;
				break;

			case PRINT_ANNOTATIONS :
				break;

			case LOOK_FOR_ANNOTATIONS :
				ret = annotationTrie.getAnnotations((String)event.getParameter(PREFIX));
				break;

			case GET_CLUSTER_ANNOTATIONS :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				if (cluster2annotation.get(cluster) == null) {
					Vector<CnSNodeAnnotation> annot = new Vector<CnSNodeAnnotation>();
					for (CnSNode cnsNode : cluster.getNodes()) {
						Vector<CnSNodeAnnotation> a = cyNodes.get(cnsNode.getCyNode());
						if (a != null)
							for (CnSNodeAnnotation a2 : a)
								if (!annot.contains(a2)) {
									annot.addElement(a2);
									if (annotation2cluster.get(a2) == null) {
										Vector<CnSCluster> vcl = new Vector<CnSCluster>();
										vcl.addElement(cluster);
										annotation2cluster.put(a2, vcl);
									}
									else if (! annotation2cluster.get(a2).contains(cluster))
										annotation2cluster.get(a2).addElement(cluster);
								}
					}
					cluster2annotation.put(cluster, annot);
				}
				ret = cluster2annotation.get(cluster);
				break;

			case CLEAR_CLUSTERS_ANNOTATION :
				cluster2annotation.clear();
				break;

			case GET_ANNOTATION :
				value = (String)event.getParameter(ANNOTATION);
				CnSTrieNode tn = annotationTrie.get(value);
				if (tn != null) 
					ret = tn.getAnnotation();
				else
					ret = null;
				break;

			case GET_ANNOTATED_NODES :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				
				Vector<CyNode> vcn = new Vector<CyNode>();
				if ((cluster != null)  && (annotation == null)) {
					ret = getClusterAnnotationNumber(cluster);
				}
				else {
					if ((annotation == null) || (cluster == null))
						vcn = new Vector<CyNode>(cyNodes.keySet());
					else if ((annotation != null) && (cluster != null)) {
						for (CnSNodeNetwork cnn : annotations.get(annotation).getNodeNetworks()) {
							if (cluster.contains(cnn.getNode())) vcn.addElement(cnn.getNode());
						}
					}
					ret = vcn;
				}
				break;

			case GET_ANNOTATED_CLUSTERS :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				if (annotation != null)
					ret = annotation2cluster.get(annotation);
				break;

			case UNLOAD_ANNOTATIONS :
				af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);

				if (af != null) {
					removeAnnotations(af);
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.INIT_ANNOTATION_PANEL, CnSEventManager.PARTITION_PANEL);
					//CnSEventManager.handleMessage(ev);
					makeCyNodesHashMap();
					makeClustersHashMap();
					files.removeElement(af);
					ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL);
					CnSEventManager.handleMessage(ev);
				}
				break;

			case ANNOTATE_NETWORK :
				af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);
				network = (CyNetwork)event.getParameter(NETWORK);
				TaskMonitor task = (TaskMonitor)event.getParameter(TASK);
				
				System.err.println("ANNOTATE_NETWORK " + network);
				List<String> netNodes = network.getDefaultNodeTable().getColumn("shared name").getValues(String.class);

				Set<CyNode> nodes;
				CyNode cn;
				int index = 0;
				
				for (CnSTrieNode trieNode : af.getAllAnnotations()) {
					CnSNodeAnnotation annot = trieNode.getAnnotation();
					for (CnSAnnotationTarget target : annot.getTargets()) {
						if (target.getFiles().contains(af)) {
							if (netNodes.contains(target.getTarget())) {
								nodes = getNodesWithValue(network, network.getDefaultNodeTable(), "shared name", target.getTarget());
								cn = nodes.iterator().next();
								CnSNodeNetwork nodeNetwork = annotations.get(annot).getNodeNetwork(network, cn);
								if (nodeNetwork != null) {
									nodeNetwork.addAnnotationFile(af);
								}
								else {
									CnSNodeNetwork nn = new CnSNodeNetwork(network, cn);
									nn.addAnnotationFile(af);
									annotations.get(annot).getNodeNetworks().addElement(nn);
								}
							}
						}
					}
					index++;
					if (task != null) task.setProgress((double)index / (double)af.getAllAnnotations().size());
				}
				
				makeCyNodesHashMap();
				makeClustersHashMap();
				if (annotatedNetworks.get(af) == null) annotatedNetworks.put(af, new Vector<CyNetwork>());
				annotatedNetworks.get(af).addElement(network);
				
				cluster2pv.clear();
				annotation2pv.clear();
				break;
				
			case COMPUTE_ENRICHMENT :
				CnSPartition part = (CnSPartition)event.getParameter(PARTITION);
				taskMonitor = (TaskMonitor)event.getParameter(TASK);
				
				computeEnrichment(part.getInputNetwork(), part.getClusters(), taskMonitor);
				break;

			case IS_NETWORK_ANNOTATED :
				af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);
				network = (CyNetwork)event.getParameter(NETWORK);
				ret = Boolean.valueOf(annotatedNetworks.containsKey(af));
				if ((Boolean)ret) 
					ret = (Boolean)ret & Boolean.valueOf(annotatedNetworks.get(af).contains(network));
				break;

			case DEANNOTATE_NETWORK :
				System.err.println("DEANNOTATE_NETWORK");
				af = (CnSNodeAnnotationFile)event.getParameter(ANNOTATION_FILE);
				network = (CyNetwork)event.getParameter(NETWORK);
				
				Vector<CnSNodeNetwork> toRemove = new Vector<CnSNodeNetwork>(); 
				for (CnSNodeNetworkSet nns : annotations.values()) {
					toRemove.clear();
					for (CnSNodeNetwork nn : nns.getNodeNetworks()) {
						nn.getAnnotationFiles().removeElement(af);
						if (nn.getAnnotationFiles().size() == 0) {
							toRemove.addElement(nn);
						}
					}
					nns.getNodeNetworks().removeAll(toRemove);
				}
				
				makeCyNodesHashMap();
				makeClustersHashMap();
				annotatedNetworks.get(af).removeElement(network);
				
				CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				part = (CnSPartition)CnSEventManager.handleMessage(ev);
				if (part != null) {
					computeEnrichment(network, part.getClusters(), null);
					ev = new CnSEvent(CnSPartitionPanel.INIT_ANNOTATION_PANEL, CnSEventManager.PARTITION_PANEL);
					ev.addParameter(CnSPartitionPanel.PARTITION, part);
					CnSEventManager.handleMessage(ev);
				}
				
				ev = new CnSEvent(CnSPartitionPanel.REFRESH, CnSEventManager.PARTITION_PANEL);
				CnSEventManager.handleMessage(ev);
				break;

			case GET_NETWORK_ANNOTATIONS :
				network = (CyNetwork)event.getParameter(NETWORK);
				for (CnSNodeNetworkSet nns : annotations.values()) {
					Vector<CnSNodeNetwork> v1 = nns.getNodeNetworks();
					for (CnSNodeNetwork nn : v1) {
						if (nn.getNetwork() == network) {
							
						}
					}
				}
				break;
				

			case GET_NETWORK_ANNOTATED_NODES :
				network = (CyNetwork)event.getParameter(NETWORK);
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				Vector<String> vn = new Vector<String>();
				if (annotation != null)
					for (CnSAnnotationTarget t : annotation.getTargets()) {
						nodes = getNodesWithValue(network, network.getDefaultNodeTable(), "shared name", t.getTarget());
						if (nodes.size() > 0) {
							cn = nodes.iterator().next();
							CnSNodeNetwork nn = new CnSNodeNetwork(network, cn/*, naf*/); 
							if (annotations.get(annotation) != null) {
								Vector<CnSNodeNetwork> nodeNetworks = annotations.get(annotation).getNodeNetworks();
								if (nodeNetworks.contains(nn)) vn.addElement(t.getTarget());
							}
						}
					}
				else 
					for (CyNode node : network.getNodeList()) {
						if (cyNodes.get(node) != null) vn.addElement(node.SUID);
					}
				ret = vn;
				break;

			case GET_NETWORK_MAPPED_ANNOTATIONS :
				network = (CyNetwork)event.getParameter(NETWORK);
				Vector<CnSNodeAnnotation> va = new Vector<CnSNodeAnnotation>();
				for (CnSNodeAnnotation na : annotations.keySet()) {
					if (annotations.get(na) != null) {
						//System.err.print("ANNOT : " + na.getValue() + " -> ");
						//System.err.println(annotations.get(na).getNodeNetworks());
						Vector<CnSNodeNetwork> nodeNetworks = annotations.get(na).getNodeNetworks();
						for (CnSNodeNetwork nn : nodeNetworks) {
							if (nn.getNetwork() == network) {
								va.addElement(na);
								break;
							}
						}
					}
				}
				ret = va;
				break;
				
			case GET_BH_HYPERGEOMETRIC :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				Double alpha = (Double)event.getParameter(ALPHA);
				if (alpha == null) alpha = currentAlpha;
				
				Vector <CnSAnnotationClusterPValue> pvalues = cluster2pv.get(cluster);
				
				if (pvalues == null) 
					if (annotation == null)
						ret = new Vector<CnSAnnotationClusterPValue>();
					else
						ret = new CnSAnnotationClusterPValue(annotation, cluster, 0, 0, 0);
				else if (annotation != null) {
					//pvalues.removeIf(new CnSPredicate(alpha));
					
					ret = new CnSAnnotationClusterPValue(annotation, cluster, 0, 0, 0);
					for (CnSAnnotationClusterPValue pv : pvalues) {
						if (pv.getAnnotation() == annotation) {
							ret = pv;
							break;
						}
					}
				}
				else {
					//pvalues.removeIf(new CnSPredicate(alpha));
					ret = pvalues;
				}
				break;
				
			case GET_BH_FILTERED_ANNOTATIONS :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				alpha = (Double)event.getParameter(ALPHA);
				if (alpha == null) alpha = currentAlpha;
				
				pvalues = cluster2pv.get(cluster);
				if (pvalues != null) pvalues = (Vector<CnSAnnotationClusterPValue>) pvalues.clone();
				if (pvalues != null) 
					pvalues.removeIf(new CnSPredicate(alpha));
				else
					pvalues = new Vector<CnSAnnotationClusterPValue>();
				ret = pvalues;
				break;
				
			case GET_ENRICHED_CLUSTERS :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				double threshold = (Double)event.getParameter(HG_THRESHOLD);
				ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				part = (CnSPartition)CnSEventManager.handleMessage(ev);
				Vector<CnSCluster> clusters = new Vector<CnSCluster>();
				int annotated_cluster_count = 0;
				
				if (part != null)
					if (threshold != -1) {
						if (annotation2pv.get(annotation) != null) {
							for (CnSAnnotationClusterPValue pv : annotation2pv.get(annotation)) {
								if (part.containsCluster(pv.getCluster()) && pv.getBHValue() <= threshold) {
									clusters.addElement(pv.getCluster());
								}
							}
						}
					}
					else if (annotation2cluster.get(annotation) != null) {
						for (CnSCluster c : annotation2cluster.get(annotation)) {
							annotated_cluster_count = 0;
							for (CnSNodeNetwork cnn : annotations.get(annotation).getNodeNetworks()) {
								if (c.contains(cnn.getNode())) annotated_cluster_count++;
							}
							if (annotated_cluster_count >= (c.getNbNodes() / 2.0)) clusters.addElement(c);
						}
					}
				ret = clusters;
				break;
				
			case GET_TOP3_CLUSTERS :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				threshold = (Double)event.getParameter(HG_THRESHOLD);
				TreeSet<CnSAnnotationClusterPValue> clustersHash = new TreeSet<CnSAnnotationClusterPValue>();
				ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
				part = (CnSPartition)CnSEventManager.handleMessage(ev);
				if (part != null)
					if (annotation2pv.get(annotation) != null) {
						for (CnSAnnotationClusterPValue pv : annotation2pv.get(annotation)) {
							if (part.containsCluster(pv.getCluster()) && pv.getBHValue() <= threshold) {
								clustersHash.add(pv);
							}
						}
					}
				Iterator<CnSAnnotationClusterPValue> itp = clustersHash.descendingIterator();
				index = 0;
				clusters = new Vector<CnSCluster>();
				while (itp.hasNext() && index < 3) {
					clusters.addElement(itp.next().getCluster());
					index ++;
				}
				ret = clusters;
				break;
				
			case REFRESH_CLUSTER_HASMAP : 
				CnSPartition partition = (CnSPartition)event.getParameter(PARTITION);
				if (partition != null) {
					taskMonitor = (TaskMonitor)event.getParameter(TASK);
					taskMonitor.setTitle("Computing enrichment for partition " + partition.getName() + " ...");
					taskMonitor.setProgress(0.0);
					computeEnrichment(partition.getInputNetwork(), partition.getClusters(), taskMonitor);
					ev = new CnSEvent(CnSPartitionPanel.INIT, CnSEventManager.PARTITION_PANEL);
					ev.addParameter(CnSPartitionPanel.PARTITION, partition);
					//CnSEventManager.handleMessage(ev);
				}
				makeClustersHashMap();
				break;
				
			case REMOVE_ENRICHMENT :
				partition = (CnSPartition)event.getParameter(PARTITION);
				if (partition != null) removeEnrichment(partition.getClusters());
				break;
				
			case SET_ALPHA :
				currentAlpha = (Double)event.getParameter(ALPHA);
				break;
				
			case GET_ALPHA :
				ret = currentAlpha;
				break;
		}
		return ret;
	}
	/**
     * Get all the nodes with a given attribute value.
     *
     * This method is effectively a wrapper around {@link CyTable#getMatchingRows}.
     * It converts the table's primary keys (assuming they are node SUIDs) back to
     * nodes in the network.
     *
     * Here is an example of using this method to finbr.close();	d all nodes with a given name:
     *
     * {@code
     *   CyNetwork net = ...;CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
				ev.addParameter(CnSNodeAnnotationManager.NETWORK, getData(NETWORK));
				CnSEventManager.handleMessage(ev);/home/fafa/eclipse-workspace/tagc-clustnsee3/
				
				ev = new CnSEvent(CnSControlPanel.REMOVE_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL);
				ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
				CnSEventManager.handleMessage(ev)
     *   String nodeNameToSearchFor = ...;
     *   Set<CyNode> nodes = getNodesWithValue(net, net.getDefaultNodeTable(), "name", nodeNameToSearchFor);
     *   // nodes now contains all CyNodes with the name specified by nod			}eNameToSearchFor
     * }
     * @param net The network that contains the nodes you are looking for.
     * @param table The node table that has the attribute value you are looking for;
     * the primary keys of this table <i>must</i> be SUIDs of n
[INFO] ------------------------------------------------------------------------odes in {@code net}.
     * @param colname The name of the column with the attribute value
     * @param value The attribute value
     * @return A set of {@code CyNode}s with a matching value, or an empty set if no nodes match.
     */
    private static Set<CyNode> getNodesWithValue(final CyNetwork net, final CyTable table, final String colname, final Object value) {
        final Collection<CyRow> matchingRows = table.getMatchingRows(colname, value);
        final Set<CyNode> nodes = new HashSet<CyNode>();
        final String primaryKeyColname = table.getPrimaryKey().getName();

        for (final CyRow row : matchingRows) {
            final Long nodeId = row.get(primaryKeyColname, Long.class);
            if (nodeId == null) continue;
            final CyNode node = net.getNode(nodeId);
            if (node == null) continue;
            nodes.add(node);
        }
        return nodes;
    }

	private void addAnnotations(CnSNodeAnnotationFile aif) {
		Iterator<CnSTrieNode> it = aif.getAllAnnotations().iterator();
		CnSTrieNode ann;
		CnSNodeAnnotation nodeAnnotation;
		while (it.hasNext()) {
			ann = it.next();
			if (ann.getAnnotation() == null) {
				nodeAnnotation = new CnSNodeAnnotation(ann/*, aif*/);
				ann.setAnnotation(nodeAnnotation);
				annotations.put(nodeAnnotation, new CnSNodeNetworkSet());
			}
			else
				nodeAnnotation = ann.getAnnotation();
			for (String tar : aif.getTargets(ann)) nodeAnnotation.addTarget(tar, aif);
		}
	}

	private void removeAnnotations(CnSNodeAnnotationFile aif) {
		Iterator<CnSTrieNode> it = aif.getAllAnnotations().iterator();
		CnSTrieNode ann;
		CnSNodeAnnotation nodeAnnotation;
		Vector<CnSAnnotationTarget> toRemove = new Vector<CnSAnnotationTarget>();
		System.err.print("Remove annotations : " + annotations.size());
		while (it.hasNext()) {
			ann = it.next();
			nodeAnnotation = ann.getAnnotation();
			for (CnSAnnotationTarget t : nodeAnnotation.getTargets()) 
				if (t.getFiles().contains(aif))
					toRemove.addElement(t);
			nodeAnnotation.getTargets().removeAll(toRemove);
			if (nodeAnnotation.getTargets().size() == 0) {
				annotations.remove(nodeAnnotation);
				ann.removeAnnotation();
				annotationTrie.removeWord(nodeAnnotation.getValue());
			}
			toRemove.clear();
		}
		System.err.println(" -> " + annotations.size());
	}

	private void makeCyNodesHashMap() {
		cyNodes.clear();
		for (CnSNodeAnnotation annotation : annotations.keySet()) {
			Vector<CnSNodeNetwork> nodes = annotations.get(annotation).getNodeNetworks();
			for (CnSNodeNetwork node : nodes) {
				Vector<CnSNodeAnnotation> a = cyNodes.get(node.getNode());
				if (a == null) {
					Vector<CnSNodeAnnotation> b = new Vector<CnSNodeAnnotation>();
					b.addElement(annotation);
					cyNodes.put(node.getNode(), b);
				}
				else if (!a.contains(annotation))
					a.addElement(annotation);
			}
		}
	}

	private void makeClustersHashMap() {
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_ALL_CLUSTERS, CnSEventManager.PARTITION_MANAGER);
		Vector<CnSCluster> all_clusters = (Vector<CnSCluster>)CnSEventManager.handleMessage(ev);
		cluster2annotation.clear();
		annotation2cluster.clear();
		for (CnSCluster cl : all_clusters)
			if (cluster2annotation.get(cl) == null) {
				Vector<CnSNodeAnnotation> annot = new Vector<CnSNodeAnnotation>();
				for (CnSNode cnsNode : cl.getNodes()) {
					Vector<CnSNodeAnnotation> a = cyNodes.get(cnsNode.getCyNode());
					if (a != null)
						for (CnSNodeAnnotation a2 : a)
							if (!annot.contains(a2)) {
								annot.addElement(a2);
								if (annotation2cluster.get(a2) == null) {
									Vector<CnSCluster> vcl = new Vector<CnSCluster>();
									vcl.addElement(cl);
									annotation2cluster.put(a2, vcl);
								}
								else if (! annotation2cluster.get(a2).contains(cl))
									annotation2cluster.get(a2).addElement(cl);
							}
					cluster2annotation.put(cl, annot);
				}
				System.err.println("--- " + cl.getName() + " -> " + annot.size());
			}
	}
	public void printAnnotations() {
		int N = 0;
		for (CnSNodeAnnotation key : annotations.keySet()) {
			System.out.println(++N + " " + key.getValue());
			for (CnSNodeNetwork value : annotations.get(key).getNodeNetworks()) {
				System.out.println("  " + value.getNode().getSUID());
			}
		}
	}
	
	private void computeEnrichment(CyNetwork network, Vector<CnSCluster> clusters, TaskMonitor taskMonitor) {
		
		HypergeometricDistribution dist;
	
		Vector<String> annotatedNodes = new Vector<String>();
		Vector<CnSNodeAnnotation> population = new Vector<CnSNodeAnnotation>();
		
		Vector<CyNode> vcn = new Vector<CyNode>();
		if (taskMonitor != null) taskMonitor.setProgress(0.0);
		int annotationIndex = 0;
		
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_NETWORK_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
		ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
		population = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
				
		Vector<CnSAnnotationClusterPValue> pvalues = new Vector<CnSAnnotationClusterPValue>();
		ev = new CnSEvent(CnSNodeAnnotationManager.GET_NETWORK_MAPPED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
		Vector<CnSNodeAnnotation> vv = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
		System.err.println(vv.size());
	
		for (CnSNodeAnnotation ann : vv) {
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_NETWORK_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
			ev.addParameter(CnSNodeAnnotationManager.NETWORK, network);
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, ann);
			annotatedNodes = (Vector<String>)CnSEventManager.handleMessage(ev);
			
			for (CnSCluster c : clusters) {
				ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, c);
				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, ann);
				vcn = (Vector<CyNode>)CnSEventManager.handleMessage(ev);
				//dist = new HypergeometricDistribution(network.getNodeCount(), annotatedNodes.size(), c.getNbNodes());
				if (c.getNbNodes() <= population.size()) {
					dist = new HypergeometricDistribution(population.size(), annotatedNodes.size(), c.getNbNodes());
					if (vcn.size() > 0) {
						double prob = dist.upperCumulativeProbability(vcn.size());
						pvalues.addElement(new CnSAnnotationClusterPValue(ann, c, annotatedNodes.size(), vcn.size(), prob));
					}
				}
			}
			annotationIndex++;
			if (taskMonitor != null) taskMonitor.setProgress((double)annotationIndex / (double)vv.size());
		}
		pvalues.sort(null);
		double qv;
		int index;
		CnSAnnotationClusterPValue previous_pv = null;
		for (CnSAnnotationClusterPValue pv : pvalues) {
			index = pvalues.size() - pvalues.indexOf(pv);
			qv = Math.min(pv.getPValue() * (double)pvalues.size() / (double)index, 1.0D);
			if (index < pvalues.size()) qv = Math.min(qv, previous_pv.getBHValue());
			pv.setBHValue(qv);
			previous_pv = pv;
		}
		//pvalues.removeIf(new CnSPredicate(currentAlpha));
		//System.err.println("*** ENRICHMENT");
		for (CnSAnnotationClusterPValue pv : pvalues) {
			if (cluster2pv.get(pv.getCluster()) == null)
				cluster2pv.put(pv.getCluster(), new Vector<CnSAnnotationClusterPValue>());
			if (annotation2pv.get(pv.getAnnotation()) == null)
				annotation2pv.put(pv.getAnnotation(), new Vector<CnSAnnotationClusterPValue>());
			cluster2pv.get(pv.getCluster()).addElement(pv);
			annotation2pv.get(pv.getAnnotation()).addElement(pv);
			//System.err.println(pv.getAnnotation().getValue() + " " + pv.getCluster().getName() + " : " + (pvalues.size() - pvalues.indexOf(pv)) + " , " + pv.getPValue() + " , " + pv.getBHValue());
		}
		//System.err.println("*** ----------");
	}
	
	public void removeEnrichment(Vector<CnSCluster> clusters)  {
		for (CnSCluster c : clusters) cluster2pv.remove(c);
	}
}
