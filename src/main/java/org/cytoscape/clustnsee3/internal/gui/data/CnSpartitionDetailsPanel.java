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

package org.cytoscape.clustnsee3.internal.gui.data;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSpartitionDetailsPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -271614706912742588L;
	private static CnSpartitionDetailsPanel instance;
	public static final int CLUSTER = 1001;
	public static final int INIT = 1;
	private JLabel testLabel;
	private JTabbedPane tabbedPane;
	private JTable nodeTable, annotationTable;
	
	public CnSpartitionDetailsPanel(String title) {
		super(title);
		nodeTable = new JTable();
		annotationTable = new JTable();
		initGraphics();
	}
	public void init(CnSCluster cluster) {
		nodeTable.setModel(new CnSNodeListTableModel(cluster));
	}
	public CnSpartitionDetailsPanel() {
		initGraphics();
	}
	public static CnSpartitionDetailsPanel getInstance() {
		if (instance == null)
			instance = new CnSpartitionDetailsPanel("Partition details");
		return instance;
	}
	
	protected void initGraphics() {
		super.initGraphics();
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Node list", new JScrollPane(nodeTable));
		tabbedPane.addTab("Annotation list", new JScrollPane(annotationTable));
		testLabel = new JLabel();
		addComponent(testLabel, 0, 0, 1, 1, 0.0, 1.0, NORTH, VERTICAL, 0, 10, 10, 10, 0, 0);
		addComponent(tabbedPane, 1, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 10, 10, 10, 0, 0);
	}
	
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		switch(event.getAction()) {
		case INIT :
			CnSCluster cluster = (CnSCluster)event.getParameter(CLUSTER);
			testLabel.setText(cluster.getName());
			init(cluster);
			break;
		}
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
