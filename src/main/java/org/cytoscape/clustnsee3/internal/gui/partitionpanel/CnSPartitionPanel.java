/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel;

import java.awt.Component;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationanalysis.CnSAnnotationAnalysisPanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusteranalysis.CnSClusterAnalysisPanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusterannotationmatrix.CnSClusterAnnotationMatrixPanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTablePanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSPartitionPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -3877080938361953871L;
	
	public static final int INIT = 1;
	public static final int CLEAR = 2;
	public static final int SELECT_CLUSTER = 3;
	public static final int SEARCH = 4;
	public static final int INIT_ANNOTATION_PANEL = 5;
	public static final int REFRESH = 6;
	public static final int SET_SEARCH_ANNOTATION = 7;
	public static final int GET_SELECTED_ANNOTATION = 8;
	public static final int SEARCH_ANNOTATION = 10;
	public static final int EXPORT_CLUSTER_LIST_DATA = 13;
	public static final int EXPORT_CLUSTER_ANALYSIS_DATA = 14;
	public static final int EXPORT_ANNOTATION_TERM_ANALYSIS_DATA = 15;
	public static final int GET_SELECTED_STAT = 26;
	public static final int GET_CURRENT_BH_THRESHOLD = 27;
	public static final int GET_CURRENT_MAJORITY_THRESHOLD = 28;
	public static final int GET_HIDE_SMALL_CLUSTERS = 29;
	public static final int FIRE_TABLE_DATA_CHANGED = 30;
	public static final int EXPORT_CLUSTER_ANNOTATIONS_MATRIX_DATA = 31;
	
	public static final int PARTITION = 1001;
	public static final int CLUSTER = 1002;
	public static final int ANNOTATION = 1003;
	public static final int OUTPUT_FILE = 1004;
	
	private static CnSPartitionPanel instance;
	private CnSPartitionTablePanel partitionTablePanel;
	private CnSClusterAnalysisPanel clusterAnalysisPanel;
	private CnSAnnotationAnalysisPanel annotationAnalysisPanel;
	//private CnSMulticlassedNodesPanel multiclassedNodesPanel;
	private CnSClusterAnnotationMatrixPanel clusterAnnotationMatrixPanel;
	private static JTabbedPane tabbedPane;
	
	public static CnSPartitionPanel getInstance() {
		if (instance == null) instance = new CnSPartitionPanel("C&S Partition");
		return instance;
	}
	
	private CnSPartitionPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	public String getActionName(int k) {
		switch(k) {
			case INIT : return "INIT";
			case CLEAR : return "CLEAR";
			case SELECT_CLUSTER : return "SELECT_CLUSTER";
			case SEARCH : return "SEARCH";
			case INIT_ANNOTATION_PANEL : return "INIT_ANNOTATION_PANEL";
			case REFRESH : return "REFRESH";
			case SET_SEARCH_ANNOTATION : return "SET_SEARCH_ANNOTATION";
			case GET_SELECTED_ANNOTATION : return "GET_SELECTED_ANNOTATION";
			case SEARCH_ANNOTATION : return "SEARCH_ANNOTATION";
			case EXPORT_CLUSTER_LIST_DATA : return "EXPORT_CLUSTER_LIST_DATA";
			case EXPORT_CLUSTER_ANALYSIS_DATA : return "EXPORT_CLUSTER_ANALYSIS_DATA";
			case EXPORT_ANNOTATION_TERM_ANALYSIS_DATA : return "EXPORT_ANNOTATION_TERM_ANALYSIS_DATA";
			case GET_SELECTED_STAT : return "GET_SELECTED_STAT";
			case GET_CURRENT_BH_THRESHOLD : return "GET_CURRENT_BH_THRESHOLD";
			case GET_CURRENT_MAJORITY_THRESHOLD : return "GET_CURRENT_MAJORITY_THRESHOLD";
			case GET_HIDE_SMALL_CLUSTERS : return "GET_HIDE_SMALL_CLUSTERS";
			case FIRE_TABLE_DATA_CHANGED : return "FIRE_TABLE_DATA_CHANGED";
			case EXPORT_CLUSTER_ANNOTATIONS_MATRIX_DATA : return "EXPORT_CLUSTER_ANNOTATIONS_MATRIX_DATA";
			default : return "UNDEFINED_ACTION";
		}
	}

	public String getParameterName(int k) {
		switch(k) {
			case PARTITION : return "PARTITION";
			case CLUSTER : return "CLUSTER";
			case ANNOTATION : return "ANNOTATION";
			case OUTPUT_FILE : return "OUTPUT_FILE";
			default : return "UNDEFINED_PARAMETER";
		}
	}

	public void initGraphics() {
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, true).getValue();
		rBundle = CyActivator.getResourcesBundle();
		
		partitionTablePanel = new CnSPartitionTablePanel();
		clusterAnalysisPanel = new CnSClusterAnalysisPanel();
		annotationAnalysisPanel = new CnSAnnotationAnalysisPanel();
		//multiclassedNodesPanel = new CnSMulticlassedNodesPanel();
		clusterAnnotationMatrixPanel = new CnSClusterAnnotationMatrixPanel();
		
		ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
		init(partition);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add(rBundle.getString("CnSPartitionPanel.ClusterTableTab"), partitionTablePanel);
		tabbedPane.add(rBundle.getString("CnSPartitionPanel.ClusterAnalysisTab"), clusterAnalysisPanel); 
		tabbedPane.add(rBundle.getString("CnSPartitionPanel.AnnotationTermAnalysisTab"), annotationAnalysisPanel);
		//tabbedPane.add(rBundle.getString("CnSPartitionPanel.MulticlassedNodesTab"), multiclassedNodesPanel);
		tabbedPane.add(rBundle.getString("CnSPartitionPanel.ClusterAnnotationMatrixTab"), clusterAnnotationMatrixPanel);
		tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 12));
		addComponent(tabbedPane, 0, 0, 3, 1, 1.0, 1.0, CENTER, BOTH, 0, 0, 0, 0, 0, 0);
	}
	
	public void init(CnSPartition partition) {
		if (partition != null) {
			annotationAnalysisPanel.init(partition);
			//multiclassedNodesPanel.init(partition);
		}
	}
	
	private void initListeners() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		CnSEventResult<?> ret = new CnSEventResult<Object>(null);
		final CnSCluster cluster;
		final CnSPartition partition;
		CnSEvent ev;
		
		if (log) CnSLogger.getInstance().LogCnSEvent(event, this);
		
		switch (event.getAction()) {
			case INIT :
				partition = (CnSPartition)event.getParameter(PARTITION);
				init(partition);
				if (partition != null) {
					partitionTablePanel.init(partition);
					clusterAnalysisPanel.init(partition);
					annotationAnalysisPanel.init(partition);
					//multiclassedNodesPanel.init(partition);
					clusterAnnotationMatrixPanel.init(partition);
				}
				else {
					clusterAnalysisPanel.init();
				}
				break;
			
			case CLEAR :
				partitionTablePanel.clear();
				annotationAnalysisPanel.clear();
				clusterAnalysisPanel.clear();
				break;
				
			case SELECT_CLUSTER :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partitionTablePanel.selectCluster(cluster);
				if (cluster != null) {
					annotationAnalysisPanel.selectCluster(cluster);
					clusterAnalysisPanel.selectCluster(cluster.getID());
				}
				else {
					annotationAnalysisPanel.selectCluster(null);
					clusterAnalysisPanel.selectCluster(0);
				}
				break;
				
			case SEARCH :
				CnSNodeAnnotation annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				cluster = partitionTablePanel.getSelectedCluster();
				annotationAnalysisPanel.setSelectedAnnotation(annotation);
				clusterAnalysisPanel.refresh();
				partitionTablePanel.selectCluster(cluster);
				annotationAnalysisPanel.selectCluster(cluster);
				clusterAnalysisPanel.selectAnnotation(annotation);
				/*ev = new CnSEvent(CnSInfoPanel.TAG_NODES, CnSEventManager.INFO_PANEL, this.getClass());
				if (annotation != null) ev.addParameter(CnSInfoPanel.ANNOTATION, annotation);
				CnSEventManager.handleMessage(ev, true);*/
				break;
				
			case SEARCH_ANNOTATION :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				ev = new CnSEvent(CnSPartitionPanel.SET_SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
				ev.addParameter(CnSPartitionPanel.ANNOTATION, annotation);
				CnSEventManager.handleMessage(ev, true);
				clusterAnalysisPanel.selectAnnotation(annotation);
				break;
				
			case INIT_ANNOTATION_PANEL :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partition = (CnSPartition)event.getParameter(PARTITION);
				if (cluster != null)
					clusterAnalysisPanel.init(cluster);
				else if (partition != null)
					clusterAnalysisPanel.refresh(partition);
				else
					clusterAnalysisPanel.clear();
				break;
				
			case REFRESH :
				partition = (CnSPartition)event.getParameter(PARTITION);
				partitionTablePanel.refresh();
				clusterAnnotationMatrixPanel.init(partition);
				annotationAnalysisPanel.init(partition);
				if (partition != null) 
					clusterAnalysisPanel.refresh(partition);
				else
					clusterAnalysisPanel.refresh();
				break;
				
			case SET_SEARCH_ANNOTATION :
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				//cluster = partitionTablePanel.getSelectedCluster();
				//partitionTablePanel.selectCluster(cluster);
				annotationAnalysisPanel.getSearchComponent().setAnnotation(annotation);
				break;
				
			case GET_SELECTED_ANNOTATION :
				ret = new CnSEventResult<CnSNodeAnnotation>(annotationAnalysisPanel.getSelectedAnnotation());
				break;
				
			case EXPORT_CLUSTER_LIST_DATA :
				File file = (File)event.getParameter(OUTPUT_FILE);
				try {
					BufferedWriter br= new BufferedWriter(new FileWriter(file));
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
					br.write("#ClustnSee cluster list");
					br.newLine();
					br.write("#Algorithm: ");
					if (partition != null) br.write(partition.getAlgorithmName());
					br.newLine();
					br.write("#Network: ");
					if (partition != null) br.write(partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
					br.newLine();
					br.write("#Scope: ");
					if (partition != null) br.write(partition.getScope());
					br.newLine();
					if (partition != null) {
						Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
						while (k.hasNext()) {
							int key = k.next();
							br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
							br.newLine();
						}
					}
					br.write("#Statistics: " + partitionTablePanel.getSelectedStatName());
					br.newLine();
					br.write("#Threshold: " + partitionTablePanel.getCurrentThreshold() + "%");
					br.newLine();
					partitionTablePanel.write(br);
					br.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case EXPORT_CLUSTER_ANALYSIS_DATA :
				file = (File)event.getParameter(OUTPUT_FILE);
				try {
					BufferedWriter br= new BufferedWriter(new FileWriter(file));
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CnSPartitionPanel.GET_HIDE_SMALL_CLUSTERS, CnSEventManager.PARTITION_PANEL, this.getClass());
					boolean hsc = (Boolean)CnSEventManager.handleMessage(ev, true).getValue();
					br.write("#ClustnSee cluster analysis");
					br.newLine();
					br.write("#Algorithm: ");
					if (partition != null) br.write(partition.getAlgorithmName());
					br.newLine();
					br.write("#Network: ");
					if (partition != null) br.write(partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
					br.newLine();
					br.write("#Scope: ");
					if (partition != null) br.write(partition.getScope());
					br.newLine();
					if (partition != null) {
						Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
						while (k.hasNext()) {
							int key = k.next();
							br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
							br.newLine();
						}
					}
					br.write("#Hide small clusters: " + hsc);
					br.newLine();
					if (clusterAnalysisPanel.getSelectedCluster() != null)
						br.write("#Cluster: " + clusterAnalysisPanel.getSelectedCluster().getID());
					else
						br.write("#Cluster: all");
					br.newLine();
					clusterAnalysisPanel.write(br);
					br.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case EXPORT_CLUSTER_ANNOTATIONS_MATRIX_DATA :
				file = (File)event.getParameter(OUTPUT_FILE);
				try {
					BufferedWriter br= new BufferedWriter(new FileWriter(file));
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CnSPartitionPanel.GET_HIDE_SMALL_CLUSTERS, CnSEventManager.PARTITION_PANEL, this.getClass());
					br.write("#ClustnSee cluster analysis");
					br.newLine();
					br.write("#Algorithm: ");
					if (partition != null) br.write(partition.getAlgorithmName());
					br.newLine();
					br.write("#Network: ");
					if (partition != null) br.write(partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
					br.newLine();
					br.write("#Scope: ");
					if (partition != null) br.write(partition.getScope());
					br.newLine();
					if (partition != null) {
						Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
						while (k.hasNext()) {
							int key = k.next();
							br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
							br.newLine();
						}
					}
					ev = new CnSEvent(CnSNodeAnnotationManager.GET_NETWORK_ANNOTATION_FILES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					ev.addParameter(CnSNodeAnnotationManager.NETWORK, partition.getInputNetwork());
					Vector<CnSNodeAnnotationFile> vcnaf = (Vector<CnSNodeAnnotationFile>)CnSEventManager.handleMessage(ev, true).getValue();
					br.write("#Annotation files: ");
					for (CnSNodeAnnotationFile sf : vcnaf) br.write(sf.getFile().getAbsolutePath() + ",");
					br.newLine();
					br.write("#Statistics: " + clusterAnnotationMatrixPanel.getSelectedStatName());
					br.newLine();
					
					clusterAnnotationMatrixPanel.write(br);
					br.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case EXPORT_ANNOTATION_TERM_ANALYSIS_DATA :
				file = (File)event.getParameter(OUTPUT_FILE);
				try {
					BufferedWriter br= new BufferedWriter(new FileWriter(file));
					ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL, this.getClass());
					partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
					ev = new CnSEvent(CnSPartitionPanel.GET_HIDE_SMALL_CLUSTERS, CnSEventManager.PARTITION_PANEL, this.getClass());
					boolean hsc = (Boolean)CnSEventManager.handleMessage(ev, true).getValue();
					
					br.write("#ClustnSee annotation terms analysis");
					br.newLine();
					br.write("#Algorithm: ");
					if (partition != null) br.write(partition.getAlgorithmName());
					br.newLine();
					br.write("#Network: ");
					if (partition != null) br.write(partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
					br.newLine();
					br.write("#Scope: ");
					if (partition != null) br.write(partition.getScope());
					br.newLine();
					if (partition != null) {
						Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
						while (k.hasNext()) {
							int key = k.next();
							br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
							br.newLine();
						}
					}
					br.write("#Hide small clusters: " + hsc);
					br.newLine();
					if (clusterAnalysisPanel.getSelectedAnnotation() != null)
						br.write("#Annotation: " + clusterAnalysisPanel.getSelectedAnnotation());
					else
						br.write("#Annotation: none");
					br.newLine();
					annotationAnalysisPanel.write(br);
					br.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case GET_SELECTED_STAT :
				ret = new CnSEventResult<Integer>(partitionTablePanel.getSelectedStat());
				break;
				
			case GET_CURRENT_BH_THRESHOLD :
				ret = new CnSEventResult<Double>(partitionTablePanel.getCurrentThreshold());
				break;
				
			case GET_CURRENT_MAJORITY_THRESHOLD :
				ret = new CnSEventResult<Double>(partitionTablePanel.getCurrentThreshold());
				break;
				
			case GET_HIDE_SMALL_CLUSTERS :
				ret = new CnSEventResult<Boolean>(partitionTablePanel.hideSmallClusters());
				break;
				
			case FIRE_TABLE_DATA_CHANGED :
				partitionTablePanel.fireTableDatachanged();
				annotationAnalysisPanel.fireTableDataChanged();
				
				/*cluster = partitionTablePanel.getSelectedCluster();
				clusterAnalysisPanel.refresh();
				partitionTablePanel.selectCluster(cluster);
				annotationAnalysisPanel.selectCluster(cluster);*/
				break;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return null;
	}
}
