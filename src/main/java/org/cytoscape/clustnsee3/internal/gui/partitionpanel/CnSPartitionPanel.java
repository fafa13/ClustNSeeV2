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
import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable.CnSAnnotationTablePanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTablePanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSPartitionPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -3877080938361953871L;
	private static CnSPartitionPanel instance;
	private CnSPartitionTablePanel partitionPanel;
	private CnSAnnotationTablePanel annotationPanel;
	private static JSplitPane splitPane;
	
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
	
	public static final int PARTITION = 1001;
	public static final int CLUSTER = 1002;
	public static final int ANNOTATION = 1003;
	
	public static CnSPartitionPanel getInstance() {
		if (instance == null) {
			instance = new CnSPartitionPanel("C&S Partition");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					splitPane.setDividerLocation(0.95D);
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
		partitionPanel = new CnSPartitionTablePanel();
		annotationPanel = new CnSAnnotationTablePanel();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, partitionPanel, annotationPanel);
		splitPane.setOneTouchExpandable(true);
		addComponent(splitPane, 0, 0, 1, 1, 1.0, 1.0, CENTER, BOTH, 0, 0, 0, 0, 0, 0);
	}
	
	private void initListeners() {
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
				cluster = (CnSCluster)event.getParameter(CLUSTER);
				partition = (CnSPartition)event.getParameter(PARTITION);
				if (cluster != null)
					annotationPanel.init(cluster);
				else if (partition != null) {
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
				break;
				
			case SEARCH :
				System.err.println("SEARCH");
				CnSNodeAnnotation annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				cluster = partitionPanel.getSelectedCluster();
				partitionPanel.setSelectedAnnotation(annotation);
				annotationPanel.refresh();
				partitionPanel.selectCluster(cluster);
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
				partitionPanel.setAnnotation(annotation);
				partitionPanel.selectCluster(cluster);
				break;
				
			case GET_SELECTED_ANNOTATION :
				ret = annotationPanel.getSelectedAnnotation();
				break;
				
			case GET_SEARCHED_ANNOTATION :
				ret = partitionPanel.getSearchedAnnotation();
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
