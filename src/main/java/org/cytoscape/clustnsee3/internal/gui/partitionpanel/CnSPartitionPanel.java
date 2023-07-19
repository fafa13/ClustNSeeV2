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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable.CnSAnnotationTablePanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTablePanel;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.search.CnSSearchAnnotationComponent;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSPartitionPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -3877080938361953871L;
	private static CnSPartitionPanel instance;
	private CnSPartitionTablePanel partitionPanel;
	private CnSAnnotationTablePanel annotationPanel;
	//private static JSplitPane splitPane;
	private static JTabbedPane tabbedPane;
	private CnSSearchAnnotationComponent annotationSearchComponent;
	private CnSButton clearButton;
	private ImageIcon icon_delete;
	private CnSButton exportDataButton;
	private JComboBox<String> clusterList;
	
	public static final int INIT = 1;
	public static final int CLEAR = 2;
	public static final int SELECT_CLUSTER = 3;
	public static final int SEARCH = 4;
	public static final int INIT_ANNOTATION_PANEL = 5;
	public static final int REFRESH = 6;
	public static final int SET_SEARCH_ANNOTATION = 7;
	public static final int GET_SELECTED_ANNOTATION = 8;
	public static final int GET_SEARCHED_ANNOTATION = 9;
	public static final int SEARCH_ANNOTATION = 10;
	public static final int GET_SELECTED_CLUSTER = 11;
	public static final int SET_SELECTED_CLUSTER = 12;
	
	public static final int PARTITION = 1001;
	public static final int CLUSTER = 1002;
	public static final int ANNOTATION = 1003;
	
	public static CnSPartitionPanel getInstance() {
		if (instance == null) {
			instance = new CnSPartitionPanel("C&S Partition");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					//splitPane.setDividerLocation(0.95D);
				}
			});
		}
		return instance;
	}
	
	private CnSPartitionPanel(String title) {
		super(title);
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		CnSPanel annotationsPanel = new CnSPanel();
		annotationsPanel.setBorder(BorderFactory.createEtchedBorder());
		annotationSearchComponent = new CnSSearchAnnotationComponent(CnSPartitionPanel.SEARCH, CnSEventManager.PARTITION_PANEL, CnSPartitionPanel.ANNOTATION);
		annotationsPanel.addComponent(new JLabel("Focus on annotation :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
		annotationsPanel.addComponent(annotationSearchComponent.getTextField(), 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 5, 0, 0);
		icon_delete = new ImageIcon(getClass().getResource("/org/cytoscape/clustnsee3/internal/resources/delete_annotation.gif"));
		clearButton = new CnSButton(icon_delete);
		clearButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 6, icon_delete.getIconHeight()));
		clearButton.setFocusable(false);
		annotationsPanel.addComponent(clearButton, 2, 0, 1, 1, 0.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		addComponent(annotationsPanel, 0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		
		CnSPanel showAnnotationsPanel = new CnSPanel();
		showAnnotationsPanel.setBorder(BorderFactory.createEtchedBorder());
		showAnnotationsPanel.addComponent(new JLabel("Show"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		clusterList = new JComboBox<String>();
		CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		init(partition);
		showAnnotationsPanel.addComponent(clusterList, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		showAnnotationsPanel.addComponent(new JLabel("annotations"), 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		addComponent(showAnnotationsPanel, 1, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		
		CnSPanel exportPanel = new CnSPanel();
		exportPanel.setBorder(BorderFactory.createEtchedBorder());
		exportDataButton = new CnSButton("Export data");
		exportPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(exportPanel, 2, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 0, 5, 5, 5, 0, 0);
		
		partitionPanel = new CnSPartitionTablePanel();
		annotationPanel = new CnSAnnotationTablePanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Cluster analysis", partitionPanel);
		tabbedPane.add("Annotation term analysis", annotationPanel);
		//splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, partitionPanel, annotationPanel);
		//splitPane.setOneTouchExpandable(true);
		addComponent(tabbedPane, 0, 1, 3, 1, 1.0, 1.0, CENTER, BOTH, 0, 0, 0, 0, 0, 0);
	}
	
	public void init(CnSPartition partition) {
		clusterList.removeAllItems();
		clusterList.addItem("all");
		if (partition != null) {
			for (int i = 1; i <= partition.getClusters().size(); i++) clusterList.addItem("cluster " + i);
		}
	}
	
	private void initListeners() {
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				annotationSearchComponent.getTextField().setText("");
				annotationSearchComponent.searchForAnnotation();
			}
		});
		exportDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("CSV file (separator: tabulation)", "csv"));
				int ret = jfc.showSaveDialog(null);
				boolean tosave =false;
				File file = null;
				if (ret == JFileChooser.APPROVE_OPTION) {
					tosave =true;
					file = jfc.getSelectedFile();
					if (file.exists()) {
						ret = JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists. Are you sure you want to owerwrite it ?");
						tosave =  (ret == JOptionPane.YES_OPTION);
					}	
				}
				if (tosave) {
					try {
						BufferedWriter br= new BufferedWriter(new FileWriter(file));
						if (tabbedPane.getSelectedIndex() == 0) {
							CnSEvent ev = new CnSEvent(CnSResultsPanel.GET_SELECTED_PARTITION, CnSEventManager.RESULTS_PANEL);
							CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
							br.write("#ClustnSee cluster list");
							br.newLine();
							br.write("#Algorithm: " + partition.getAlgorithmName());
							br.newLine();
							br.write("#Network: " + partition.getInputNetwork().getRow(partition.getInputNetwork()).get(CyNetwork.NAME, String.class));
							br.newLine();
							br.write("#Scope: " + partition.getScope());
							br.newLine();
							Iterator<Integer> k = partition.getAlgorithmParameters().iterator();
							while (k.hasNext()) {
								int key = k.next();
								br.write("#Parameter: " + partition.getAlgorithmParameters().getParameter(key).getName() + "=" + partition.getAlgorithmParameters().getParameter(key).getValue());
								br.newLine();
							}
							if (partitionPanel.getSelectedAnnotation() != null) {
								br.write("#Annotation: " + partitionPanel.getSelectedAnnotation());
								br.newLine();
							}
							partitionPanel.write(br);
						}
						else if (tabbedPane.getSelectedIndex() == 1) {
							br.write("#ClustnSee annotation list");
							br.newLine();
							if (partitionPanel.getSelectedAnnotation() != null) {
								br.write("#Annotation: " + partitionPanel.getSelectedAnnotation());
								br.newLine();
							}
							br.write("#Cluster: " + clusterList.getSelectedItem());
							br.newLine();
							annotationPanel.write(br);
						}
						
						br.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		clusterList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
					ev.addParameter(CnSResultsPanel.CLUSTER_NAME, clusterList.getSelectedIndex());
					CnSEventManager.handleMessage(ev);
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		final CnSCluster cluster;
		final CnSPartition partition;
		
		switch (event.getAction()) {
			case INIT :
				System.err.println("INIT");
				//cluster = (CnSCluster)event.getParameter(CLUSTER);
				partition = (CnSPartition)event.getParameter(PARTITION);
				init(partition);
				//if (cluster != null)
				//	annotationPanel.init(cluster);
				/*else */if (partition != null) {
					partitionPanel.init(partition);
					annotationPanel.init(partition);
				}
				else
					annotationPanel.init();
				break;
			
			case CLEAR :
				partitionPanel.clear();
				annotationPanel.clear();
				break;
				
			case SELECT_CLUSTER :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partitionPanel.selectCluster(cluster);
				if (cluster != null)
					clusterList.setSelectedIndex(cluster.getID());
				else
					clusterList.setSelectedIndex(0);
				break;
				
			case SEARCH :
				System.err.println("SEARCH");
				CnSNodeAnnotation annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				cluster = partitionPanel.getSelectedCluster();
				partitionPanel.setSelectedAnnotation(annotation);
				annotationPanel.refresh();
				partitionPanel.selectCluster(cluster);
				//CnSEvent ev = new CnSEvent(CnSPartitionPanel.SET_SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL);
				//ev.addParameter(CnSPartitionPanel.ANNOTATION, annotation);
				//CnSEventManager.handleMessage(ev);
				annotationPanel.selectAnnotation(annotation);
				break;
				
			case SEARCH_ANNOTATION :
				System.err.println("SEARCH_ANNOTATION");
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				CnSEvent ev = new CnSEvent(CnSPartitionPanel.SET_SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL);
				ev.addParameter(CnSPartitionPanel.ANNOTATION, annotation);
				CnSEventManager.handleMessage(ev);
				annotationPanel.selectAnnotation(annotation);
				break;
				
			case INIT_ANNOTATION_PANEL :
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partition = (CnSPartition)event.getParameter(PARTITION);
				if (cluster != null)
					annotationPanel.init(cluster);
				else if (partition != null)
					annotationPanel.init(partition);
				else
					annotationPanel.clear();
				break;
				
			case REFRESH :
				partitionPanel.refresh();
				annotationPanel.refresh();
				break;
				
			case SET_SEARCH_ANNOTATION :
				System.err.println("SET_SEARCH_ANNOTATION");
				annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				cluster = partitionPanel.getSelectedCluster();
				System.err.println("CnSPartitionTablePanel.setAnnotation : " + annotation.getValue());
				annotationSearchComponent.setAnnotation(annotation);
				//partitionPanel.setAnnotation(annotation);
				partitionPanel.selectCluster(cluster);
				break;
				
			case GET_SELECTED_ANNOTATION :
				ret = annotationPanel.getSelectedAnnotation();
				break;
				
			case GET_SEARCHED_ANNOTATION :
				//ret = partitionPanel.getSearchedAnnotation();
				ret = annotationSearchComponent.getText();
				break;
				
			case GET_SELECTED_CLUSTER :
				ret = clusterList.getSelectedIndex();
				break;
				
			case SET_SELECTED_CLUSTER :
				int i = (int)event.getParameter(CLUSTER);
				clusterList.setSelectedIndex(i);
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
